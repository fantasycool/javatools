package com.frio.checker;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by frio on 17/2/22.
 */
public class Checker {
    public static List<String> attrNotNullChecker(String[] attrs, Object o){
        if (null == o) {
            throw new IllegalArgumentException("无效的入参object");
        }
        Class<?> c = o.getClass();
        ArrayList<String> resultList = new ArrayList<>();
        Field[] fields = c.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isPrivate(field.getModifiers())) {
                if(attrs == null || !Arrays.asList(attrs).contains(field.getName())){
                    field.setAccessible(true);
                    try {
                        if(field.get(o) == null){
                            resultList.add(field.getName());
                        }
                    } catch (IllegalAccessException e) {
                        continue;
                    }
                }
            }
        }
        return resultList;
    }

    public static void checkNull(Object... arg){
        for(Object o : arg){
            if(o != null && o instanceof String){
                if(StringUtils.isBlank(o.toString())){
                    throw new IllegalArgumentException();
                }
            }
            if(o == null){
                throw new IllegalArgumentException();
            }
        }
    }

    public static void checkNull(String[] description, Object... arg){
        for(int i=0; i < arg.length; i++){
            Object o = arg[i];
            if(o != null && o instanceof String){
                if(StringUtils.isBlank(o.toString())){
                    throw new IllegalArgumentException(description.length > (i + 1)? description[i] + "参数异常.": "");
                }
            }
            if(o == null){
                throw new IllegalArgumentException(description.length > (i + 1)? description[i]: "");
            }
        }
    }
}
