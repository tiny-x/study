package org.rpc.rpc.provider;

import org.rpc.comm.UnresolvedAddress;
import org.rpc.register.RegisterService;
import org.rpc.register.model.RegisterMeta;
import org.rpc.register.netty.DefaultRegisterService;
import org.rpc.remoting.api.Directory;
import org.rpc.remoting.api.RpcServer;
import org.rpc.remoting.netty.NettyServer;
import org.rpc.remoting.netty.NettyServerConfig;
import org.rpc.rpc.container.DefaultServiceProviderContainer;
import org.rpc.rpc.container.ServiceProviderContainer;
import org.rpc.rpc.model.ServiceWrapper;
import org.rpc.rpc.processor.DefaultProviderProcessor;
import org.rpc.rpc.register.local.DefaultServiceRegistry;
import org.rpc.rpc.register.local.ServiceRegistry;
import org.rpc.utils.InetUtils;

import java.util.concurrent.Executors;

public class DefaultProvider implements Provider{

    private RpcServer server;

    private ServiceProviderContainer serviceProviderContainer;

    private RegisterService registerService = null;

    public DefaultProvider(NettyServerConfig nettyServerConfig) {
        this.serviceProviderContainer = new DefaultServiceProviderContainer();
        this.server = new NettyServer(nettyServerConfig);
        this.server.registerRequestProcess(new DefaultProviderProcessor(this), Executors.newCachedThreadPool());
    }

    @Override
    public void start() {
        server.start();
    }

    @Override
    public void connectToRegistryServer(String address) {
        registerService = new DefaultRegisterService(address);
    }

    @Override
    public ServiceWrapper lookupService(Directory directory) {
        return serviceProviderContainer.lookupService(directory.directory());
    }

    @Override
    public ServiceRegistry serviceRegistry() {
        return new DefaultServiceRegistry(serviceProviderContainer);
    }

    @Override
    public void publishService(ServiceWrapper serviceWrapper) {

        RegisterMeta registerMeta = new RegisterMeta();
        registerMeta.setServiceMeta(serviceWrapper.getServiceMeta());
        registerMeta.setConnCount(4);
        registerMeta.setAddress(new UnresolvedAddress(InetUtils.getLocalHost(), 9180));

        registerService.register(registerMeta);
    }
}
