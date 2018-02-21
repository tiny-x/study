package org.rpc.register.netty;

import io.netty.channel.ChannelHandlerContext;
import org.rpc.comm.UnresolvedAddress;
import org.rpc.register.AbstractRegisterService;
import org.rpc.register.model.Notify;
import org.rpc.register.model.RegisterMeta;
import org.rpc.remoting.api.RequestProcessor;
import org.rpc.remoting.api.RpcClient;
import org.rpc.remoting.api.payload.RequestBytes;
import org.rpc.remoting.api.payload.ResponseBytes;
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
import java.util.concurrent.TimeUnit;

public class DefaultRegisterClient {

    private static final Logger logger = LoggerFactory.getLogger(DefaultRegisterClient.class);

    private UnresolvedAddress unresolvedAddress = null;

    private RpcClient rpcClient;

    private NettyClientConfig config = new NettyClientConfig();

    private AbstractRegisterService registerService;

    public DefaultRegisterClient(String address, AbstractRegisterService registerService) {
        this.registerService = registerService;
        // TODO 先按一个注册中心写完功能
        UnresolvedAddress[] addrsses = InetUtils.spiltAddrss(address);
        unresolvedAddress = addrsses[0];

        this.rpcClient = new NettyClient(config);
        rpcClient.start();
        try {
            rpcClient.connect(unresolvedAddress);
            rpcClient.registerRequestProcess(new RegisterClientProcess(), Executors.newCachedThreadPool());
        } catch (Exception e) {
            logger.error("connect register fail", e);
        }
    }

    public void register(RegisterMeta registerMeta) {
        Serializer serializer = SerializerFactory.serializer(SerializerType.PROTO_STUFF);
        RequestBytes requestBytes = new RequestBytes(ProtocolHead.REGISTER_SERVICE,
                SerializerType.PROTO_STUFF.value(),
                serializer.serialize(registerMeta));

        try {
            rpcClient.invokeSync(unresolvedAddress,
                    requestBytes, config.getInvokeTimeoutMillis(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            logger.error("register service fail", e);
        }

    }

    public void unRegister(RegisterMeta registerMeta) {

    }

    public void subscribe(ServiceMeta serviceMeta) {
        Serializer serializer = SerializerFactory.serializer(SerializerType.PROTO_STUFF);

        RequestBytes requestBytes = new RequestBytes(ProtocolHead.SUBSCRIBE_SERVICE,
                SerializerType.PROTO_STUFF.value(),
                serializer.serialize(serviceMeta));

        try {
            ResponseBytes responseBytes = rpcClient.invokeSync(unresolvedAddress,
                    requestBytes, config.getInvokeTimeoutMillis(), TimeUnit.MILLISECONDS);

            Notify notifyData = serializer.deserialize(responseBytes.getBody(), Notify.class);
            registerService.notify(notifyData.getServiceMeta(),
                    notifyData.getEvent(),
                    notifyData.getRegisterMetas());
        } catch (Exception e) {
            logger.error("subscribe service fail", e);
        }
    }

    public void unSubscribe(RegisterMeta RegisterMeta) {

    }

    public List<RegisterMeta> lookup(RegisterMeta registerMeta) {

        List<RegisterMeta> registerMetaList = null;

        return registerMetaList;
    }

    class RegisterClientProcess implements RequestProcessor {

        @Override
        public ResponseBytes process(ChannelHandlerContext context, RequestBytes request) {
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
}
