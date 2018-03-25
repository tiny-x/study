package com.xy.reflect;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ReflectJava {

    private final ConcurrentMap<String, Method> cache = new ConcurrentHashMap<>();

    public String invoke(HelloService helloService) {
        try {
            Method method = cache.get("hello");
            if (method == null) {
                Method hello = HelloService.class.getMethod("hello", String.class);
                method = cache.putIfAbsent("hello", hello);
                if (method == null) {
                    method = hello;
                }
            }
            return (String) method.invoke(helloService, "java");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        ReflectJava reflectJava = new ReflectJava();
        String invoke = reflectJava.invoke(new HelloService());
        System.out.println(invoke);
    }
}
