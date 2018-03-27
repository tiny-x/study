package com.xy.reflect;

public class HelloService {

    public String hello(String name) {
        return name;
    }

    public String hi(String name) {
        System.out.println("invoke hi() , param [" + name + "]");
        return name;
    }
}
