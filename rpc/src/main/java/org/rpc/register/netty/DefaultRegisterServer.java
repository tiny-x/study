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
}
