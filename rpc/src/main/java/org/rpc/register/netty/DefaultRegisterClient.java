package org.rpc.register.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import io.netty.util.internal.SystemPropertyUtil;
import org.rpc.comm.UnresolvedAddress;
import org.rpc.comm.collection.ConcurrentSet;
import org.rpc.register.AbstractRegisterService;
import org.rpc.register.model.Notify;
import org.rpc.register.model.RegisterMeta;
import org.rpc.remoting.api.ChannelEventAdapter;
import org.rpc.remoting.api.RequestProcessor;
import org.rpc.remoting.api.RpcClient;
import org.rpc.remoting.api.channel.ChannelGroup;
import org.rpc.remoting.api.payload.RequestCommand;
import org.rpc.remoting.api.payload.ResponseCommand;
import org.rpc.remoting.api.procotol.ProtocolHead;
import org.rpc.remoting.netty.NettyClient;
import org.rpc.remoting.netty.NettyClientConfig;
import org.rpc.rpc.model.ServiceMeta;
import org.rpc.serializer.Serializer;
import org.rpc.serializer.SerializerFactory;
import org.rpc.serializer.SerializerType;
import org.rpc.utils.InetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Executors;

public class DefaultRegisterClient {

    private static final Logger logger = LoggerFactory.getLogger(DefaultRegisterClient.class);

    // 注册中心地址
    private UnresolvedAddress unresolvedAddress = null;

    private RpcClient rpcClient;

    private NettyClientConfig config = new NettyClientConfig();

    private AbstractRegisterService registerService;

    private AttributeKey<ConcurrentSet<RegisterMeta>> REGISTER_KEY = AttributeKey.valueOf("register.key");

    private AttributeKey<ConcurrentSet<ServiceMeta>> SUBSCRIBE_KEY = AttributeKey.valueOf("subscribe.key");

    private volatile Channel channel;

    private static final SerializerType serializerType;

    static {
        serializerType = SerializerType.parse(
                (byte) SystemPropertyUtil.getInt("serializer.serializerType", SerializerType.PROTO_STUFF.value()));
    }

    public DefaultRegisterClient(String address, AbstractRegisterService registerService) {
        this.registerService = registerService;
        // TODO 先按一个注册中心写完功能
        UnresolvedAddress[] addresses = InetUtils.spiltAddress(address);
        unresolvedAddress = addresses[0];

        this.rpcClient = new NettyClient(config, new RegisterClientChannelEventProcess());
        rpcClient.start();
        try {
            rpcClient.connect(unresolvedAddress);
            rpcClient.registerRequestProcess(new RegisterClientProcess(), Executors.newCachedThreadPool());
            ChannelGroup group = rpcClient.group(unresolvedAddress);
            this.channel = group.next();
        } catch (Exception e) {
            logger.error("connect register fail", e);
        }
    }

    public void register(RegisterMeta registerMeta) {
        Serializer serializer = SerializerFactory.serializer(serializerType);
        RequestCommand requestCommand = new RequestCommand(ProtocolHead.REGISTER_SERVICE,
                serializerType.value(),
                serializer.serialize(registerMeta));
        try {
            if (attachRegisterEvent(registerMeta, channel)) {
                rpcClient.invokeSync(channel, requestCommand, config.getInvokeTimeoutMillis());
            }
        } catch (Exception e) {
            logger.error("register service fail", e);
        }
    }

    public void unRegister(RegisterMeta registerMeta) {
        Serializer serializer = SerializerFactory.serializer(serializerType);
        RequestCommand requestCommand = new RequestCommand(ProtocolHead.CANCEL_REGISTER_SERVICE,
                serializerType.value(),
                serializer.serialize(registerMeta));
        try {
            if (attachCancelRegisterEvent(registerMeta, channel)) {
                rpcClient.invokeSync(channel, requestCommand, config.getInvokeTimeoutMillis());
            }
        } catch (Exception e) {
            logger.error("unRegister service fail", e);
        }
    }


    public void subscribe(ServiceMeta serviceMeta) {
        Serializer serializer = SerializerFactory.serializer(serializerType);

        RequestCommand requestCommand = new RequestCommand(ProtocolHead.SUBSCRIBE_SERVICE,
                serializerType.value(),
                serializer.serialize(serviceMeta));

        try {
            if (attachSubscribeEvent(serviceMeta, channel)) {
                ResponseCommand responseCommand = rpcClient.invokeSync(channel, requestCommand, config.getInvokeTimeoutMillis());
                Notify notifyData = serializer.deserialize(responseCommand.getBody(), Notify.class);
                registerService.notify(notifyData.getServiceMeta(),
                        notifyData.getEvent(),
                        notifyData.getRegisterMetas());
            }
        } catch (Exception e) {
            logger.error("subscribe service fail", e);
        }
    }

    public List<RegisterMeta> lookup(RegisterMeta registerMeta) {

        List<RegisterMeta> registerMetaList = null;

        return registerMetaList;
    }

    class RegisterClientChannelEventProcess extends ChannelEventAdapter {

        @Override
        public void onChannelActive(String remoteAddr, Channel channel) {
            DefaultRegisterClient.this.channel = channel;
            // 重新连接 重新发布 订阅服务
            ConcurrentSet<RegisterMeta> providers = registerService.getProviders();
            if (providers != null && providers.size() > 0) {
                for (RegisterMeta registerMeta : providers) {
                    register(registerMeta);
                }
            }
            ConcurrentSet<ServiceMeta> consumers = registerService.getConsumers();
            if (consumers != null && consumers.size() > 0) {
                for (ServiceMeta serviceMeta : consumers) {
                    subscribe(serviceMeta);
                }
            }
        }
    }

    class RegisterClientProcess implements RequestProcessor {

        @Override
        public ResponseCommand process(ChannelHandlerContext context, RequestCommand request) {
            Serializer serializer = SerializerFactory.serializer(SerializerType.parse(request.getSerializerCode()));
            Notify notifyData = serializer.deserialize(request.getBody(), Notify.class);
            switch (request.getMessageCode()) {
                case ProtocolHead.SUBSCRIBE_SERVICE: {
                    registerService.notify(notifyData.getServiceMeta(),
                            notifyData.getEvent(),
                            notifyData.getRegisterMetas());
                }
                case ProtocolHead.OFFLINE_SERVICE: {
                    UnresolvedAddress address = notifyData.getAddress();
                    registerService.offline(address);
                }
                default:
                    throw new UnsupportedOperationException("RegisterClientProcess Unsupported MessageCode: " + request.getMessageCode());
            }
        }

        @Override
        public boolean rejectRequest() {
            return false;
        }
    }

    // channel 附着注册的服务，忽略重复注册
    private boolean attachRegisterEvent(RegisterMeta registerMeta, Channel channel) {
        ConcurrentSet<RegisterMeta> registerMetas = channel.attr(REGISTER_KEY).get();
        if (registerMetas == null) {
            ConcurrentSet<RegisterMeta> newRegisterMetas = new ConcurrentSet<>();
            registerMetas = channel.attr(REGISTER_KEY).setIfAbsent(newRegisterMetas);
            if (registerMetas == null) {
                registerMetas = newRegisterMetas;
            }
        }
        return registerMetas.add(registerMeta);
    }

    private boolean attachCancelRegisterEvent(RegisterMeta registerMeta, Channel channel) {
        ConcurrentSet<RegisterMeta> registerMetas = channel.attr(REGISTER_KEY).get();
        if (registerMetas == null) {
            return false;
        }
        return registerMetas.remove(registerMeta);
    }

    // channel 附着订阅的服务，忽略重复订阅
    private boolean attachSubscribeEvent(ServiceMeta serviceMeta, Channel channel) {
        ConcurrentSet<ServiceMeta> serviceMetas = channel.attr(SUBSCRIBE_KEY).get();
        if (serviceMetas == null) {
            ConcurrentSet<ServiceMeta> newServiceMetas = new ConcurrentSet<>();
            serviceMetas = channel.attr(SUBSCRIBE_KEY).setIfAbsent(newServiceMetas);
            if (serviceMetas == null) {
                serviceMetas = newServiceMetas;
            }
        }
        return serviceMetas.add(serviceMeta);
    }
}
