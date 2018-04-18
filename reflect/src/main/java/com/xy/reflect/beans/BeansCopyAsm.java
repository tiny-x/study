package com.xy.reflect.beans;

import com.xy.reflect.method.access.MethodAccessAsm;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BeansCopyAsm {

    private final static ConcurrentMap<Class<?>, MethodAccessAsm> METHOD_CACHE = new ConcurrentHashMap<>();

    private final static ConcurrentMap<Class<?>, Field[]> FIELDS_CACHE = new ConcurrentHashMap<>();

    private static MethodAccessAsm setIfAbsentMethodAccessAsm(Class<?> aClass) {
        MethodAccessAsm MethodAccessAsm = METHOD_CACHE.get(aClass);
        if (MethodAccessAsm == null) {
            MethodAccessAsm newMethodAccessAsm = MethodAccessAsm.get(aClass);
            MethodAccessAsm = METHOD_CACHE.putIfAbsent(aClass, newMethodAccessAsm);
            if (MethodAccessAsm == null) {
                MethodAccessAsm = newMethodAccessAsm;
            }
        }
        return MethodAccessAsm;
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
        final MethodAccessAsm destMethodAccessAsm = setIfAbsentMethodAccessAsm(dest.getClass());
        final MethodAccessAsm origMethodAccessAsm = setIfAbsentMethodAccessAsm(orig.getClass());
        Field[] fields = setIfAbsentFileds(orig.getClass());

        for (Field field : fields) {
            Object param = origMethodAccessAsm.invoke(orig, getMethodName(field.getName()));
            destMethodAccessAsm.invoke(dest, setMethodName(field.getName()), param);
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
