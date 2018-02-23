package org.rpc.example.demo;

import org.rpc.comm.UnresolvedAddress;
import org.rpc.remoting.netty.NettyClientConfig;
import org.rpc.rpc.ProxyFactory;
import org.rpc.rpc.consumer.Consumer;
import org.rpc.rpc.consumer.DefaultConsumer;
import org.rpc.rpc.consumer.future.RpcContext;
import org.rpc.rpc.consumer.future.RpcFuture;
import org.rpc.rpc.model.ServiceMeta;

public class ConsumerAsyncExample {

    public static void main(String[] args) {
        NettyClientConfig config = new NettyClientConfig();
        Consumer consumer = new DefaultConsumer("consumer", config);
        UnresolvedAddress address = new UnresolvedAddress("127.0.0.1", 9180);
        consumer.connect(address);

        ServiceMeta serviceMeta = new ServiceMeta("test", "org.rpc.example.demo.HelloService", "1.0.0");
        consumer.client().addChannelGroup(serviceMeta, address);

        HelloService helloService = ProxyFactory.factory(HelloService.class)
                .consumer(consumer)
                .directory(serviceMeta)
                .timeMillis(3000L)
                .sync(false)
                .newProxy();

        String s = helloService.sayHello(" biu biu biu!!!");
        System.out.println(s);

        RpcFuture<String> future = RpcContext.getFuture();
        System.out.println(future.get());

    }
}
