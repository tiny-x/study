package org.rpc.rpc.provider;

import org.rpc.remoting.api.Directory;
import org.rpc.remoting.api.RpcServer;
import org.rpc.remoting.netty.NettyServer;
import org.rpc.remoting.netty.NettyServerConfig;
import org.rpc.rpc.container.DefaultServiceProviderContainer;
import org.rpc.rpc.container.ServiceProviderContainer;
import org.rpc.rpc.model.ServiceWrapper;
import org.rpc.rpc.processor.DefaultProcessor;
import org.rpc.rpc.register.DefaultServiceRegistry;
import org.rpc.rpc.register.ServiceRegistry;

import java.util.concurrent.Executors;

public class DefaultProvider implements Provider{

    private RpcServer server;

    private ServiceRegistry serviceRegistry;

    private ServiceProviderContainer serviceProviderContainer;

    public DefaultProvider(NettyServerConfig nettyServerConfig) {
        this.serviceProviderContainer = new DefaultServiceProviderContainer();
        serviceRegistry = new DefaultServiceRegistry(serviceProviderContainer);

        this.server = new NettyServer(nettyServerConfig);
        server.registerRequestProcess(new DefaultProcessor(this), Executors.newCachedThreadPool());
    }

    @Override
    public void start() {
        server.start();
    }

    @Override
    public ServiceWrapper lookupService(Directory directory) {
        return null;
    }

    @Override
    public ServiceRegistry serviceRegistry() {
        return null;
    }
}
