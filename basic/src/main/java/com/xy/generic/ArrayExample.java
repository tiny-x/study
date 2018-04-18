package com.xy.generic;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author yefei
 * @date 2018-04-18 16:20
 */
public class ArrayExample {

    public static void main(String[] args) {
        /**
         * 数组协变类型
         * 运行时检查类型转换
         */
        Object[] a = new B[1];
        a[0] = "a";
        System.out.println(Arrays.toString(a));

        ArrayList<String>[] arrayLists = new ArrayList[1];

    }

    static class A {

    }

    static class B extends A {

    }
}
