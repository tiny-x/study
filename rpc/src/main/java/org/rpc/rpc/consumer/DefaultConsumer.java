package org.rpc.rpc.consumer;

import org.rpc.comm.UnresolvedAddress;
import org.rpc.register.*;
import org.rpc.register.model.RegisterMeta;
import org.rpc.remoting.api.Directory;
import org.rpc.remoting.api.RpcClient;
import org.rpc.remoting.netty.NettyClient;
import org.rpc.remoting.netty.NettyClientConfig;
import org.rpc.rpc.model.ServiceMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class DefaultConsumer implements Consumer {

    private static final Logger logger = LoggerFactory.getLogger(DefaultConsumer.class);

    private String application;

    private RpcClient rpcClient;

    private RegisterService registerService = null;

    public DefaultConsumer(String application, NettyClientConfig nettyClientConfig) {
        this.application = application;
        this.rpcClient = new NettyClient(nettyClientConfig);
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
    public void offlineListening(UnresolvedAddress address, OfflineListener listener) {
        registerService.offlineListening(address, listener);
    }

    @Override
    public void connectToRegistryServer(String address) {
        registerService = RegisterFactory.registerService(RegisterType.DEFAULT);
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

    @Override
    public RegisterService registerService() {
        return registerService;
    }
}
