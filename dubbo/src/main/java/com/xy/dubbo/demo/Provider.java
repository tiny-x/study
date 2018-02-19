package com.xy.dubbo.demo;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ServiceConfig;

import java.io.IOException;

public class Provider {

    public static void main(String[] args) throws IOException {

        // 服务实现
        HelloService helloService = new HelloServiceImpl();

        // 当前应用配置
        ApplicationConfig application = new ApplicationConfig();
        application.setName("provider");

        // 服务提供者暴露服务配置
        ServiceConfig<HelloService> service = new ServiceConfig<HelloService>(); // 此实例很重，封装了与注册中心的连接，请自行缓存，否则可能造成内存和连接泄漏
        service.setApplication(application);
        service.setInterface(HelloService.class);
        service.setRegister(false);
        service.setRef(helloService);
        service.setVersion("1.0.0");

        // 暴露及注册服务
        service.export();

        System.in.read();
    }
}
