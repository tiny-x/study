package org.rpc.rpc;

import org.rpc.comm.utils.Proxies;
import org.rpc.remoting.api.Directory;
import org.rpc.remoting.api.channel.ChannelGroup;
import org.rpc.rpc.consumer.Consumer;
import org.rpc.rpc.consumer.dispatcher.DefaultRoundDispatcher;
import org.rpc.rpc.consumer.dispatcher.Dispatcher;
import org.rpc.rpc.load.balancer.LoadBalancer;
import org.rpc.rpc.model.ServiceMeta;
import org.rpc.serializer.SerializerType;

import java.util.concurrent.CopyOnWriteArrayList;

public class ProxyFactory {

    private ServiceMeta serviceMeta;

    private Class<?> interfaces;

    private Consumer consumer;

    private long timeoutMillis;

    public static ProxyFactory factory(Class<?> interfaces) {

        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.interfaces = interfaces;
        return proxyFactory;
    }

    public ProxyFactory directory(ServiceMeta serviceMeta) {
        this.serviceMeta = serviceMeta;
        return this;
    }

    public ProxyFactory consumer(Consumer consumer) {
        this.consumer = consumer;
        return this;
    }

    public ProxyFactory timeMillis(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
        return this;
    }


    @SuppressWarnings("unchecked")
    public <T> T newProxy() {
        Dispatcher dispatcher = new DefaultRoundDispatcher(consumer, new LoadBalancer() {
            @Override
            public ChannelGroup select(CopyOnWriteArrayList<ChannelGroup> list, Directory directory) {
                // TODO
                return list.get(0);
            }
        }, SerializerType.PROTO_STUFF);

        dispatcher.timeoutMillis(timeoutMillis);

        return (T) Proxies.getDefault().newProxy(interfaces, new SyncInvoker(dispatcher, serviceMeta));
    }

}
