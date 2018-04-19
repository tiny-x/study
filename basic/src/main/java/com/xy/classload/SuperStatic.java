package com.xy.classload;

import org.junit.Test;

public class SuperStatic {

    @Test
    public void main() {
        /**
         * getstatic 指令导致类加载
         *
         * getstatic 父类 不会使子类加载
         */
        System.out.println(Son.type);
    }

    @Test
    public void array() {
        /**
         * newarray 指令
         *
         * new 数组不会类加载
         */
        Son[] sons = new Son[10];
    }

    @Test
    public void finalTest() {
        /**
         * protected final static String VALUE = "son";
         * 已经编译时生成常量 存到了 SuperStatic 常量池
         */
        System.out.println(Son.VALUE);
    }

    static class Father {

        protected static String type = "father";

        static {
            System.out.println("the type is:" + type);
        }
    }

    static class Son extends Father {

        protected final static String VALUE = "son";

        static {
            System.out.println("the type is: son");
        }
    }
}
