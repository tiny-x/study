package com.xy.reflect;

import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.matcher.ElementMatchers;

import java.util.concurrent.Callable;

public class ByteBuddy {

    public static <T> T proxy(Class<T> interfaceType, Object handler) {

        Class<? extends T> cls = new net.bytebuddy.ByteBuddy()
                .subclass(interfaceType)
                .method(ElementMatchers.isDeclaredBy(interfaceType))
                .intercept(MethodDelegation.to(handler, "handler"))
                .make()
                .load(interfaceType.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                .getLoaded();

        try {
            return cls.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    static class ByteBuddyProxyHandler {

        @SuppressWarnings("unused")
        @RuntimeType
        public Object invoke(@SuperCall Callable<Object> superMethod, @AllArguments @RuntimeType Object[] args)
                throws Throwable {

            return superMethod.call();
        }
    }

    private static HelloService proxy = ByteBuddy.proxy(HelloService.class, new ByteBuddyProxyHandler());

    public String invoke() {
        return proxy.hello("bud");
    }

    public static void main(String[] args) {
        ByteBuddy byteBuddy = new ByteBuddy();
        String invoke = byteBuddy.invoke();
        System.out.println(invoke);
    }
}
