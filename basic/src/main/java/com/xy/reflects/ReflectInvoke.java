package com.xy.reflects;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectInvoke {

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        /**
         * 获取子类和子类继承的 所以公共方法
         */
        Method[] methods = User.class.getMethods();
        for (Method method : methods) {
            System.out.println(method.getName());
        }

        /**
         * 获取子类的方法含私有方法
         */
        System.out.println("--------------------------");
        Method[] declaredMethods = User.class.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            System.out.println(declaredMethod.getName());
        }

        /**
         * Method 每次调用会返回新的对象，Method应该需要缓存，否则性能下降不少。
         */
        Method getName = User.class.getDeclaredMethod("getName", String.class);
        User obj = new User();

        /**
         * 反射调用
         *  私有方法会进行一系列检查，setAccessible(true) 会绕过检查 提高一定的性能
         *
         *  生成 methodAccessor invoke --》 native 方法调用
         *
         *  执行 超过15次（默认），字节码生成 MethodAccessorImpl，类加载（每次都生成新的类加载器，考虑到的类的卸载），
         *  实例调用，应该会快很多了。
         *
         *  ****反射使得JIT编译器无法有效做优化确实是反射慢的主要原因。
         *
         */
        String a = (String) getName.invoke(obj, "a");
        System.out.println(a);
    }


    static class User {

        private String name = "xiaomin";

        private String getNamePrivate() {
            return name;
        }

        public String getName(String name) {
            return this.name + name;
        }
    }
}
