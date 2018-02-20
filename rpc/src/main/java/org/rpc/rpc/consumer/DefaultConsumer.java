package org.rpc.rpc.consumer;

import io.netty.channel.ChannelHandlerContext;
import org.rpc.comm.UnresolvedAddress;
import org.rpc.register.NotifyListener;
import org.rpc.register.RegisterService;
import org.rpc.register.model.RegisterMeta;
import org.rpc.register.netty.DefaultRegisterService;
import org.rpc.remoting.api.Directory;
import org.rpc.remoting.api.RequestProcessor;
import org.rpc.remoting.api.RpcClient;
import org.rpc.remoting.api.payload.RequestBytes;
import org.rpc.remoting.api.payload.ResponseBytes;
import org.rpc.remoting.netty.NettyClient;
import org.rpc.remoting.netty.NettyClientConfig;
import org.rpc.rpc.model.ServiceMeta;
import org.rpc.serializer.Serializer;
import org.rpc.serializer.SerializerFactory;
import org.rpc.serializer.SerializerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Executors;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.rpc.remoting.api.procotol.ProtocolHead.SUBSCRIBE_RECEIVE;

public class DefaultConsumer implements Consumer {

    private static final Logger logger = LoggerFactory.getLogger(DefaultConsumer.class);

    private String application;

    private RpcClient rpcClient;

    private RegisterService registerService = null;

    public DefaultConsumer(String application, NettyClientConfig nettyClientConfig) {
        this.application = application;
        this.rpcClient = new NettyClient(nettyClientConfig);
        this.rpcClient.registerRequestProcess(new SubscribeProcess(), Executors.newCachedThreadPool());
        this.rpcClient.start();
    }

    @Override
    public RpcClient client() {
        return rpcClient;
    }

    @Override
    public void connect(UnresolvedAddress address) {
        try {
            rpcClient.connect(address);
        } catch (Exception e) {
            logger.error("connect to: {} fail", address, e);
        }
    }

    @Override
    public void subscribe(Directory directory, NotifyListener listener) {
        registerService.subscribe((ServiceMeta) directory, listener);
    }

    @Override
    public void connectToRegistryServer(String address) {
        registerService = new DefaultRegisterService(address);
    }

    @Override
    public List<RegisterMeta> lookup(RegisterMeta registerMeta) {
        checkNotNull(registerService, "please connectToRegistryServer!");
        List<RegisterMeta> registerMetas = registerService.lookup(registerMeta);
        return registerMetas;
    }

    @Override
    public String application() {
        return application;
    }


    class SubscribeProcess implements RequestProcessor {

        @Override
        public ResponseBytes process(ChannelHandlerContext context, RequestBytes request) {
            Serializer serializer = SerializerFactory.serializer(SerializerType.parse(request.getSerializerCode()));
            RegisterMeta registerMeta = serializer.deserialize(request.getBody(), RegisterMeta.class);

            switch (request.getMessageCode()) {
                case SUBSCRIBE_RECEIVE: {
                    DefaultConsumer.this.connect(registerMeta.getAddress());
                    ServiceMeta serviceMeta = new ServiceMeta(registerMeta.getServiceMeta().getGroup(),
                            registerMeta.getServiceMeta().getServiceProviderName(),
                            registerMeta.getServiceMeta().getVersion());
                    DefaultConsumer.this.client().addChannelGroup(serviceMeta, client().group(registerMeta.getAddress()));
                    break;
                }
                default:
                    throw new UnsupportedOperationException("RegisterProcess Unsupported MessageCode: " + request.getMessageCode());
            }
            return null;
        }

        @Override
        public boolean rejectRequest() {
            return false;
        }
    }

}
