package com.xy.reflect;

import com.xy.reflect.method.access.MethodAccessJavassist;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ReflectJavassist {

    private final ConcurrentMap<Class<?>, MethodAccessJavassist> cache = new ConcurrentHashMap<>();

    public static void main(String[] args) throws Exception {
        HelloService helloService = new HelloService();
        ReflectJavassist reflectJavassist = new ReflectJavassist();
        System.out.println(reflectJavassist.invoke(helloService));
    }

    public String invoke(HelloService helloService) {
        MethodAccessJavassist methodAccessJavassist = cache.get(HelloService.class);
        if (methodAccessJavassist == null) {
            MethodAccessJavassist newMethodAccessJavassist = MethodAccessJavassist.get(HelloService.class);
            methodAccessJavassist = cache.putIfAbsent(HelloService.class, newMethodAccessJavassist);
            if (methodAccessJavassist == null) {
                methodAccessJavassist = newMethodAccessJavassist;
            }
        }
        return (String) methodAccessJavassist.invoke(helloService, "hello", "javassist");
    }

}
