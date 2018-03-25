package com.xy.reflect;

import net.sf.cglib.reflect.FastClass;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ReflectFastClass {

    private final ConcurrentMap<Class<?>, FastClass> cache = new ConcurrentHashMap<>();

    public String invoke(HelloService helloService) {
        FastClass fastClass = cache.get(HelloService.class);
        if (fastClass == null) {
            FastClass newFastClass = FastClass.create(HelloService.class);
            fastClass = cache.putIfAbsent(HelloService.class, newFastClass);
            if (fastClass == null) {
                fastClass = newFastClass;
            }
        }
        try {
            return (String) fastClass.invoke("hello",
                    new Class[]{String.class},
                    helloService,
                    new Object[]{"cgl"});
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        ReflectFastClass reflectFastClass = new ReflectFastClass();
        System.out.println(reflectFastClass.invoke(new HelloService()));
    }
}
