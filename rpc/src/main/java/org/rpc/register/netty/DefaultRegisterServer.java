package org.rpc.register.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelMatcher;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.rpc.comm.collection.ConcurrentSet;
import org.rpc.register.NotifyEvent;
import org.rpc.register.model.Notify;
import org.rpc.register.model.RegisterMeta;
import org.rpc.remoting.api.RequestProcessor;
import org.rpc.remoting.api.RpcServer;
import org.rpc.remoting.api.payload.RequestBytes;
import org.rpc.remoting.api.payload.ResponseBytes;
import org.rpc.remoting.api.procotol.ProtocolHead;
import org.rpc.remoting.netty.NettyServer;
import org.rpc.remoting.netty.NettyServerConfig;
import org.rpc.rpc.model.ServiceMeta;
import org.rpc.serializer.Serializer;
import org.rpc.serializer.SerializerFactory;
import org.rpc.serializer.SerializerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;

import static org.rpc.remoting.api.procotol.ProtocolHead.REGISTER_SERVICE;
import static org.rpc.remoting.api.procotol.ProtocolHead.SUBSCRIBE_SERVICE;

public class DefaultRegisterServer implements RegisterServer {

    private static final int DEFAULT_PORT = 9876;

    private RpcServer rpcServer;

    public DefaultRegisterServer() {
        NettyServerConfig config = new NettyServerConfig();
        config.setPort(DEFAULT_PORT);
        this.rpcServer = new NettyServer(config);
        this.rpcServer.registerRequestProcess(new RegisterProcess(), Executors.newCachedThreadPool());
    }

    @Override
    public void start() {
        rpcServer.start();
    }

    @Override
    public RpcServer server() {
        return rpcServer;
    }

    static class RegisterProcess implements RequestProcessor {

        private static final Logger logger = LoggerFactory.getLogger(RegisterProcess.class);

        private ConcurrentMap<String, ConcurrentSet<ServiceMeta>> consumerMap = new ConcurrentHashMap<>();

        private ConcurrentMap<String, ConcurrentSet<RegisterMeta>> providerMap = new ConcurrentHashMap<>();

        private static final AttributeKey<ConcurrentSet<ServiceMeta>> SUBSCRIBE_KEY =
                AttributeKey.valueOf("server.subscribed");

        // 订阅者
        private final ChannelGroup subscriberChannels =
                new DefaultChannelGroup("subscribers", GlobalEventExecutor.INSTANCE);

        @Override
        public ResponseBytes process(ChannelHandlerContext context, RequestBytes request) {
            Serializer serializer = SerializerFactory.serializer(SerializerType.parse(request.getSerializerCode()));

            switch (request.getMessageCode()) {
                case REGISTER_SERVICE: {
                    return handleRegisterService(request, serializer);
                }
                case SUBSCRIBE_SERVICE: {
                    return handleSubscribeService(context, request, serializer);
                }
                default:
                    throw new UnsupportedOperationException("RegisterProcess Unsupported MessageCode: " + request.getMessageCode());
            }
        }

        // 订阅服务
        private ResponseBytes handleSubscribeService(ChannelHandlerContext context, RequestBytes request, Serializer serializer) {
            ServiceMeta serviceMeta = serializer.deserialize(request.getBody(), ServiceMeta.class);

            logger.debug("[SUBSCRIBE] consumer: subscribe service: {}", serviceMeta);

            // channel 附上 订阅的服务（三元素）
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
            serviceMetas.add(serviceMeta);

            subscriberChannels.add(channel);

            String serviceProviderName = serviceMeta.getServiceProviderName();
            ConcurrentSet<ServiceMeta> consumers = consumerMap.get(serviceProviderName);
            if (consumers == null) {
                ConcurrentSet<ServiceMeta> newConsumers = new ConcurrentSet();
                consumers = consumerMap.putIfAbsent(serviceProviderName, newConsumers);
                if (consumers == null) {
                    consumers = newConsumers;
                }
            }
            consumers.add(serviceMeta);

            ConcurrentSet<RegisterMeta> providers = providerMap.get(serviceProviderName);
            Notify notify = new Notify(
                    NotifyEvent.ADD,
                    serviceMeta,
                    new ArrayList<>(providers)
            );

            // 返回给客户端已经注册的服务
            ResponseBytes responseBytes = new ResponseBytes(ProtocolHead.SUBSCRIBE_RECEIVE,
                    SerializerType.PROTO_STUFF.value(),
                    serializer.serialize(notify));
            responseBytes.setStatus(ProtocolHead.STATUS_SUCCESS);
            responseBytes.setInvokeId(request.getInvokeId());
            return responseBytes;
        }

        // 注册服务
        private ResponseBytes handleRegisterService(RequestBytes request, Serializer serializer) {
            RegisterMeta registerMeta = serializer.deserialize(request.getBody(), RegisterMeta.class);

            logger.debug("[REGISTER] provider: {} register service: {}", registerMeta.getAddress().getHost(), registerMeta);
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

            ArrayList<RegisterMeta> registerMetas = new ArrayList<>(1);
            registerMetas.add(registerMeta);
            Notify notify = new Notify(
                    NotifyEvent.ADD,
                    registerMeta.getServiceMeta(),
                    registerMetas
            );

            ResponseBytes responseBytesNotify = new ResponseBytes(ProtocolHead.SUBSCRIBE_RECEIVE,
                    SerializerType.PROTO_STUFF.value(),
                    serializer.serialize(notify));
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

        @Override
        public boolean rejectRequest() {
            return false;
        }
    }
}
