package com.xy.classanalysis;

/**
 * @author yefei
 * @date 2018-05-24 11:08
 */
public class Invoke {

    public static void main(String[] args) {
        Invoke invoke = new Invoke();
        int b;
        b = invoke.add(10);
        System.out.println(0);
    }

    public int add(int a) {
        return a++;
    }
}
