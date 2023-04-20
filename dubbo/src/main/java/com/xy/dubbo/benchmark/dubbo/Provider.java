package com.xy.dubbo.benchmark.dubbo;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.MethodConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;
import com.example.demo.service.HelloService;
import com.xy.dubbo.demo.HelloServiceImpl;

import java.io.IOException;
import java.util.ArrayList;

public class Provider {

    public static void main(String[] args) throws IOException {

        // 服务实现
        HelloService helloService = new HelloServiceImpl();

        // 当前应用配置
        ApplicationConfig application = new ApplicationConfig();
        application.setName("provider");

        // 服务提供者暴露服务配置
        ServiceConfig<HelloService> service = new ServiceConfig<>();
        service.setGeneric("true");

        service.setApplication(application);
        service.setInterface(HelloService.class);
        RegistryConfig registry = new RegistryConfig();
        registry.setAddress("zookeeper://10.10.225.128:2181");
        service.setRegistry(registry);
        service.setRef(helloService);
        service.setVersion("1.0.0");
        service.setConnections(4);
        MethodConfig methodConfig = new MethodConfig();
        methodConfig.setName("sayHello");
        methodConfig.setTimeout(1000);
        ArrayList<MethodConfig> methodConfigs = new ArrayList<>();
        methodConfigs.add(methodConfig);
        service.setMethods(methodConfigs);

        // 暴露及注册服务
        service.export();

        System.in.read();
    }
}
