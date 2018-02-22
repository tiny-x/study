package org.rpc.rpc;

import org.rpc.comm.UnresolvedAddress;
import org.rpc.comm.utils.Proxies;
import org.rpc.register.NotifyEvent;
import org.rpc.register.NotifyListener;
import org.rpc.register.OfflineListener;
import org.rpc.register.model.RegisterMeta;
import org.rpc.remoting.api.Directory;
import org.rpc.remoting.api.channel.ChannelGroup;
import org.rpc.rpc.consumer.Consumer;
import org.rpc.rpc.consumer.dispatcher.DefaultRoundDispatcher;
import org.rpc.rpc.consumer.dispatcher.Dispatcher;
import org.rpc.rpc.load.balancer.LoadBalancer;
import org.rpc.rpc.model.ServiceMeta;
import org.rpc.serializer.SerializerType;
import org.rpc.utils.InetUtils;

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

        RegisterMeta registerMeta = new RegisterMeta();
        registerMeta.setServiceMeta(serviceMeta);
        registerMeta.setAddress(new UnresolvedAddress(InetUtils.getLocalHost(), 9180));

        if (consumer.registerService() != null) {
            consumer.subscribe(serviceMeta, new NotifyListener() {
                @Override
                public void notify(RegisterMeta registerMeta, NotifyEvent event) {
                    switch (event) {
                        case ADD: {
                            if (!consumer.client().hasAvailableChannelGroup(registerMeta.getAddress())) {
                                for (int i = 0; i < registerMeta.getConnCount(); i++) {
                                    consumer.connect(registerMeta.getAddress());
                                    consumer.client().addChannelGroup(serviceMeta, registerMeta.getAddress());
                                }
                                consumer.offlineListening(registerMeta.getAddress(), new OfflineListener() {
                                    @Override
                                    public void offline() {
                                        consumer.client().removeChannelGroup(serviceMeta, registerMeta.getAddress());
                                    }
                                });
                            }
                            break;
                        }
                        case REMOVE: {
                            consumer.client().removeChannelGroup(serviceMeta, registerMeta.getAddress());
                            break;
                        }
                    }
                }
            });
        }

        return (T) Proxies.getDefault().newProxy(interfaces, new SyncInvoker(dispatcher, serviceMeta));
    }

}
