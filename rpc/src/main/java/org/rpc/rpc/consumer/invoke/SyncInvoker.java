package org.rpc.rpc.consumer.invoke;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.rpc.rpc.Request;
import org.rpc.rpc.consumer.StrategyConfig;
import org.rpc.rpc.consumer.cluster.ClusterInvoker;
import org.rpc.rpc.consumer.cluster.FailFastClusterInvoker;
import org.rpc.rpc.consumer.cluster.FailOverClusterInvoker;
import org.rpc.rpc.consumer.dispatcher.Dispatcher;
import org.rpc.rpc.model.RequestWrapper;
import org.rpc.rpc.model.ServiceMeta;

import java.lang.reflect.Method;

public class SyncInvoker {

    private Dispatcher dispatcher;

    private ServiceMeta serviceMeta;

    private StrategyConfig strategyConfig;

    private boolean sync;

    public SyncInvoker(Dispatcher dispatcher,
                       ServiceMeta serviceMeta,
                       StrategyConfig strategyConfig,
                       boolean sync) {
        this.dispatcher = dispatcher;
        this.serviceMeta = serviceMeta;
        this.strategyConfig = strategyConfig;
        this.sync = sync;
    }

    @RuntimeType
    public Object invoke(@Origin Method method, @AllArguments @RuntimeType Object[] args) throws Throwable {

        RequestWrapper requestWrapper = new RequestWrapper();
        requestWrapper.setMethodName(method.getName());
        requestWrapper.setArgs(args);
        requestWrapper.setServiceMeta(serviceMeta);

        Request request = new Request();
        request.setRequestWrapper(requestWrapper);
        ClusterInvoker clusterInvoker = createClusterInvoker(dispatcher, strategyConfig);

        return clusterInvoker.invoke(request, method.getReturnType(), sync);
    }


    private ClusterInvoker createClusterInvoker(Dispatcher dispatcher, StrategyConfig strategy) {
        ClusterInvoker.Strategy s = strategy.getStrategy();
        switch (s) {
            case FAIL_FAST:
                return new FailFastClusterInvoker(dispatcher);
            case FAIL_OVER:
                return new FailOverClusterInvoker(dispatcher, strategy.getRetries());
            default:
                throw new UnsupportedOperationException("strategy: " + strategy);
        }
    }
}
