package com.xy.reflect.beans;

import com.xy.reflect.method.access.MethodAccessAsm;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BeansCopyAsm {

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
        final MethodAccessAsm destMethodAccessAsm = MethodAccessAsm.get(dest.getClass());
        final MethodAccessAsm origMethodAccessAsm = MethodAccessAsm.get(orig.getClass());
        Field[] fields = setIfAbsentFileds(orig.getClass());

        for (Field field : fields) {
            destMethodAccessAsm.invoke(dest,
                    setMethodName(field.getName()),
                    origMethodAccessAsm.invoke(orig, getMethodName(field.getName())));
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
