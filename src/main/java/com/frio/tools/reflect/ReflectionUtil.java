package com.frio.tools.reflect;

import com.frio.tools.checker.Checker;
import com.frio.tools.datetime.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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
     * 根据属性名称遍历获取bean中对应的value
     *
     * @return
     */
    public static Object getValueByNameFromBean(String name, Object bean) {
        Checker.checkNull(name, bean);
        if (name.contains("_")) {
            name = getFieldNameByColumnName(name);
        }
        Class<?> c = bean.getClass();
        for (Field field : c.getDeclaredFields()) {
            if (Modifier.isPrivate(field.getModifiers())) {
                field.setAccessible(true);
                try {
                    if (field.getName().equals(name)) {
                        return field.get(bean);
                    }
                } catch (IllegalAccessException e) {
                    continue;
                }
            }
        }
        return null;
    }

    /**
     * 拷贝m的属性到Bean的属性
     *
     * @param m
     * @param bean
     */
    public static void cloneMapValueToBean(Map<String, Object> m, Object bean) {
        cloneMapValueToBean(m, bean, null);
    }

    /**
     * 递归赋值
     *
     * @param m
     * @param bean
     * @param childDescriptor
     */
    public static void cloneMapValueToBean(Map<String, Object> m, Object bean, Map<String, Class> childDescriptor) {
        if (m == null) {
            throw new IllegalArgumentException();
        }
        List<Field> fields = getPrivateFields(bean.getClass());
        Map<String, Field> mapper = new HashMap<>();
        for (Field f : fields) {
            mapper.put(f.getName(), f);
        }
        for (Map.Entry<String, Object> entry : m.entrySet()) {
            String key = entry.getKey().contains("_") ?
                    ReflectionUtil.getFieldNameByColumnName(entry.getKey()) :
                    entry.getKey();
            Object value = entry.getValue();
            if (mapper.get(key) == null || value == null) {
                continue;
            }
            Field field = mapper.get(key);
            try {
                field.setAccessible(true);
                if (field.getType().equals(java.util.Date.class) && (value instanceof Long)) {
                    field.set(bean, new java.util.Date((long) value));
                } else if (field.getType().equals(java.util.Date.class) && (value instanceof String)) {
                    field.set(bean, DateUtil.parse(value.toString(), "yyyy-MM-dd HH:mm:ss"));
                } else if (field.getType().equals(java.lang.Integer.class)) {
                    field.set(bean, Integer.valueOf(value.toString()));
                } else if ((field.getType().equals(java.lang.Double.class) || field.getType().equals(double.class))) {
                    field.set(bean, Double.valueOf(value.toString()));
                } else if (field.getType().equals(java.lang.Float.class) || field.getType().equals(float.class)) {
                    field.set(bean, Float.valueOf(value.toString()));
                } else if (null != childDescriptor && field.getType().isAssignableFrom(Collection.class) && entry.getValue() != null) {
                    recursionSetChilds(entry, bean, field, childDescriptor);
                } else {
                    field.set(bean, value);
                }
            } catch (IllegalAccessException e) {
                LOG.error("set value, fieldName:[{}]", e, field.getName());
            } catch (Exception e) {
                LOG.error("转型有问题, field name is:[{}]", e, field.getName());
            }
        }
    }

    private static void recursionSetChilds(Map.Entry<String, Object> entry, Object bean,
                                           Field field, Map<String, Class> childDescriptor) throws InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException, NoSuchMethodException {
        Collection<Object> childs = (Collection<Object>) entry.getValue();
        Collection result;
        if (field.getClass().equals(List.class)) {
            result = new ArrayList<>();
        } else if (field.getClass().equals(Set.class)) {
            result = new HashSet<>();
        } else {
            return;
        }
        for (Object child : childs) {
            Map<String, Object> childValue = (Map<String, Object>) child;
            Class childClass = childDescriptor.get(field.getName());
            Object childBean = childClass.getConstructor().newInstance();
            ReflectionUtil.cloneMapValueToBean(childValue, childBean, null);
            result.add(childBean);
        }
        field.set(bean, result);
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

    public static String getFieldNameByColumnName(String columnName) {
        String[] columns = columnName.split("_");
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < columns.length; i++) {
            char[] c = columns[i].toCharArray();
            if (i != 0) {
                c[0] = Character.toUpperCase(c[0]);
                result.append(c);
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}