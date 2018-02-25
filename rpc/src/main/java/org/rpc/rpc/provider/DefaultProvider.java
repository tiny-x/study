package org.rpc.rpc.provider;

import com.esotericsoftware.reflectasm.MethodAccess;
import io.netty.channel.ChannelHandlerContext;
import org.rpc.comm.UnresolvedAddress;
import org.rpc.register.RegisterFactory;
import org.rpc.register.RegisterService;
import org.rpc.register.RegisterType;
import org.rpc.register.model.RegisterMeta;
import org.rpc.remoting.api.Directory;
import org.rpc.remoting.api.RequestProcessor;
import org.rpc.remoting.api.ResponseStatus;
import org.rpc.remoting.api.RpcServer;
import org.rpc.remoting.api.payload.RequestBytes;
import org.rpc.remoting.api.payload.ResponseBytes;
import org.rpc.remoting.api.procotol.ProtocolHead;
import org.rpc.remoting.netty.NettyServer;
import org.rpc.remoting.netty.NettyServerConfig;
import org.rpc.rpc.container.DefaultServiceProviderContainer;
import org.rpc.rpc.container.ServiceProviderContainer;
import org.rpc.rpc.flow.controller.FlowController;
import org.rpc.rpc.model.RequestWrapper;
import org.rpc.rpc.model.ServiceWrapper;
import org.rpc.rpc.register.local.DefaultServiceRegistry;
import org.rpc.rpc.register.local.ServiceRegistry;
import org.rpc.serializer.Serializer;
import org.rpc.serializer.SerializerFactory;
import org.rpc.serializer.SerializerType;
import org.rpc.utils.InetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

public class DefaultProvider implements Provider {

    private static final Logger logger = LoggerFactory.getLogger(DefaultProvider.class);

    private RpcServer server;

    private ServiceProviderContainer serviceProviderContainer;

    private RegisterService registerService = null;

    private FlowController[] flowControllers;

    public DefaultProvider(NettyServerConfig nettyServerConfig) {
        this.serviceProviderContainer = new DefaultServiceProviderContainer();
        this.server = new NettyServer(nettyServerConfig);
        this.server.registerRequestProcess(new DefaultProviderProcessor(), Executors.newCachedThreadPool());
    }

    @Override
    public void start() {
        server.start();
    }

    @Override
    public void connectToRegistryServer(String address) {
        registerService = RegisterFactory.registerService(RegisterType.DEFAULT);
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
    public void publishService(ServiceWrapper serviceWrapper) {

        RegisterMeta registerMeta = new RegisterMeta();
        registerMeta.setServiceMeta(serviceWrapper.getServiceMeta());
        registerMeta.setConnCount(4);
        registerMeta.setAddress(new UnresolvedAddress(InetUtils.getLocalHost(), 9180));

        registerService.register(registerMeta);
    }

    class DefaultProviderProcessor implements RequestProcessor {

        private ConcurrentHashMap<Class<?>, SoftReference<MethodAccess>> methodAccessCache = new ConcurrentHashMap<>();

        @Override
        public ResponseBytes process(ChannelHandlerContext context, RequestBytes request) {

            SerializerType serializerType = SerializerType.parse(request.getSerializerCode());
            Serializer serializer = SerializerFactory.serializer(serializerType);

            switch (request.getMessageCode()) {
                case ProtocolHead.REQUEST: {
                    RequestWrapper requestWrapper = serializer.deserialize(request.getBody(), RequestWrapper.class);
                    ServiceWrapper serviceWrapper = DefaultProvider.this.lookupService(requestWrapper.getServiceMeta());

                    ResponseBytes responseBytes;
                    if (serviceWrapper == null) {
                        String message = context.channel() + "service: [" + requestWrapper.getServiceMeta() + "] not found";
                        responseBytes = new ResponseBytes(
                                request.getSerializerCode(),
                                serializer.serialize(message));

                        responseBytes.setStatus(ResponseStatus.SERVICE_NOT_FOUND.value());
                        responseBytes.setInvokeId(request.getInvokeId());
                    } else {
                        Class<?> aClass = serviceWrapper.getServiceProvider().getClass();
                        SoftReference<MethodAccess> methodAccess = methodAccessCache.get(aClass);
                        if (methodAccess == null) {
                            SoftReference<MethodAccess> newMethodAccess = new SoftReference<>(MethodAccess.get(aClass));
                            methodAccess = methodAccessCache.putIfAbsent(aClass, newMethodAccess);
                            if (methodAccess == null) {
                                methodAccess = newMethodAccess;
                            }
                        }
                        MethodAccess invoker = methodAccess.get();
                        if (invoker == null) {
                            methodAccessCache.remove(aClass);
                            invoker = MethodAccess.get(aClass);
                        }

                        Object result = invoker
                                .invoke(serviceWrapper.getServiceProvider(),
                                        requestWrapper.getMethodName(),
                                        requestWrapper.getArgs());

                        responseBytes = new ResponseBytes(request.getSerializerCode(), serializer.serialize(result));
                        responseBytes.setInvokeId(request.getInvokeId());
                    }
                    return responseBytes;
                }
                default:
                    throw new UnsupportedOperationException("DefaultProviderProcessor Unsupported MessageCode: " + request.getMessageCode());
            }
        }

        @Override
        public boolean rejectRequest() {
            if (flowControllers != null && flowControllers.length > 0) {
                for (FlowController flowController : flowControllers) {
                    try {
                        flowController.flowController();
                    } catch (RejectedExecutionException e) {
                        logger.error(e.getMessage(), e);
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
