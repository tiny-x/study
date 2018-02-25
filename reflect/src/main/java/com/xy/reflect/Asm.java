package com.xy.reflect;

import com.esotericsoftware.reflectasm.MethodAccess;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Asm {

    private final ConcurrentMap<Class<?>, MethodAccess> cache = new ConcurrentHashMap<>();

    private final HelloService helloService = new HelloService();

    public void invoke() {
        MethodAccess methodAccess = cache.get(HelloService.class);
        if (methodAccess == null) {
            MethodAccess newMethodAccess = MethodAccess.get(HelloService.class);
            methodAccess = cache.putIfAbsent(HelloService.class, newMethodAccess);
            if (methodAccess == null) {
                methodAccess = newMethodAccess;
            }
        }
        methodAccess.invoke(helloService, "hello", "asm");
    }
}
