package com.xy.dubbo.benchmark.leaf;

import com.leaf.common.UnresolvedAddress;
import com.leaf.rpc.DefaultProxyFactory;
import com.leaf.rpc.consumer.DefaultLeafClient;
import com.leaf.rpc.consumer.LeafClient;
import com.xy.dubbo.demo.HelloService;

public class ConsumerExample {

    private static HelloService helloService;

    static {
        LeafClient leafClient = new DefaultLeafClient("consumer");
        helloService = DefaultProxyFactory.factory(HelloService.class)
                .consumer(leafClient)
                .providers(new UnresolvedAddress("127.0.0.1", 9180))
                .group("test")
                .version("1.0.0")
                .timeMillis(3000L)
                .newProxy();
    }

    public static void main(String[] args) {
        new ConsumerExample().invoke();
    }

    public void invoke() {
        helloService.sayHello(" biu biu biu!!!");
    }
}
