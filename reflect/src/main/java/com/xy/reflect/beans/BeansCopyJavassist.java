package com.xy.reflect.beans;

import com.xy.reflect.method.access.MethodAccessJavassist;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BeansCopyJavassist {

    private final static ConcurrentMap<Class<?>, Field[]> FIELDS_CACHE = new ConcurrentHashMap<>();

    private static Field[] setIfAbsentFileds(Class<?> aClass) {
        Field[] fields = FIELDS_CACHE.get(aClass);
        if (fields == null) {
            Field[] newFiled = aClass.getDeclaredFields();
            fields = FIELDS_CACHE.putIfAbsent(aClass, newFiled);
            if (fields == null) {
                fields = newFiled;
            }
        }
        return fields;
    }

    public static void copyProperties(Object dest, Object orig) {
        final MethodAccessJavassist destMethodAccessJavassist = MethodAccessJavassist.get(dest.getClass());
        final MethodAccessJavassist origMethodAccessJavassist = MethodAccessJavassist.get(orig.getClass());
        Field[] fields = setIfAbsentFileds(orig.getClass());

        for (Field field : fields) {
            Object param = null;
            try {
                field.setAccessible(true);
                param = field.get(orig);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (param == null)
                continue;

            Field destField = null;
            try {
                destField = dest.getClass().getDeclaredField(field.getName());
                if (destField.getType() == field.getType()) {

                    destMethodAccessJavassist.invoke(dest,
                            setMethodName(field.getName()),
                            param
                    );
                } else {
                    copyProperties(destField.getType().newInstance(), param);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String getMethodName(String fieldName) {
        char n = fieldName.charAt(0);
        return "get" + fieldName.replace(n, (char) (n - 32));
    }

    private static String setMethodName(String fieldName) {
        char n = fieldName.charAt(0);
        return "set" + fieldName.replace(n, (char) (n - 32));
    }

}
