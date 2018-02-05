
package org.rpc.rpc;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.rpc.comm.bean.RpcRequest;
import org.rpc.comm.bean.RpcResponse;
import org.rpc.rpc.future.Future;
import org.rpc.rpc.future.FutureListener;
import org.rpc.rpc.future.InvokeFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.SynchronousQueue;

/**
 * The type Sync invoker.
 *
 * @author yefei
 * @date 2017 -6-26 11:02:11
 */
public class SyncInvoker {

    /**
     * logger
     */
    private final static Logger logger = LoggerFactory.getLogger(SyncInvoker.class);

    private RpcClient rpcClient;

    /**
     * Instantiates a new Sync invoker.
     *
     * @param rpcClient the rpc client
     */
    public SyncInvoker(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
    }

    /**
     * Invoke object.
     *
     * @param method the method
     * @param args   the args
     * @return the object
     * @throws Throwable the throwable
     */
    @RuntimeType
    public Object invoke(@Origin Method method, @AllArguments @RuntimeType Object[] args) throws Throwable {

        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setInterfaceName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);

        InvokeFuture<RpcResponse> invokeFuture = new InvokeFuture();
        invokeFuture.addListener(new FutureListener<RpcResponse>() {
            @Override
            public void operationCompleted(Future<RpcResponse> future) throws Exception {
                // TODO
                logger.debug("provider response: {}, body {}", future.get().getRequestId(), future.get().getResult());
            }
        });

        rpcClient.invoke(request, invokeFuture);

        ChannelFuture channelFuture = rpcClient.getChannelGroup().get(rpcClient.getUnresolvedAddress().toString());

        channelFuture.channel().writeAndFlush(request);
        RpcResponse response = invokeFuture.get();

        return response.getResult();
    }
}
