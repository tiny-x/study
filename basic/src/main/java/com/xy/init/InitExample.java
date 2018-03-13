package com.xy.init;

public class InitExample {

    private static A a1 = new A(); // 不应该这种写法

    static class A {
        static {
            // 导致读取外部类还未初始化的a
            System.out.println("a: " + InitExample.a);
        }
    }

    static {
        a = 2; // 没什么效果
        // System.out.println(a); 拒绝编译
    }

    private static int a = 1;

    static {
        System.out.println("a: " + InitExample.a);
    }



    static {
        a = 3;
    }

    int b = 1;

    public InitExample() {
        System.out.println("b: " + b);
    }

    public static void main(String[] args) {
    }
}
