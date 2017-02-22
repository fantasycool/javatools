package com.frio.tools.reflect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by frio on 16/6/29.
 */
public class ReflectionUtil {
    private static Logger LOG = LoggerFactory.getLogger(ReflectionUtil.class);

    /**
     * 獲取Class的私有屬性
     *
     * @param theClass
     * @return
     */
    public static List<Field> getPrivateFields(Class<?> theClass) {
        List<Field> privateFields = new ArrayList<>();

        Field[] fields = theClass.getDeclaredFields();

        for (Field field : fields) {
            if (Modifier.isPrivate(field.getModifiers())) {
                privateFields.add(field);
            }
        }
        return privateFields;
    }

    /**
     * 拷贝m的属性到Bean的属性
     * @param m
     * @param bean
     */
    public static void cloneMapValueToBean(Map<String, Object> m, Object bean){
        if(m == null){
            throw new IllegalArgumentException();
        }
        List<Field> fields = getPrivateFields(bean.getClass());
        Map<String, Field> mapper = new HashMap<>();
        for(Field f : fields){
            mapper.put(f.getName(), f);
        }
        for(Map.Entry<String, Object> entry : m.entrySet()){
            String key = ReflectionUtil.getFieldNameByColumnName(entry.getKey());
            Object value = entry.getValue();
            if(mapper.get(key) == null || value == null){
                continue;
            }
            Field field = mapper.get(key);
            try {
                field.setAccessible(true);
                if(field.getType().equals(java.util.Date.class) && (value instanceof Long)) {
                    field.set(bean, new java.util.Date((long) value));
                }else if(field.getType().equals(java.util.Date.class) && (value instanceof String)) {
                    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date d = sf.parse(value.toString());
                    field.set(bean, d);
                }else if(field.getType().equals(java.lang.Integer.class)) {
                    field.set(bean, Integer.valueOf(value.toString()));
                }else if((field.getType().equals(java.lang.Double.class) || field.getType().equals(double.class))){
                    field.set(bean, Double.valueOf(value.toString()));
                }else if(field.getType().equals(java.lang.Float.class) || field.getType().equals(float.class)){
                    field.set(bean, Float.valueOf(value.toString()));
                }else{
                    field.set(bean, value);
                }
            } catch (IllegalAccessException e) {
                LOG.error("set value, fieldName:[{}]", e, field.getName());
            } catch (ParseException e) {
                LOG.error("data parse failed, fieldName:[{}]", e, field.getName());
            }catch(Exception e){
                LOG.error("转型有问题, field name is:[{}]", e, field.getName());
            }
        }
    }

    /**
     * convert like this:
     * eg:userId -> user_id
     *
     * @param fieldName
     * @return
     */
    public static String getColumnNameByField(String fieldName) {
        char[] chars = fieldName.toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : chars) {
            if (!Character.isLowerCase(c)) {
                stringBuilder.append('_');
                stringBuilder.append(Character.toLowerCase(c));
            } else {
                stringBuilder.append(c);
            }
        }
        return stringBuilder.toString();
    }

    public static String getFieldNameByColumnName(String columnName){
        String[] columns = columnName.split("_");
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < columns.length; i ++){
            char[] c = columns[i].toCharArray();
            if(i != 0){
                c[0] = Character.toUpperCase(c[0]);
                result.append(c);
            }else{
                result.append(c);
            }
        }
        return result.toString();
    }
}