package com.xy.spring.bean.event;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class Main {

    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext configApplicationContext = new AnnotationConfigApplicationContext(Config.class);
        System.in.read();
    }

    @Configuration
    static class Config {

        @Bean
        public ContextRefreshedEventListener get() {
            return new ContextRefreshedEventListener();
        }

        @Bean
        public AEventListener get1() {
            return new AEventListener();
        }
    }
}
