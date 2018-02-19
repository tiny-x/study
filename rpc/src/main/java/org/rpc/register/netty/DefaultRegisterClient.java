package org.rpc.register.netty;

import org.rpc.comm.UnresolvedAddress;
import org.rpc.comm.collection.ConcurrentSet;
import org.rpc.register.bean.RegisterMeta;
import org.rpc.remoting.api.RpcClient;
import org.rpc.remoting.api.payload.RequestBytes;
import org.rpc.remoting.api.payload.ResponseBytes;
import org.rpc.remoting.api.procotol.ProtocolHead;
import org.rpc.remoting.netty.NettyClient;
import org.rpc.remoting.netty.NettyClientConfig;
import org.rpc.serializer.Serializer;
import org.rpc.serializer.SerializerFactory;
import org.rpc.serializer.SerializerType;
import org.rpc.utils.InetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class DefaultRegisterClient {

    private static final Logger logger = LoggerFactory.getLogger(DefaultRegisterClient.class);

    private UnresolvedAddress unresolvedAddress = null;

    private RpcClient rpcClient;

    private NettyClientConfig config = new NettyClientConfig();

    private ConcurrentMap<String, ConcurrentSet<RegisterMeta>> providerMap = new ConcurrentHashMap<>();

    public DefaultRegisterClient(String address) {
        // TODO 先按一个注册中心写完功能
        UnresolvedAddress[] addrsses = InetUtils.spiltAddrss(address);
        unresolvedAddress = addrsses[0];

        this.rpcClient = new NettyClient(config);
        rpcClient.start();
        try {
            rpcClient.connect(unresolvedAddress);
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
            rpcClient.invokeSync(rpcClient.group(unresolvedAddress).next(),
                    requestBytes, config.getInvokeTimeoutMillis(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            logger.error("register service fail", e);
        }

    }

    public void unRegister(RegisterMeta registerMeta) {

    }

    public void subscribe(RegisterMeta registerMeta) {
        Serializer serializer = SerializerFactory.serializer(SerializerType.PROTO_STUFF);

        RequestBytes requestBytes = new RequestBytes(ProtocolHead.SUBSCRIBE_SERVICE,
                SerializerType.PROTO_STUFF.value(),
                serializer.serialize(registerMeta));

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

        try {
            rpcClient.invokeSync(rpcClient.group(unresolvedAddress).next(),
                    requestBytes, config.getInvokeTimeoutMillis(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            logger.error("subscribe service fail", e);
        }
    }

    public void unSubscribe(RegisterMeta RegisterMeta) {

    }

    public List<RegisterMeta> lookup(RegisterMeta registerMeta) {
        Serializer serializer = SerializerFactory.serializer(SerializerType.PROTO_STUFF);

        RequestBytes requestBytes = new RequestBytes(ProtocolHead.LOOKUP_SERVICE,
                SerializerType.PROTO_STUFF.value(),
                serializer.serialize(registerMeta));

        List<RegisterMeta> registerMetaList = null;
        try {
            ResponseBytes responseBytes = rpcClient.invokeSync(rpcClient.group(unresolvedAddress).next(),
                    requestBytes, config.getInvokeTimeoutMillis(), TimeUnit.MILLISECONDS);

            registerMetaList = serializer.deserialize(responseBytes.getBody(), ArrayList.class);
        } catch (Exception e) {
            logger.error("lookup service fail", e);
        }
        return registerMetaList;
    }
}
