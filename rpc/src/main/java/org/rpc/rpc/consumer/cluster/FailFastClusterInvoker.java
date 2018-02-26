package org.rpc.rpc.consumer.cluster;

import org.rpc.rpc.Request;
import org.rpc.rpc.consumer.InvokeType;
import org.rpc.rpc.consumer.dispatcher.Dispatcher;

public class FailFastClusterInvoker implements ClusterInvoker {

    private final Dispatcher dispatcher;

    public FailFastClusterInvoker(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public Strategy strategy() {
        return Strategy.FAIL_FAST;
    }

    @Override
    public Object invoke(Request request, Class<?> classType, InvokeType invokeType) throws Exception {
        return dispatcher.dispatch(request, classType, invokeType);
    }
}
