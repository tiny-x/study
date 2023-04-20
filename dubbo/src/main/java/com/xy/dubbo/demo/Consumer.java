package com.xy.dubbo.demo;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.example.demo.service.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class Consumer {

    public static void main(String[] args) throws Exception {
        Logger logger = LoggerFactory.getLogger(Consumer.class);
        // 当前应用配置
        ApplicationConfig application = new ApplicationConfig();
        application.setName("yyy");

        // 引用远程服务
        ReferenceConfig<HelloService> reference = new ReferenceConfig<HelloService>(); // 此实例很重，封装了与注册中心的连接以及与提供者的连接，请自行缓存，否则可能造成内存和连接泄漏
        reference.setApplication(application);
        reference.setInterface(HelloService.class);
        reference.setVersion("1.0.0");
        reference.setUrl("dubbo://10.10.225.128:30000");
        reference.setTimeout(30009);
        //reference.setRegistry(new RegistryConfig("zookeeper://10.10.225.128:2181"));

        // 和本地bean一样使用xxxService
        HelloService helloService = reference.get();

        long l = System.currentTimeMillis();
        for (int i = 0; i < 20000; i++) {
            TimeUnit.SECONDS.sleep(2);
            long start = System.currentTimeMillis();
            String s = helloService.sayHello(" biu biu biu!!!");
            System.out.println("耗时：" + (System.currentTimeMillis() - start) + s);
            //System.out.printf("---------->: receive provider message %s \n", s);
        }
        System.out.printf("耗时 %s \n", System.currentTimeMillis() - l);
    }
}
