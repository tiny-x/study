package org.rpc.rpc.consumer.invoke;

import org.rpc.rpc.consumer.InvokeType;
import org.rpc.rpc.consumer.StrategyConfig;
import org.rpc.rpc.consumer.dispatcher.Dispatcher;
import org.rpc.rpc.model.ServiceMeta;

public class GenericInvoke extends AbstractInvoker {

    public GenericInvoke(
            String application,
            Dispatcher dispatcher,
            ServiceMeta serviceMeta,
            StrategyConfig strategyConfig,
            InvokeType invokeType) {
        super(application, dispatcher, serviceMeta, strategyConfig, invokeType);
    }

    public Object $invoke(String methodName, Object... args) throws Throwable{
        return doInvoke(methodName, args);
    }
}
