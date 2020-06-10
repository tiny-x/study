package com.xy.spring.bean.lifecycle;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");

        context.start();
        context.stop();
        context.close();

//        Object cycle = context.getBean("cycle");
//        System.out.println(cycle);
//
//        context.destroy();
//        System.in.read();
    }
}
