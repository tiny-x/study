package org.rpc.register.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelMatcher;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.internal.SystemPropertyUtil;
import org.rpc.comm.UnresolvedAddress;
import org.rpc.comm.collection.ConcurrentSet;
import org.rpc.register.NotifyEvent;
import org.rpc.register.model.Notify;
import org.rpc.register.model.RegisterMeta;
import org.rpc.remoting.api.ChannelEventAdapter;
import org.rpc.remoting.api.RemotingCommandFactory;
import org.rpc.remoting.api.RequestProcessor;
import org.rpc.remoting.api.RpcServer;
import org.rpc.remoting.api.payload.RequestCommand;
import org.rpc.remoting.api.payload.ResponseCommand;
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

import static org.rpc.remoting.api.procotol.ProtocolHead.*;

public class DefaultRegisterServer implements RegisterServer {

    private static final Logger logger = LoggerFactory.getLogger(DefaultRegisterServer.class);

    private static final AttributeKey<ConcurrentSet<ServiceMeta>> SUBSCRIBE_KEY =
            AttributeKey.valueOf("server.subscribed");

    private static final AttributeKey<ConcurrentSet<RegisterMeta>> PUBLISH_KEY =
            AttributeKey.valueOf("server.publish");

    private static final ConcurrentMap<String, ConcurrentSet<ServiceMeta>> CONSUMER_MAP = new ConcurrentHashMap<>();

    private static final ConcurrentMap<String, ConcurrentSet<RegisterMeta>> PROVIDER_MAP = new ConcurrentHashMap<>();

    // 订阅者
    private static final ChannelGroup subscriberChannels =
            new DefaultChannelGroup("subscribers", GlobalEventExecutor.INSTANCE);

    private static final int DEFAULT_PORT = 9876;
    private static final SerializerType serializerType;

    static {
        serializerType = SerializerType.parse(
                (byte) SystemPropertyUtil.getInt("serializer.serializerType", SerializerType.PROTO_STUFF.value()));
    }

    private RpcServer rpcServer;

    public DefaultRegisterServer() {
        NettyServerConfig config = new NettyServerConfig();
        config.setPort(DEFAULT_PORT);
        this.rpcServer = new NettyServer(config, new RegisterChannelEventProcess());
        this.rpcServer.registerRequestProcess(new RegisterProcess(), Executors.newCachedThreadPool());
    }

    public DefaultRegisterServer(NettyServerConfig config) {
        this.rpcServer = new NettyServer(config, new RegisterChannelEventProcess());
        this.rpcServer.registerRequestProcess(new RegisterProcess(), Executors.newCachedThreadPool());
    }

    private static ResponseCommand handleUnRegisterService(RequestCommand request, Serializer serializer) {
        RegisterMeta registerMeta = serializer.deserialize(request.getBody(), RegisterMeta.class);
        logger.info("[UN_REGISTER] cancel register service: {}", registerMeta);
        String serviceProviderName = registerMeta.getServiceMeta().getServiceProviderName();
        ConcurrentSet<RegisterMeta> registerMetaList = PROVIDER_MAP.get(serviceProviderName);
        if (registerMetaList != null && registerMetaList.size() > 0) {
            registerMetaList.remove(registerMeta);
        }

        // 通知订阅者
        ArrayList<RegisterMeta> registerMetas = new ArrayList<>(1);
        registerMetas.add(registerMeta);
        Notify notify = new Notify(
                NotifyEvent.REMOVE,
                registerMeta.getServiceMeta(),
                registerMetas
        );

        RequestCommand requestCommand = new RequestCommand(ProtocolHead.SUBSCRIBE_SERVICE,
                serializerType.value(),
                serializer.serialize(notify));
        subscriberChannels.writeAndFlush(requestCommand, new ChannelMatcher() {
            @Override
            public boolean matches(Channel channel) {
                Attribute<ConcurrentSet<ServiceMeta>> attr = channel.attr(SUBSCRIBE_KEY);
                ConcurrentSet<ServiceMeta> serviceMetas = attr.get();
                return serviceMetas != null && serviceMetas.contains(registerMeta.getServiceMeta());
            }
        });

        // 回复服务端下线服务成功
        ResponseCommand responseCommand = RemotingCommandFactory.createResponseCommand(
                ProtocolHead.ACK,
                serializerType.value(),
                serializer.serialize(notify),
                request.getInvokeId()
        );

        return responseCommand;
    }

    private static void attachRegisterEvent(RegisterMeta registerMeta, Channel channel) {
        ConcurrentSet<RegisterMeta> services = channel.attr(PUBLISH_KEY).get();
        if (services == null) {
            ConcurrentSet<RegisterMeta> newServices = new ConcurrentSet<>();
            services = channel.attr(PUBLISH_KEY).setIfAbsent(newServices);
            if (services == null) {
                services = newServices;
            }
        }
        services.add(registerMeta);
    }

    private static void attachOfflineEvent(RegisterMeta registerMeta, Channel channel) {
        ConcurrentSet<RegisterMeta> services = channel.attr(PUBLISH_KEY).get();
        if (services == null) {
            ConcurrentSet<RegisterMeta> newServices = new ConcurrentSet<>();
            services = channel.attr(PUBLISH_KEY).setIfAbsent(newServices);
            if (services == null) {
                services = newServices;
            }
        }
        services.remove(registerMeta);
    }

    private static void attachSubscribeEvent(ServiceMeta serviceMeta, Channel channel) {
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
    }

    @Override
    public void start() {
        rpcServer.start();
    }

    static class RegisterChannelEventProcess extends ChannelEventAdapter {

        @Override
        public void onChannelInActive(String remoteAddr, Channel channel) {
            logger.info("[OFFLINE_SERVICE] server: {} offline", remoteAddr);
            ConcurrentSet<RegisterMeta> registerMetas = channel.attr(PUBLISH_KEY).get();
            if (registerMetas != null && registerMetas.size() > 0) {
                UnresolvedAddress address = null;
                for (RegisterMeta registerMeta : registerMetas) {
                    address = registerMeta.getAddress();
                    attachOfflineEvent(registerMeta, channel);
                    ConcurrentSet<RegisterMeta> services = PROVIDER_MAP.get(registerMeta.getServiceMeta().getServiceProviderName());
                    services.remove(registerMeta);
                }

                // 通知订阅下线服务
                Notify notify = new Notify(address);
                Serializer serializer = SerializerFactory.serializer(serializerType);
                RequestCommand requestCommand = new RequestCommand(ProtocolHead.OFFLINE_SERVICE,
                        serializerType.value(),
                        serializer.serialize(notify));
                subscriberChannels.writeAndFlush(requestCommand);
            }
        }
    }

    static class RegisterProcess implements RequestProcessor {

        @Override
        public ResponseCommand process(ChannelHandlerContext context, RequestCommand request) {
            Serializer serializer = SerializerFactory.serializer(SerializerType.parse(request.getSerializerCode()));

            switch (request.getMessageCode()) {
                case REGISTER_SERVICE: {
                    return handleRegisterService(context, request, serializer);
                }
                case SUBSCRIBE_SERVICE: {
                    return handleSubscribeService(context, request, serializer);
                }
                case CANCEL_REGISTER_SERVICE: {
                    return handleUnRegisterService(request, serializer);
                }
                default:
                    throw new UnsupportedOperationException("RegisterProcess Unsupported MessageCode: " + request.getMessageCode());
            }
        }

        // 订阅服务
        private ResponseCommand handleSubscribeService(ChannelHandlerContext context, RequestCommand request, Serializer serializer) {
            ServiceMeta serviceMeta = serializer.deserialize(request.getBody(), ServiceMeta.class);

            logger.info("[SUBSCRIBE] subscribe service: {}", serviceMeta);

            // channel 附上 订阅的服务（三元素）
            Channel channel = context.channel();
            attachSubscribeEvent(serviceMeta, channel);

            String serviceProviderName = serviceMeta.getServiceProviderName();
            ConcurrentSet<ServiceMeta> consumers = CONSUMER_MAP.get(serviceProviderName);
            if (consumers == null) {
                ConcurrentSet<ServiceMeta> newConsumers = new ConcurrentSet();
                consumers = CONSUMER_MAP.putIfAbsent(serviceProviderName, newConsumers);
                if (consumers == null) {
                    consumers = newConsumers;
                }
            }
            consumers.add(serviceMeta);

            ConcurrentSet<RegisterMeta> providers = PROVIDER_MAP.get(serviceProviderName);
            Notify notify = new Notify(
                    NotifyEvent.ADD,
                    serviceMeta,
                    new ArrayList<>(providers)
            );

            // 返回给客户端已经注册的服务
            ResponseCommand responseCommand = RemotingCommandFactory.createResponseCommand(
                    ProtocolHead.ACK,
                    serializerType.value(),
                    serializer.serialize(notify),
                    request.getInvokeId()
            );

            return responseCommand;
        }

        // 注册服务
        private ResponseCommand handleRegisterService(ChannelHandlerContext context, RequestCommand request, Serializer serializer) {
            RegisterMeta registerMeta = serializer.deserialize(request.getBody(), RegisterMeta.class);

            logger.info("[REGISTER] register service: {}", registerMeta);
            String serviceProviderName = registerMeta.getServiceMeta().getServiceProviderName();
            ConcurrentSet<RegisterMeta> providers = PROVIDER_MAP.get(serviceProviderName);
            if (providers == null) {
                ConcurrentSet<RegisterMeta> newProviders = new ConcurrentSet();
                providers = PROVIDER_MAP.putIfAbsent(serviceProviderName, newProviders);
                if (providers == null) {
                    providers = newProviders;
                }
            }
            providers.add(registerMeta);

            // channel 绑定服务
            Channel channel = context.channel();
            attachRegisterEvent(registerMeta, channel);

            // 通知订阅者
            ArrayList<RegisterMeta> registerMetas = new ArrayList<>(1);
            registerMetas.add(registerMeta);
            Notify notify = new Notify(
                    NotifyEvent.ADD,
                    registerMeta.getServiceMeta(),
                    registerMetas
            );

            RequestCommand requestCommand = new RequestCommand(ProtocolHead.SUBSCRIBE_SERVICE,
                    serializerType.value(),
                    serializer.serialize(notify));
            subscriberChannels.writeAndFlush(requestCommand, new ChannelMatcher() {
                @Override
                public boolean matches(Channel channel) {
                    Attribute<ConcurrentSet<ServiceMeta>> attr = channel.attr(SUBSCRIBE_KEY);
                    ConcurrentSet<ServiceMeta> serviceMetas = attr.get();
                    return serviceMetas != null && serviceMetas.contains(registerMeta.getServiceMeta());
                }
            });

            // 回复服务端注册成功
            ResponseCommand responseCommand = RemotingCommandFactory.createResponseCommand(
                    ProtocolHead.ACK,
                    serializerType.value(),
                    null,
                    request.getInvokeId()
            );
            return responseCommand;
        }

        @Override
        public boolean rejectRequest() {
            return false;
        }
    }
}
