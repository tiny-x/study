package org.rpc.rpc;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.rpc.rpc.consumer.dispatcher.Dispatcher;
import org.rpc.rpc.model.RequestWrapper;
import org.rpc.rpc.model.ServiceMeta;

import java.lang.reflect.Method;

public class SyncInvoker {

    private Dispatcher dispatcher;

    private ServiceMeta serviceMeta;

    public SyncInvoker(Dispatcher dispatcher, ServiceMeta serviceMeta) {
        this.dispatcher = dispatcher;
        this.serviceMeta = serviceMeta;
    }

    @RuntimeType
    public Object invoke(@Origin Method method, @AllArguments @RuntimeType Object[] args) throws Throwable {

        RequestWrapper requestWrapper = new RequestWrapper();
        requestWrapper.setMethodName(method.getName());
        requestWrapper.setArgs(args);
        requestWrapper.setServiceMeta(serviceMeta);

        Request request = new Request();
        request.setRequestWrapper(requestWrapper);

        return dispatcher.dispatch(request, method.getReturnType());

    }
}
