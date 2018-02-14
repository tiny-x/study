package org.rpc.rpc;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.rpc.comm.bean.RpcRequest;
import org.rpc.remoting.api.Directory;
import org.rpc.rpc.consumer.Consumer;

import java.lang.reflect.Method;

public class SyncInvoker {

    private Consumer consumer;

    private Directory directory;

    public SyncInvoker(Consumer consumer, Directory directory) {
        this.consumer = consumer;
        this.directory = directory;
    }

    @RuntimeType
    public Object invoke(@Origin Method method, @AllArguments @RuntimeType Object[] args) throws Throwable {

        RpcRequest request = new RpcRequest();

        request.setInterfaceName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);


        return null;
    }
}
