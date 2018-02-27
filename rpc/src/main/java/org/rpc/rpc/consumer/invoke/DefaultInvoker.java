package org.rpc.rpc.consumer.invoke;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.rpc.rpc.consumer.InvokeType;
import org.rpc.rpc.consumer.StrategyConfig;
import org.rpc.rpc.consumer.dispatcher.Dispatcher;
import org.rpc.rpc.model.ServiceMeta;

import java.lang.reflect.Method;

public class DefaultInvoker extends AbstractInvoker {

    public DefaultInvoker(
                String application,
                Dispatcher dispatcher,
                ServiceMeta serviceMeta,
                StrategyConfig strategyConfig,
                InvokeType invokeType) {
        super(application, dispatcher, serviceMeta, strategyConfig, invokeType);
    }

    @RuntimeType
    public Object invoke(@Origin Method method, @AllArguments @RuntimeType Object[] args) throws Throwable {
        return doInvoke(method.getName(), args);
    }

}
