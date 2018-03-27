package com.xy.reflect;

import com.xy.reflect.method.access.MethodAccess;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ReflectJavassist {

    private final ConcurrentMap<Class<?>, MethodAccess> cache = new ConcurrentHashMap<>();

    public static void main(String[] args) throws Exception {
        HelloService helloService = new HelloService();
        ReflectJavassist reflectJavassist = new ReflectJavassist();
        System.out.println(reflectJavassist.invoke(helloService));
    }

    public String invoke(HelloService helloService) {
        MethodAccess methodAccess = cache.get(HelloService.class);
        if (methodAccess == null) {
            MethodAccess newMethodAccess = MethodAccess.get(HelloService.class);
            methodAccess = cache.putIfAbsent(HelloService.class, newMethodAccess);
            if (methodAccess == null) {
                methodAccess = newMethodAccess;
            }
        }
        return (String) methodAccess.invoke(helloService, "hello", "javassist");
    }

}
