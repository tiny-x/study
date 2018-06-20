package com.xy.string;

public class StringExample {

    public static void main(String[] args) {
        String s0 = new String("cba");
        String s1 = new String("cba");
        String s2 = "cba";

        System.out.println(s0 == s1);
        System.out.println(s0 == s2);
        System.out.println(s0.intern() == s2);
    }
}
