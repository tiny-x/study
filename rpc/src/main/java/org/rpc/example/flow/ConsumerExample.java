package org.rpc.example.flow;

import org.rpc.comm.UnresolvedAddress;
import org.rpc.exception.RemotingConnectException;
import org.rpc.remoting.netty.NettyClientConfig;
import org.rpc.rpc.ProxyFactory;
import org.rpc.rpc.consumer.Consumer;
import org.rpc.rpc.consumer.DefaultConsumer;
import org.rpc.rpc.model.ServiceMeta;

public class ConsumerExample {

    public static void main(String[] args) throws RemotingConnectException, InterruptedException {
        NettyClientConfig config = new NettyClientConfig();
        Consumer consumer = new DefaultConsumer("consumer", config);
        UnresolvedAddress address = new UnresolvedAddress("127.0.0.1", 9180);
        consumer.connect(address);

        ServiceMeta serviceMeta = new ServiceMeta("test", "org.rpc.example.demo.HelloService", "1.0.0");
        consumer.client().addChannelGroup(serviceMeta, address);

        HelloService helloService = ProxyFactory.factory(HelloService.class)
                .consumer(consumer)
                .directory(serviceMeta)
                .timeMillis(300000L)
                .newProxy();

        long l = System.currentTimeMillis();
        for (int i = 0; i < 101; i++) {
            String s = helloService.sayHello(" biu biu biu!!!");
            //System.out.printf("---------->: receive provider message %s \n", s);
        }
        System.out.printf("耗时 %s \n", System.currentTimeMillis() - l);

    }
}
