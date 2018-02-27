package org.rpc.rpc;

import io.netty.util.internal.SystemPropertyUtil;
import org.rpc.register.NotifyEvent;
import org.rpc.register.NotifyListener;
import org.rpc.register.OfflineListener;
import org.rpc.register.model.RegisterMeta;
import org.rpc.rpc.consumer.Consumer;
import org.rpc.rpc.consumer.InvokeType;
import org.rpc.rpc.consumer.StrategyConfig;
import org.rpc.rpc.consumer.cluster.ClusterInvoker;
import org.rpc.rpc.consumer.dispatcher.DefaultRoundDispatcher;
import org.rpc.rpc.consumer.dispatcher.Dispatcher;
import org.rpc.rpc.consumer.invoke.GenericInvoke;
import org.rpc.rpc.load.balancer.RandomRobinLoadBalancer;
import org.rpc.rpc.model.ServiceMeta;
import org.rpc.serializer.SerializerType;

public class GenericProxyFactory {

    private static final SerializerType serializerType;

    static {
        serializerType = SerializerType.parse(
                (byte) SystemPropertyUtil.getInt("serializer.serializerType", SerializerType.PROTO_STUFF.value()));
    }

    private ServiceMeta serviceMeta;
    private Consumer consumer;
    private long timeoutMillis;
    private InvokeType invokeType = InvokeType.SYNC;
    private ClusterInvoker.Strategy strategy = ClusterInvoker.Strategy.FAIL_FAST;
    private int retries = 0;

    public static GenericProxyFactory factory() {
        GenericProxyFactory proxyFactory = new GenericProxyFactory();
        return proxyFactory;
    }

    public GenericProxyFactory directory(ServiceMeta serviceMeta) {
        this.serviceMeta = serviceMeta;
        return this;
    }

    public GenericProxyFactory consumer(Consumer consumer) {
        this.consumer = consumer;
        return this;
    }

    public GenericProxyFactory timeMillis(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
        return this;
    }

    public GenericProxyFactory invokeType(InvokeType invokeType) {
        this.invokeType = invokeType;
        return this;
    }

    public GenericProxyFactory strategy(ClusterInvoker.Strategy strategy) {
        this.strategy = strategy;
        return this;
    }

    public GenericProxyFactory retries(int retries) {
        this.retries = retries;
        return this;
    }

    @SuppressWarnings("unchecked")
    public GenericInvoke newProxy() {
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
                                int connCount = registerMeta.getConnCount() < 1 ? 1 : registerMeta.getConnCount();
                                for (int i = 0; i < connCount; i++) {
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

        GenericInvoke genericInvoke = new GenericInvoke(
                consumer.application(),
                dispatcher,
                serviceMeta,
                new StrategyConfig(strategy, retries),
                invokeType
        );
        return genericInvoke;
    }

}
