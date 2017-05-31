package com.frio.tools.db;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.PrintStream;
import java.sql.*;
import java.sql.Date;
import java.util.*;

/**
 * JDBC tools to execute sql.
 * Created by frio on 16/6/15.
 */
public abstract class JDBCUtils {
    static Logger LOG = LoggerFactory.getLogger(JDBCUtils.class);

    /**
     * execute query, return list result
     *
     * @param dataSource
     * @param sql
     * @param parameters query parameters to be set
     * @return
     */
    public static List<Map<String, Object>> executeQuery(DataSource dataSource, String sql, List<Object> parameters) throws SQLException {
        Connection connection = null;
        List<Map<String, Object>> rows = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            connection = dataSource.getConnection();
            stmt = connection.prepareStatement(sql);
            setParameters(stmt, parameters);
            if (null != parameters) {
                rs = stmt.executeQuery();
            }
            ResultSetMetaData rsMeta = rs.getMetaData();
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 0, size = rsMeta.getColumnCount(); i < size; ++i) {
                    String columnName = rsMeta.getColumnLabel(i + 1);
                    Object value = rs.getObject(i + 1);
                    row.put(columnName, value);
                }
                rows.add(row);
            }
        } finally {
            close(rs);
            close(stmt);
            close(connection);
        }
        return rows;
    }


    public static List<Map<String, Object>> executeQuery(Connection connection, String sql, List<Object> parameters) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<>();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.prepareStatement(sql);
            setParameters(stmt, parameters);
            if (null != parameters) {
                rs = stmt.executeQuery();
            }
            ResultSetMetaData rsMeta = rs.getMetaData();
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 0, size = rsMeta.getColumnCount(); i < size; ++i) {
                    String columnName = rsMeta.getColumnLabel(i + 1);
                    Object value = rs.getObject(i + 1);
                    row.put(columnName, value);
                }
                rows.add(row);
            }
        } finally {
            close(rs);
            close(stmt);
        }
        return rows;
    }

    /**
     * execute update return take effect number
     *
     * @param dataSource
     * @param tableName
     * @param parameters
     * @return
     * @throws SQLException
     */
    public static int executeUpdateById(DataSource dataSource, String tableName, Map<String, Object> parameters, String idName, long id) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("update ").append(tableName).append(" ").append("set ");
        List<Object> args = new ArrayList<Object>();
        updateFieldAppend(parameters, sb, args);
        sb = new StringBuilder(sb.toString().substring(0, sb.length() - 1));
        sb.append(" where ").append(idName).append("=").append(id);
        LOG.info("executeUpdateById, sql:[{}]", sb.toString());
        return executeUpdate(dataSource, sb.toString(), args);
    }

    /**
     * get result by id
     * @param dataSource
     * @param id
     * @return
     */
    public static Map<String, Object> getById(DataSource dataSource, String tableName, Long id){
        List<Object> args = new ArrayList<>();
        args.add(id);
        try{
            List<Map<String, Object>> result =
                    JDBCUtils.executeQuery(dataSource, "select * from " + tableName + " where id=?", args);
            return result != null && result.size() > 0 ? result.get(0) : null;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    private static void updateFieldAppend(Map<String, Object> parameters, StringBuilder sb, List<Object> args) {
        for (int i = 0; i < parameters.keySet().size(); i++) {
            Object v = parameters.get(parameters.keySet().toArray()[i]);
            if (v != null) {
                String keyName = parameters.keySet().toArray()[i].toString();
                if(keyName.equals("key")){
                    keyName = "`" + keyName + "`";
                }
                sb.append(keyName).append("=").append("?");
                args.add(v);
                sb.append(",");
            }
        }
    }

    public static int executeUpdateById(Connection conn, String tableName, Map<String, Object> parameters, String idName, long id) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("update ").append(tableName).append(" ").append("set ");
        List<Object> args = new ArrayList<Object>();
        updateFieldAppend(parameters, sb, args);
        sb = new StringBuilder(sb.toString().substring(0, sb.length() - 1));
        sb.append(" where ").append(idName).append("=").append(id);
        LOG.info("executeUploadById sql is:[{}]", sb.toString());
        System.out.println(sb.toString());
        return executeUpdate(conn, sb.toString(), args);
    }

    public static void commit(Connection conn) {
        if (conn == null) {
            return;
        }
        try {
            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException("connection commit failed!");
        }
    }

    public static void rollback(Connection conn) {
        if (conn == null) {
            return;
        }
        try {
            conn.rollback();
        } catch (SQLException e) {
            throw new RuntimeException("connection rollback failed!");
        }
    }

    /**
     * execute update with parameters and sql
     *
     * @param dataSource
     * @param sql
     * @param parameters
     * @return
     * @throws SQLException
     */
    public static int executeUpdate(DataSource dataSource, String sql, List<Object> parameters) throws SQLException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            return executeUpdate(conn, sql, parameters);
        } finally {
            close(conn);
        }
    }

    /**
     * batch execute update with parameters and sql
     *
     * @param dataSource
     * @param sql
     * @param parameters
     * @return
     * @throws SQLException
     */
    public static List<Object> batchExecuteUpdate(DataSource dataSource, String sql, List<List<Object>> parameters) throws SQLException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            PreparedStatement stmt = null;
            int count = 0;
            return executeBatchUpdate(sql, parameters, conn, stmt, count);
        } finally {
            close(conn);
        }
    }

    private static List<Object> executeBatchUpdate(String sql, List<List<Object>> parameters, Connection conn, PreparedStatement stmt, int count) throws SQLException {
        List<Object> resultList = new ArrayList<Object>();
        for (List<Object> l : parameters) {
            try {
                stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                setParameters(stmt, l);
                stmt.addBatch();
                if (++count % 100 == 0) {
                    stmt.executeBatch();
                }
                ResultSet rs = stmt.getGeneratedKeys();
                Object next = rs.next();
                if (next != null) {
                    resultList.add(next);
                }
            } finally {
                stmt.executeBatch();
                close(stmt);
            }
        }
        return resultList;
    }

    public static List<Object> batchExecuteUpdate(Connection connection, String sql, List<List<Object>> parameters) throws SQLException {
        PreparedStatement stmt = null;
        int count = 0;
        return executeBatchUpdate(sql, parameters, connection, stmt, count);
    }

    /**
     * execute insert with map data
     *
     * @param dataSource
     * @param tableName
     * @param data
     * @throws SQLException
     */
    public static long insertToTable(DataSource dataSource, String tableName, Map<String, Object> data)
            throws SQLException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            return insertToTable(conn, tableName, data);
        } finally {
            close(conn);
        }
    }

    /**
     * execute insert with map data
     *
     * @param dataSource
     * @param tableName
     * @param data
     * @throws SQLException
     */
    public static Long insertToTableGetId(DataSource dataSource, String tableName, Map<String, Object> data)
            throws SQLException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            return insertToTable(conn, tableName, data);
        } finally {
            close(conn);
        }
    }

    /**
     * execute batch insert with map data
     *
     * @param dataSource
     * @param tableName
     * @param data
     * @throws SQLException
     */
    public static void insertToTable(DataSource dataSource, String tableName, List<Map<String, Object>> data)
            throws SQLException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            insertToTable(conn, tableName, data);
        } finally {
            close(conn);
        }
    }

    public static void batchInsertToTable(DataSource dataSource, String tableName, List<Map<String, Object>> data) throws SQLException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            insertToTable(conn, tableName, data);
        } finally {
            close(conn);
        }
    }

    public static long insertToTable(Connection conn, String tableName, Map<String, Object> data) throws SQLException {
        String sql = makeInsertToTableSql(tableName, data.keySet());
        List<Object> parameters = new ArrayList<Object>(data.values());
        return executeGetLastId(conn, sql, parameters);
    }

    public static void insertToTable(Connection conn, String tableName, List<Map<String, Object>> data) throws SQLException {
        String sql = makeInsertToTableSql(tableName, data.get(0).keySet());
        int count = 0;
        PreparedStatement stmt = conn.prepareStatement(sql);
        try {
            for (Map<String, Object> m : data) {
                setParameters(stmt, new ArrayList<Object>(m.values()));
                stmt.addBatch();
                if (++count % 100 == 0) {
                    stmt.executeBatch();
                }
            }
            stmt.executeBatch();
        } finally {
            close(stmt);
        }
    }

    public static String makeInsertToTableSql(String tableName, Collection<String> names) {
        if (names.size() == 0) {
            return "insert into " + tableName + "(id) values (null)";
        }
        StringBuilder sql = new StringBuilder() //
                .append("insert into ") //
                .append(tableName) //
                .append("("); //

        int nameCount = 0;
        for (String name : names) {
            if (nameCount > 0) {
                sql.append(",");
            }
            sql.append(name);
            nameCount++;
        }
        sql.append(") values (");
        for (int i = 0; i < nameCount; ++i) {
            if (i != 0) {
                sql.append(",");
            }
            sql.append("?");
        }
        sql.append(")");

        return sql.toString();
    }

    public static void execute(Connection conn, String sql, List<Object> parameters) throws SQLException {
        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(sql);

            setParameters(stmt, parameters);

            stmt.executeUpdate();
        } finally {
            close(stmt);
        }
    }

    public static long executeGetLastId(Connection conn, String sql, List<Object> parameters) throws SQLException {
        PreparedStatement stmt = null;

        try {
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            setParameters(stmt, parameters);

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } finally {
            close(stmt);
        }
    }

    public static int executeUpdate(Connection conn, String sql, List<Object> parameters) throws SQLException {
        PreparedStatement stmt = null;

        int updateCount;
        try {
            stmt = conn.prepareStatement(sql);

            setParameters(stmt, parameters);

            updateCount = stmt.executeUpdate();
        } finally {
            close(stmt);
        }
        return updateCount;
    }

    private static void setParameters(PreparedStatement stmt, List<Object> parameters) throws SQLException {
        for (int i = 0, size = parameters.size(); i < size; ++i) {
            Object param = parameters.get(i);
            stmt.setObject(i + 1, param);
        }
    }

    public static void close(Connection x) {
        if (x == null) {
            return;
        }
        try {
            x.close();
        } catch (Exception e) {
            throw new RuntimeException("close sql connection failed!", e);
        }
    }

    public static void close(Statement x) {
        if (x == null) {
            return;
        }
        try {
            x.close();
        } catch (Exception e) {
            throw new RuntimeException("close sql statement exception failed", e);
        }
    }

    public static void close(ResultSet x) {
        if (x == null) {
            return;
        }
        try {
            x.close();
        } catch (Exception e) {
            throw new RuntimeException("close result set failed", e);
        }
    }

    public static void printResultSet(ResultSet rs, PrintStream out, boolean printHeader, String seperator) throws SQLException {
        ResultSetMetaData metadata = rs.getMetaData();
        int columnCount = metadata.getColumnCount();
        if (printHeader) {
            for (int columnIndex = 1; columnIndex <= columnCount; ++columnIndex) {
                if (columnIndex != 1) {
                    out.print(seperator);
                }
                out.print(metadata.getColumnName(columnIndex));
            }
        }

        out.println();

        while (rs.next()) {

            for (int columnIndex = 1; columnIndex <= columnCount; ++columnIndex) {
                if (columnIndex != 1) {
                    out.print(seperator);
                }

                int type = metadata.getColumnType(columnIndex);

                if (type == Types.VARCHAR || type == Types.CHAR || type == Types.NVARCHAR || type == Types.NCHAR) {
                    out.print(rs.getString(columnIndex));
                } else if (type == Types.DATE) {
                    Date date = rs.getDate(columnIndex);
                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(date.toString());
                    }
                } else if (type == Types.BIT) {
                    boolean value = rs.getBoolean(columnIndex);
                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(Boolean.toString(value));
                    }
                } else if (type == Types.BOOLEAN) {
                    boolean value = rs.getBoolean(columnIndex);
                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(Boolean.toString(value));
                    }
                } else if (type == Types.TINYINT) {
                    byte value = rs.getByte(columnIndex);
                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(Byte.toString(value));
                    }
                } else if (type == Types.SMALLINT) {
                    short value = rs.getShort(columnIndex);
                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(Short.toString(value));
                    }
                } else if (type == Types.INTEGER) {
                    int value = rs.getInt(columnIndex);
                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(Integer.toString(value));
                    }
                } else if (type == Types.BIGINT) {
                    long value = rs.getLong(columnIndex);
                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(Long.toString(value));
                    }
                } else if (type == Types.TIMESTAMP) {
                    out.print(String.valueOf(rs.getTimestamp(columnIndex)));
                } else if (type == Types.DECIMAL) {
                    out.print(String.valueOf(rs.getBigDecimal(columnIndex)));
                } else if (type == Types.CLOB) {
                    out.print(String.valueOf(rs.getString(columnIndex)));
                } else if (type == Types.JAVA_OBJECT) {
                    Object object = rs.getObject(columnIndex);

                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(String.valueOf(object));
                    }
                } else if (type == Types.LONGVARCHAR) {
                    Object object = rs.getString(columnIndex);

                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        out.print(String.valueOf(object));
                    }
                } else if (type == Types.NULL) {
                    out.print("null");
                } else {
                    Object object = rs.getObject(columnIndex);

                    if (rs.wasNull()) {
                        out.print("null");
                    } else {
                        if (object instanceof byte[]) {
                            byte[] bytes = (byte[]) object;
                            String text = HexBin.encode(bytes);
                            out.print(text);
                        } else {
                            out.print(String.valueOf(object));
                        }
                    }
                }
            }
            out.println();
        }
    }

    public static String getTypeName(int sqlType) {
        switch (sqlType) {
            case Types.ARRAY:
                return "ARRAY";

            case Types.BIGINT:
                return "BIGINT";

            case Types.BINARY:
                return "BINARY";

            case Types.BIT:
                return "BIT";

            case Types.BLOB:
                return "BLOB";

            case Types.BOOLEAN:
                return "BOOLEAN";

            case Types.CHAR:
                return "CHAR";

            case Types.CLOB:
                return "CLOB";

            case Types.DATALINK:
                return "DATALINK";

            case Types.DATE:
                return "DATE";

            case Types.DECIMAL:
                return "DECIMAL";

            case Types.DISTINCT:
                return "DISTINCT";

            case Types.DOUBLE:
                return "DOUBLE";

            case Types.FLOAT:
                return "FLOAT";

            case Types.INTEGER:
                return "INTEGER";

            case Types.JAVA_OBJECT:
                return "JAVA_OBJECT";

            case Types.LONGNVARCHAR:
                return "LONGNVARCHAR";

            case Types.LONGVARBINARY:
                return "LONGVARBINARY";

            case Types.NCHAR:
                return "NCHAR";

            case Types.NCLOB:
                return "NCLOB";

            case Types.NULL:
                return "NULL";

            case Types.NUMERIC:
                return "NUMERIC";

            case Types.NVARCHAR:
                return "NVARCHAR";

            case Types.REAL:
                return "REAL";

            case Types.REF:
                return "REF";

            case Types.ROWID:
                return "ROWID";

            case Types.SMALLINT:
                return "SMALLINT";

            case Types.SQLXML:
                return "SQLXML";

            case Types.STRUCT:
                return "STRUCT";

            case Types.TIME:
                return "TIME";

            case Types.TIMESTAMP:
                return "TIMESTAMP";

            case Types.TINYINT:
                return "TINYINT";

            case Types.VARBINARY:
                return "VARBINARY";

            case Types.VARCHAR:
                return "VARCHAR";

            default:
                return "OTHER";

        }
    }

    /**
     * List arguments
     *
     * @param entryFlag
     * @return
     */
    public static String generateListTypeArgs(Object[] entryFlag) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (int i = 0; i < entryFlag.length; i++) {
            sb.append("?");
            if (i < entryFlag.length - 1) {
                sb.append(",");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    public static String generateListInArgs(Object[] args){
        StringBuilder result = new StringBuilder();
        result.append("(");
        for(int i = 0; i < args.length; i++){
            result.append(args[i].toString());
            if(i < args.length - 1){
                result.append(",");
            }
        }
        result.append(")");
        return result.toString();
    }
}