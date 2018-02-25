package com.xy.reflect;

import net.sf.cglib.reflect.FastClass;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Cglib {

    private final ConcurrentMap<Class<?>, FastClass> cache = new ConcurrentHashMap<>();

    private final HelloService helloService = new HelloService();

    public void invoke() throws InvocationTargetException {
        FastClass fastClass = cache.get(HelloService.class);
        if (fastClass == null) {
            FastClass newFastClass = FastClass.create(HelloService.class);
            fastClass = cache.putIfAbsent(HelloService.class, newFastClass);
            if (fastClass == null) {
                fastClass = newFastClass;
            }
        }

        fastClass.invoke("hello",
                new Class[]{String.class},
                helloService,
                new Object[]{"cgl"});
    }
}
