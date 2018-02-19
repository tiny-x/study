package com.xy.dubbo.demo;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;

public class Consumer {

    public static void main(String[] args) {
        // 当前应用配置
        ApplicationConfig application = new ApplicationConfig();
        application.setName("yyy");

        // 引用远程服务
        ReferenceConfig<HelloService> reference = new ReferenceConfig<HelloService>(); // 此实例很重，封装了与注册中心的连接以及与提供者的连接，请自行缓存，否则可能造成内存和连接泄漏
        reference.setApplication(application);
        reference.setInterface(HelloService.class);
        reference.setVersion("1.0.0");
        reference.setUrl("dubbo://127.0.0.1:20880");

        // 和本地bean一样使用xxxService
        HelloService helloService = reference.get();

        long l = System.currentTimeMillis();
        for (int i = 0; i < 20000; i++) {
            String s = helloService.sayHello(" biu biu biu!!!");
            //System.out.printf("---------->: receive provider message %s \n", s);
        }
        System.out.printf("耗时 %s \n", System.currentTimeMillis() - l);
    }
}
