package org.rpc.rpc.consumer.invoke;

import org.rpc.rpc.Request;
import org.rpc.rpc.consumer.InvokeType;
import org.rpc.rpc.consumer.StrategyConfig;
import org.rpc.rpc.consumer.cluster.ClusterInvoker;
import org.rpc.rpc.consumer.cluster.FailFastClusterInvoker;
import org.rpc.rpc.consumer.cluster.FailOverClusterInvoker;
import org.rpc.rpc.consumer.dispatcher.Dispatcher;
import org.rpc.rpc.model.RequestWrapper;
import org.rpc.rpc.model.ServiceMeta;

public abstract class AbstractInvoker {

    protected String application;

    protected Dispatcher dispatcher;

    protected ServiceMeta serviceMeta;

    protected StrategyConfig strategyConfig;

    protected InvokeType invokeType;

    public AbstractInvoker(
            String application,
            Dispatcher dispatcher,
            ServiceMeta serviceMeta,
            StrategyConfig strategyConfig,
            InvokeType invokeType) {
        this.application = application;
        this.dispatcher = dispatcher;
        this.serviceMeta = serviceMeta;
        this.strategyConfig = strategyConfig;
        this.invokeType = invokeType;
    }

    public Object doInvoke(String methodName, Object... args) throws Exception {
        RequestWrapper requestWrapper = new RequestWrapper();
        requestWrapper.setApplication(application);
        requestWrapper.setMethodName(methodName);
        requestWrapper.setArgs(args);
        requestWrapper.setServiceMeta(serviceMeta);

        Request request = new Request();
        request.setRequestWrapper(requestWrapper);

        ClusterInvoker clusterInvoker = createClusterInvoker(dispatcher, strategyConfig);
        return clusterInvoker.invoke(request, invokeType);
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
