package com.xy.spring.bean.post.process;

import com.xy.spring.bean.post.process.Bean.EnableDataSource;
import com.xy.spring.bean.post.process.Bean.MessageService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

public class Main {

    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext configApplicationContext = new AnnotationConfigApplicationContext(Config.class);

        MessageService messageService = (MessageService) configApplicationContext.getBean("messageService");
        messageService.sendMessage();

        System.in.read();
    }

    @Configuration
    @ComponentScan("com.xy.spring.bean.post.process")
    @EnableDataSource
    static class Config {

    }
}
