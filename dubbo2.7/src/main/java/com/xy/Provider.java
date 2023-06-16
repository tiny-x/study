package com.xy;

import com.example.demo.service.HelloService;
import com.example.demo.service.HelloServiceImpl;
import org.apache.dubbo.config.*;

/**
 * @author xf.yefei
 */
public class Provider {

    public static void main(String[] args) throws Exception {
        // 当前应用配置
        ApplicationConfig application = new ApplicationConfig();

        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setName("dubbo");
        protocolConfig.setPort(30000);

        application.setName("provider");
        // 服务提供者暴露服务配置
        ServiceConfig<HelloService> service = new ServiceConfig<>();
        service.setProtocol(protocolConfig);

        service.setApplication(application);
        service.setInterface(HelloService.class);
        service.setRef(new HelloServiceImpl());
        service.setVersion("1.0.0");
        service.setRegistry(new RegistryConfig("zookeeper://10.10.225.128:2181"));

        // 暴露及注册服务
        service.export();
        System.in.read();
    }
}
