package org.rpc.register.netty;

import org.rpc.register.netty.process.RegisterProcess;
import org.rpc.remoting.api.RpcServer;
import org.rpc.remoting.netty.NettyServer;
import org.rpc.remoting.netty.NettyServerConfig;

import java.util.concurrent.Executors;

public class DefaultRegisterServer implements RegisterServer {

    private static final int DEFAULT_PORT = 9876;

    private RpcServer rpcServer;

    public DefaultRegisterServer() {
        this(new NettyServerConfig());
    }

    public DefaultRegisterServer(NettyServerConfig config) {
        config.setPort(DEFAULT_PORT);
        RegisterProcess requestProcessor = new RegisterProcess();

        this.rpcServer = new NettyServer(config, requestProcessor.new RegisterChannelEventProcess());
        this.rpcServer.registerRequestProcess(requestProcessor, Executors.newCachedThreadPool());
    }

    @Override
    public void start() {
        rpcServer.start();
    }

}
