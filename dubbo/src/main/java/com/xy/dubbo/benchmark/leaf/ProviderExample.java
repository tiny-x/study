package com.xy.dubbo.benchmark.leaf;

import com.leaf.rpc.controller.RateLimitFlowController;
import com.leaf.rpc.local.ServiceWrapper;
import com.leaf.rpc.provider.DefaultLeafServer;
import com.leaf.rpc.provider.LeafServer;
import com.xy.dubbo.demo.HelloService;
import com.xy.dubbo.demo.HelloServiceImpl;

public class ProviderExample {

    public static void main(String[] args) {
        LeafServer leafServer = new DefaultLeafServer(9180);
        leafServer.start();
        leafServer.registerGlobalFlowController(new RateLimitFlowController(100000));
        HelloService helloService = new HelloServiceImpl();

        // 注册到本地容器 未发布到注册中心
        ServiceWrapper serviceWrapper = leafServer.serviceRegistry()
                .provider(helloService)
                .interfaceClass(HelloService.class)
                .group("test")
                .version("1.0.0")
                .register();

    }
}
