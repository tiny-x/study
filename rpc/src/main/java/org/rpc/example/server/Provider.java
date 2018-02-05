package org.rpc.example.server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author yefei
 * @date 2017-06-20 17:06
 */
public class Provider {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:spring-provider.xml");
        context.start();
    }
}
