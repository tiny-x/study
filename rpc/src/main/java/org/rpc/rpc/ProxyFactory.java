package org.rpc.rpc;

import io.netty.util.internal.SystemPropertyUtil;
import org.rpc.comm.utils.Proxies;
import org.rpc.register.NotifyEvent;
import org.rpc.register.NotifyListener;
import org.rpc.register.OfflineListener;
import org.rpc.register.model.RegisterMeta;
import org.rpc.rpc.consumer.Consumer;
import org.rpc.rpc.consumer.StrategyConfig;
import org.rpc.rpc.consumer.cluster.ClusterInvoker;
import org.rpc.rpc.consumer.dispatcher.DefaultRoundDispatcher;
import org.rpc.rpc.consumer.dispatcher.Dispatcher;
import org.rpc.rpc.consumer.invoke.DefaultInvoker;
import org.rpc.rpc.load.balancer.RandomRobinLoadBalancer;
import org.rpc.rpc.model.ServiceMeta;
import org.rpc.serializer.SerializerType;

public class ProxyFactory {

    private ServiceMeta serviceMeta;

    private Class<?> interfaces;

    private Consumer consumer;

    private long timeoutMillis;

    private boolean sync = true;

    private static final SerializerType serializerType;

    static {
        serializerType = SerializerType.parse(
                (byte) SystemPropertyUtil.getInt("serializer.serializerType", SerializerType.PROTO_STUFF.value()));
    }

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

    public ProxyFactory sync(boolean sync) {
        this.sync = sync;
        return this;
    }


    @SuppressWarnings("unchecked")
    public <T> T newProxy() {
        Dispatcher dispatcher = new DefaultRoundDispatcher(
                consumer,
                RandomRobinLoadBalancer.instance(),
                serializerType);

        dispatcher.timeoutMillis(timeoutMillis);

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

                                consumer.client().group(registerMeta.getAddress())
                                        .setWeight(serviceMeta, registerMeta.getWeight());

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

        return (T) Proxies.getDefault().newProxy(
                interfaces,
                new DefaultInvoker(
                        consumer.application(),
                        dispatcher,
                        serviceMeta,
                        new StrategyConfig(ClusterInvoker.Strategy.FAIL_FAST),
                        sync
                ));
    }

}
