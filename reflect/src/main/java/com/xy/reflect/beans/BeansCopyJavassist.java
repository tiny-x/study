package com.xy.reflect.beans;

import com.xy.reflect.method.access.MethodAccessJavassist;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BeansCopyJavassist {

    private final static ConcurrentMap<Class<?>, MethodAccessJavassist> METHOD_CACHE = new ConcurrentHashMap<>();

    private final static ConcurrentMap<Class<?>, Field[]> FIELDS_CACHE = new ConcurrentHashMap<>();

    private static MethodAccessJavassist setIfAbsentMethodAccess(Class<?> aClass) {
        MethodAccessJavassist methodAccessJavassist = METHOD_CACHE.get(aClass);
        if (methodAccessJavassist == null) {
            MethodAccessJavassist newMethodAccessJavassist = MethodAccessJavassist.get(aClass);
            methodAccessJavassist = METHOD_CACHE.putIfAbsent(aClass, newMethodAccessJavassist);
            if (methodAccessJavassist == null) {
                methodAccessJavassist = newMethodAccessJavassist;
            }
        }
        return methodAccessJavassist;
    }

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
        final MethodAccessJavassist destMethodAccessJavassist = setIfAbsentMethodAccess(dest.getClass());
        final MethodAccessJavassist origMethodAccessJavassist = setIfAbsentMethodAccess(orig.getClass());
        Field[] fields = setIfAbsentFileds(orig.getClass());

        for (Field field : fields) {
            Object param = origMethodAccessJavassist.invoke(orig, getMethodName(field.getName()));
            destMethodAccessJavassist.invoke(dest, setMethodName(field.getName()), param);
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
