package org.rpc.rpc.consumer.dispatcher;

import io.netty.channel.Channel;
import org.rpc.exception.RemotingException;
import org.rpc.remoting.api.InvokeCallback;
import org.rpc.remoting.api.channel.ChannelGroup;
import org.rpc.remoting.api.future.ResponseFuture;
import org.rpc.remoting.api.payload.ResponseBytes;
import org.rpc.rpc.Request;
import org.rpc.rpc.consumer.Consumer;
import org.rpc.rpc.load.balancer.LoadBalancer;
import org.rpc.rpc.model.ServiceMeta;
import org.rpc.serializer.Serializer;
import org.rpc.serializer.SerializerFactory;
import org.rpc.serializer.SerializerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

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

    protected Channel select(ServiceMeta metadata) {
        CopyOnWriteArrayList<ChannelGroup> groups = consumer.client().directory(metadata);

        ChannelGroup group = loadBalancer.select(groups, metadata);

        if (group != null) {
            if (group.isAvailable()) {
                return group.next();
            }
        }

        for (ChannelGroup g : groups) {
            if (g.isAvailable()) {
                return g.next();
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

    protected ResponseBytes invoke(Channel channel,
                           final Request request,
                           final DispatchType dispatchType,
                           boolean sync) {

        ResponseBytes responseBytes = null;
        try {
            if (sync) {
                responseBytes = consumer.client()
                        .invokeSync(channel, request.getRequestBytes(), timeoutMillis, TimeUnit.MILLISECONDS);

            } else {

               consumer.client()
                        .invokeAsync(channel,
                                request.getRequestBytes(),
                                timeoutMillis,
                                TimeUnit.MILLISECONDS
                                , new InvokeCallback<ResponseBytes>() {
                                    @Override
                                    public void operationComplete(ResponseFuture<ResponseBytes> responseFuture) throws ExecutionException, InterruptedException {

                                    }
                                });

            }
        } catch (RemotingException e) {
            logger.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }

        return responseBytes;
    }
}
