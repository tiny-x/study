package org.rpc.rpc.provider;

import org.rpc.comm.UnresolvedAddress;
import org.rpc.register.RegisterFactory;
import org.rpc.register.RegisterService;
import org.rpc.register.RegisterType;
import org.rpc.register.model.RegisterMeta;
import org.rpc.remoting.api.Directory;
import org.rpc.remoting.api.RpcServer;
import org.rpc.remoting.netty.NettyServer;
import org.rpc.remoting.netty.NettyServerConfig;
import org.rpc.rpc.container.DefaultServiceProviderContainer;
import org.rpc.rpc.container.ServiceProviderContainer;
import org.rpc.rpc.exector.ExectorFactory;
import org.rpc.rpc.exector.ThreadPoolExectorFactory;
import org.rpc.rpc.flow.controller.FlowController;
import org.rpc.rpc.model.ServiceWrapper;
import org.rpc.rpc.provider.process.DefaultProviderProcessor;
import org.rpc.rpc.register.local.DefaultServiceRegistry;
import org.rpc.rpc.register.local.ServiceRegistry;
import org.rpc.utils.InetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultProvider implements Provider {

    private RpcServer server;

    private ServiceProviderContainer serviceProviderContainer;

    private RegisterService registerService = null;

    private FlowController[] flowControllers;

    private NettyServerConfig config;

    private ExectorFactory exectorFactory = new ThreadPoolExectorFactory();

    public DefaultProvider(NettyServerConfig nettyServerConfig) {
        this.config = nettyServerConfig;
        this.serviceProviderContainer = new DefaultServiceProviderContainer();
        this.server = new NettyServer(nettyServerConfig);
    }

    @Override
    public void start() {
        this.server.start();
        this.server.registerRequestProcess(new DefaultProviderProcessor(this), exectorFactory.createExecutorService());
    }

    @Override
    public void connectToRegistryServer(String addressess) {
        registerService = RegisterFactory.registerService(RegisterType.DEFAULT);
        registerService.connectToRegistryServer(addressess);
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
    public void registerGlobalFlowController(FlowController... flowControllers) {
        this.flowControllers = flowControllers;
    }

    @Override
    public FlowController[] globalFlowController() {
        return flowControllers;
    }

    @Override
    public void publishService(ServiceWrapper serviceWrapper) {

        RegisterMeta registerMeta = new RegisterMeta();
        registerMeta.setServiceMeta(serviceWrapper.getServiceMeta());
        registerMeta.setConnCount(config.getConnCount());
        registerMeta.setAddress(new UnresolvedAddress(InetUtils.getLocalHost(), config.getPort()));
        registerMeta.setWeight(serviceWrapper.getWeight());

        registerService.register(registerMeta);
    }


}
