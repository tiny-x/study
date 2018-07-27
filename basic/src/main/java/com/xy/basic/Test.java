package com.xy.basic;

import java.io.FileOutputStream;
import java.io.OutputStream;

public class Test {

    public static void main(String[] args) {
        int a = 0B0010;
        int b = 0B0000_1000;

        System.out.println(a);
        System.out.println(b);

        try (OutputStream stream = new FileOutputStream("a")) {

        } catch (Exception e) {

        }
        System.out.println(0xFF);
    }
}
