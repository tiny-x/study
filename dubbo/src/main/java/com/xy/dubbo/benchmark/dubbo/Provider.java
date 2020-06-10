package com.xy.dubbo.benchmark.dubbo;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;
import com.xy.dubbo.demo.HelloService;
import com.xy.dubbo.demo.HelloServiceImpl;

import java.io.IOException;

public class Provider {

    public static void main(String[] args) throws IOException {

        // 服务实现
        HelloService helloService = new HelloServiceImpl();

        // 当前应用配置
        ApplicationConfig application = new ApplicationConfig();
        application.setName("provider");

        // 服务提供者暴露服务配置
        ServiceConfig<HelloService> service = new ServiceConfig<>();
        service.setApplication(application);
        service.setInterface(HelloService.class);
        RegistryConfig registry = new RegistryConfig();
        registry.setAddress("N/A");
        service.setRegistry(registry);
        service.setRef(helloService);
        service.setVersion("1.0.0");
        service.setConnections(4);

        // 暴露及注册服务
        service.export();

        System.in.read();
    }
}
