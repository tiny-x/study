package org.rpc.rpc.consumer;

import org.rpc.comm.UnresolvedAddress;
import org.rpc.exception.RemotingConnectException;
import org.rpc.remoting.api.RpcClient;
import org.rpc.remoting.netty.NettyClient;
import org.rpc.remoting.netty.NettyClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultConsumer implements Consumer {

    private static final Logger logger = LoggerFactory.getLogger(DefaultConsumer.class);

    private String application;

    private RpcClient rpcClient;


    public DefaultConsumer(String application, NettyClientConfig nettyClientConfig) {
        this.application = application;
        this.rpcClient = new NettyClient(nettyClientConfig);
        rpcClient.start();
    }

    @Override
    public RpcClient client() {
        return rpcClient;
    }

    @Override
    public void connect(UnresolvedAddress address) {
        try {
            rpcClient.connect(address);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        } catch (RemotingConnectException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public String application() {
        return application;
    }

}
