package org.rpc.rpc.consumer.cluster;

import org.rpc.rpc.Request;
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
    public Object invoke(Request request, Class<?> classType, boolean sync) throws Exception {
        return dispatcher.dispatch(request, classType, sync);
    }
}
