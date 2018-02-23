package org.rpc.rpc.consumer.dispatcher;

import org.rpc.exception.RemotingException;
import org.rpc.remoting.api.InvokeCallback;
import org.rpc.remoting.api.channel.ChannelGroup;
import org.rpc.remoting.api.future.ResponseFuture;
import org.rpc.remoting.api.payload.ResponseBytes;
import org.rpc.rpc.Request;
import org.rpc.rpc.consumer.Consumer;
import org.rpc.rpc.consumer.future.RpcContext;
import org.rpc.rpc.consumer.future.RpcFuture;
import org.rpc.rpc.load.balancer.LoadBalancer;
import org.rpc.rpc.model.ServiceMeta;
import org.rpc.serializer.Serializer;
import org.rpc.serializer.SerializerFactory;
import org.rpc.serializer.SerializerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;

public abstract class AbstractDispatcher implements Dispatcher {

    private static final Logger logger = LoggerFactory.getLogger(AbstractDispatcher.class);

    private Consumer consumer;

    private LoadBalancer loadBalancer;

    protected long timeoutMillis;

    private SerializerType serializerType;

    public AbstractDispatcher(Consumer consumer, LoadBalancer loadBalancer, SerializerType serializerType) {
        this.consumer = consumer;
        this.loadBalancer = loadBalancer;
        this.serializerType = serializerType;
    }

    protected ChannelGroup select(ServiceMeta metadata) {
        CopyOnWriteArrayList<ChannelGroup> groups = consumer.client().directory(metadata);

        ChannelGroup group = loadBalancer.select(groups, metadata);

        if (group != null) {
            if (group.isAvailable()) {
                return group;
            }
        }

        for (ChannelGroup g : groups) {
            if (g.isAvailable()) {
                return g;
            }
        }
        throw new IllegalStateException("no channel");
    }

    protected ChannelGroup[] groups(ServiceMeta metadata) {
        return (ChannelGroup[]) consumer.client().directory(metadata).toArray();
    }

    @Override
    public Dispatcher timeoutMillis(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
        return this;
    }

    protected Serializer getSerializer() {
        return SerializerFactory.serializer(serializerType);
    }

    protected byte getSerializerCode() {
        return serializerType.value();
    }

    protected Object invoke(ChannelGroup channelGroup,
                            final Request request,
                            final DispatchType dispatchType,
                            Class<?> returnType,
                            boolean sync) throws RemotingException, InterruptedException {

        if (sync) {
            ResponseBytes responseBytes = consumer.client()
                    .invokeSync(channelGroup.remoteAddress(),
                            request.getRequestBytes(),
                            timeoutMillis);
            return getSerializer().deserialize(responseBytes.getBody(), returnType);
        } else {

            RpcFuture future = new RpcFuture();
            RpcContext.setFuture(future);
            consumer.client()
                    .invokeAsync(channelGroup.remoteAddress(),
                            request.getRequestBytes(),
                            timeoutMillis
                            , new InvokeCallback<ResponseBytes>() {
                                @Override
                                public void operationComplete(ResponseFuture<ResponseBytes> responseFuture)
                                        throws ExecutionException, InterruptedException {
                                    ResponseBytes responseBytes = responseFuture.get();
                                    Object result = getSerializer().deserialize(responseBytes.getBody(), returnType);
                                    future.set(result);
                                }
                            });

            return null;
        }

    }
}
