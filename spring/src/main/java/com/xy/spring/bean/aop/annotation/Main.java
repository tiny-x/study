package com.xy.spring.bean.aop.annotation;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

public class Main {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
        UserService userService = context.getBean(UserService.class);
        userService.addUser();
    }

    @Configuration
    @ComponentScan("com.xy.spring.bean.aop.annotation")
    @EnableAspectJAutoProxy(proxyTargetClass = true)
    static class Config {

    }
}
