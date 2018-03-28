package com.xy.reflect;

import com.esotericsoftware.reflectasm.MethodAccess;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ReflectAsm {

    private final ConcurrentMap<Class<?>, MethodAccess> cache = new ConcurrentHashMap<>();

    public String invoke(HelloService helloService) {
        MethodAccess methodAccess = cache.get(HelloService.class);
        if (methodAccess == null) {
            MethodAccess newMethodAccess = MethodAccess.get(HelloService.class);
            methodAccess = cache.putIfAbsent(HelloService.class, newMethodAccess);
            if (methodAccess == null) {
                methodAccess = newMethodAccess;
            }
        }
        return (String) methodAccess.invoke(helloService, "hello", "asm");
    }

    public static void main(String[] args) {
        ReflectAsm reflectAsm = new ReflectAsm();
        reflectAsm.invoke(new HelloService());
        System.out.println(HelloService.class.getSimpleName());
        System.out.println(HelloService.class.getName());

        for (Method method : ReflectAsm.class.getDeclaredMethods()) {
            System.out.println(method.getName() + "-->" + org.objectweb.asm.Type.getMethodDescriptor(method));
        }
    }
}
