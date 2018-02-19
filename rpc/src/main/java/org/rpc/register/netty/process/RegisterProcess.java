package org.rpc.register.netty.process;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelMatcher;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.rpc.comm.collection.ConcurrentSet;
import org.rpc.register.bean.RegisterMeta;
import org.rpc.remoting.api.RequestProcessor;
import org.rpc.remoting.api.payload.RequestBytes;
import org.rpc.remoting.api.payload.ResponseBytes;
import org.rpc.remoting.api.procotol.ProtocolHead;
import org.rpc.rpc.model.ServiceMeta;
import org.rpc.serializer.Serializer;
import org.rpc.serializer.SerializerFactory;
import org.rpc.serializer.SerializerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.rpc.remoting.api.procotol.ProtocolHead.*;

public class RegisterProcess implements RequestProcessor {

    private static final Logger logger = LoggerFactory.getLogger(RegisterProcess.class);

    private ConcurrentMap<String, ConcurrentSet<RegisterMeta>> consumerMap = new ConcurrentHashMap<>();

    private ConcurrentMap<String, ConcurrentSet<RegisterMeta>> providerMap = new ConcurrentHashMap<>();

    private static final AttributeKey<ConcurrentSet<ServiceMeta>> SUBSCRIBE_KEY =
            AttributeKey.valueOf("server.subscribed");

    // 订阅者
    private final ChannelGroup subscriberChannels = new DefaultChannelGroup("subscribers", GlobalEventExecutor.INSTANCE);

    @Override
    public ResponseBytes process(ChannelHandlerContext context, RequestBytes request) {
        Serializer serializer = SerializerFactory.serializer(SerializerType.parse(request.getSerializerCode()));
        RegisterMeta registerMeta = serializer.deserialize(request.getBody(), RegisterMeta.class);

        switch (request.getMessageCode()) {
            case REGISTER_SERVICE: {
                logger.debug("provider: {} register service: {}", registerMeta.getAddress().getHost(), registerMeta);
                String serviceProviderName = registerMeta.getServiceMeta().getServiceProviderName();
                ConcurrentSet<RegisterMeta> providers = providerMap.get(serviceProviderName);
                if (providers == null) {
                    ConcurrentSet<RegisterMeta> newProviders = new ConcurrentSet();
                    providers = providerMap.putIfAbsent(serviceProviderName, newProviders);
                    if (providers == null) {
                        providers = newProviders;
                    }
                }
                providers.add(registerMeta);

                ResponseBytes responseBytesNotify = new ResponseBytes(ProtocolHead.REGISTER_NOTIFY,
                        SerializerType.PROTO_STUFF.value(),
                        request.getBody());
                subscriberChannels.writeAndFlush(responseBytesNotify, new ChannelMatcher() {
                    @Override
                    public boolean matches(Channel channel) {
                        Attribute<ConcurrentSet<ServiceMeta>> attr = channel.attr(SUBSCRIBE_KEY);
                        ConcurrentSet<ServiceMeta> serviceMetas = attr.get();
                        return serviceMetas != null && serviceMetas.contains(registerMeta.getServiceMeta());
                    }
                });

                // 回复服务端注册成功
                ResponseBytes responseBytes = new ResponseBytes(ProtocolHead.REGISTER_RECEIVE,
                        SerializerType.PROTO_STUFF.value(),
                        null);
                responseBytes.setStatus(ProtocolHead.STATUS_SUCCESS);
                responseBytes.setInvokeId(request.getInvokeId());
                return responseBytes;
            }
            case SUBSCRIBE_SERVICE: {
                logger.debug("consumer: {} subscribe service: {}", registerMeta.getAddress().getHost(), registerMeta);

                Channel channel = context.channel();
                Attribute<ConcurrentSet<ServiceMeta>> attr = channel.attr(SUBSCRIBE_KEY);
                ConcurrentSet<ServiceMeta> serviceMetas = attr.get();
                if (serviceMetas == null) {
                    ConcurrentSet<ServiceMeta> newServiceMetas = new ConcurrentSet<>();
                    serviceMetas = attr.setIfAbsent(newServiceMetas);
                    if (serviceMetas == null) {
                        serviceMetas = newServiceMetas;
                    }
                }
                serviceMetas.add(registerMeta.getServiceMeta());

                subscriberChannels.add(channel);

                String serviceProviderName = registerMeta.getServiceMeta().getServiceProviderName();
                ConcurrentSet<RegisterMeta> consumers = consumerMap.get(serviceProviderName);
                if (consumers == null) {
                    ConcurrentSet<RegisterMeta> newConsumers = new ConcurrentSet();
                    consumers = consumerMap.putIfAbsent(serviceProviderName, newConsumers);
                    if (consumers == null) {
                        consumers = newConsumers;
                    }
                }
                consumers.add(registerMeta);

                // 回复客户端订阅成功
                ResponseBytes responseBytes = new ResponseBytes(ProtocolHead.SUBSCRIBE_RECEIVE,
                        SerializerType.PROTO_STUFF.value(),
                        null);
                responseBytes.setStatus(ProtocolHead.STATUS_SUCCESS);
                responseBytes.setInvokeId(request.getInvokeId());
                return responseBytes;
            }
            case LOOKUP_SERVICE: {
                String serviceProviderName = registerMeta.getServiceMeta().getServiceProviderName();
                ConcurrentSet<RegisterMeta> providers = providerMap.get(serviceProviderName);

                logger.debug("consumer: {} lookup service: {}, providers: {}",
                        registerMeta.getAddress().getHost(),
                        registerMeta,
                        providers);

                ResponseBytes responseBytes = new ResponseBytes(ProtocolHead.SUBSCRIBE_RECEIVE,
                        SerializerType.PROTO_STUFF.value(),
                        serializer.serialize(new ArrayList<>(providers)));

                responseBytes.setStatus(ProtocolHead.STATUS_SUCCESS);
                responseBytes.setInvokeId(request.getInvokeId());
                return responseBytes;
            }
            default:
                throw new UnsupportedOperationException("RegisterProcess Unsupported MessageCode: " + request.getMessageCode());
        }
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
