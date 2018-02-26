package org.rpc.rpc.consumer.cluster;

import org.rpc.rpc.Request;
import org.rpc.rpc.consumer.InvokeType;
import org.rpc.rpc.consumer.dispatcher.Dispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FailOverClusterInvoker implements ClusterInvoker {

    private static final Logger logger = LoggerFactory.getLogger(FailOverClusterInvoker.class);

    private final Dispatcher dispatcher;

    /**
     * 重试次数（不包含第一次）
     */
    private final int retries;

    public FailOverClusterInvoker(Dispatcher dispatcher, int retries) {
        this.dispatcher = dispatcher;
        this.retries = (retries < 0) ? 0 : retries;
    }

    @Override
    public Strategy strategy() {
        return Strategy.FAIL_OVER;
    }

    @Override
    public Object invoke(Request request, Class<?> classType, InvokeType invokeType) throws Exception {
        Object result = invoke0(request, 0, classType, invokeType);
        return result;
    }

    private Object invoke0(Request request, int tryCount, Class<?> classType, InvokeType invokeType) throws Exception {
        try {
            tryCount ++;
            return dispatcher.dispatch(request, classType, invokeType);
        } catch (Exception e) {
            if (tryCount <= retries) {
                logger.warn("[FAILOVER] tryCount: {} directory: {}, method: {}",
                        tryCount,
                        request.getRequestWrapper().getServiceMeta().directory(),
                        request.getRequestWrapper().getMethodName());
                return invoke0(request, tryCount, classType, invokeType);
            } else {
                throw e;
            }
        }
    }
}
