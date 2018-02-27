package org.rpc.example.generic;

import org.rpc.comm.UnresolvedAddress;
import org.rpc.exception.RemotingConnectException;
import org.rpc.remoting.netty.NettyClientConfig;
import org.rpc.rpc.GenericProxyFactory;
import org.rpc.rpc.ProxyFactory;
import org.rpc.rpc.consumer.Consumer;
import org.rpc.rpc.consumer.DefaultConsumer;
import org.rpc.rpc.consumer.cluster.ClusterInvoker;
import org.rpc.rpc.consumer.invoke.GenericInvoke;
import org.rpc.rpc.model.ServiceMeta;

public class ConsumerExample {

    public static void main(String[] args) throws Throwable {
        NettyClientConfig config = new NettyClientConfig();
        Consumer consumer = new DefaultConsumer("consumer", config);
        UnresolvedAddress address = new UnresolvedAddress("127.0.0.1", 9180);
        consumer.connect(address);

        ServiceMeta serviceMeta = new ServiceMeta("test", "org.rpc.example.demo.HelloService", "1.0.0");
        consumer.client().addChannelGroup(serviceMeta, address);

        GenericInvoke genericInvoke = GenericProxyFactory.factory()
                .consumer(consumer)
                .directory(serviceMeta)
                .timeMillis(3000L)
                .newProxy();

        String s = (String) genericInvoke.$invoke("sayHello"," biu biu biu!!!");
        System.out.printf("---------->: receive provider message %s \n", s);

    }
}
