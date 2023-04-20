package com.xy.dubbo.demo;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;
import com.example.demo.service.HelloService;

import java.io.IOException;

public class Provider3 {

    public static void main(String[] args) throws IOException {

        // 服务实现
        HelloService helloService = new HelloServiceImpl();

        // 当前应用配置
        ApplicationConfig application = new ApplicationConfig();
        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setPort(30001);
        application.setName("provider2");
        // 服务提供者暴露服务配置
        ServiceConfig<HelloService> service = new ServiceConfig<>();
        service.setProtocol(protocolConfig);
        service.setApplication(application);
        service.setInterface(HelloService.class);
        service.setRef(helloService);
        service.setVersion("1.0.0");
        service.setRegistry(new RegistryConfig("zookeeper://127.0.0.1:2181"));

        // 暴露及注册服务
        service.export();

        System.in.read();
    }
}
