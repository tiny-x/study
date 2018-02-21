package org.rpc.example.register;

import org.rpc.comm.UnresolvedAddress;
import org.rpc.remoting.netty.NettyClientConfig;
import org.rpc.rpc.ProxyFactory;
import org.rpc.rpc.consumer.Consumer;
import org.rpc.rpc.consumer.DefaultConsumer;
import org.rpc.rpc.model.ServiceMeta;

public class ConsumerExample {

    public static void main(String[] args) {
        NettyClientConfig config = new NettyClientConfig();
        Consumer consumer = new DefaultConsumer("consumer", config);
        consumer.connectToRegistryServer("127.0.0.1:9876");

        ServiceMeta serviceMeta = new ServiceMeta("test", "org.rpc.example.demo.HelloService", "1.0.0");

        HelloService helloService = ProxyFactory.factory(HelloService.class)
                .consumer(consumer)
                .directory(serviceMeta)
                .timeMillis(300000L)
                .newProxy();

        String s = helloService.sayHello(" biu biu biu!!!");

    }
}
