package com.xy.spring.bean.event;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("refresh.xml");
        context.start();
        context.publishEvent(new EventBean1.AEvent(context));

        System.in.read();
    }
}
