package com.frio.reflect;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by frio on 16/7/30.
 */
public class Reloader {
    public static Map<String, Object> reloadObjectReadyForDb(Object o, String[] ignoreAttrs) {
        Map<String, Object> dbArgs = new HashMap<>();
        if (null == o) {
            throw new IllegalArgumentException("无效的入参object");
        }
        Class<?> c = o.getClass();
        Field[] fields = c.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isPrivate(field.getModifiers())) {
                if (ignoreAttrs == null || !Arrays.asList(ignoreAttrs).contains(field.getName())) {
                    field.setAccessible(true);
                    try {
                        if (field.get(o) != null) {
                            if ((field.get(o) instanceof String)) {
                                if (StringUtils.isNotBlank(field.get(o).toString())) {
                                    dbArgs.put(ReflectionUtil.getColumnNameByField(field.getName()), field.get(o));
                                }
                            } else {
                                dbArgs.put(ReflectionUtil.getColumnNameByField(field.getName()), field.get(o));
                            }
                        }
                    } catch (IllegalAccessException e) {
                        continue;
                    }
                }
            }
        }
        return dbArgs;
    }
}