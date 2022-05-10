package com.xy.dubbo.demo;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;
import com.perfma.paas.service.core.api.IProductInfoService;
import com.perfma.paas.service.core.api.IProductInfoServiceImpl;

import java.io.IOException;

public class Provider2 {

    public static void main(String[] args) throws IOException {

        // 服务实现
        IProductInfoServiceImpl helloService = new IProductInfoServiceImpl();

        // 当前应用配置
        ApplicationConfig application = new ApplicationConfig();
        application.setName("xyz");

        // 服务提供者暴露服务配置
        ServiceConfig<IProductInfoService> service = new ServiceConfig<>();
        service.setApplication(application);
        service.setInterface(IProductInfoService.class);
        service.setRegister(false);
        service.setRef(helloService);
        service.setVersion("1.0.0");
        service.setRegistry(new RegistryConfig("zookeeper://192.168.102.250:2181"));

        // 暴露及注册服务
        service.export();

        System.in.read();
    }
}
