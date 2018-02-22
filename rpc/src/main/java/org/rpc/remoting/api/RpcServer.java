package org.rpc.remoting.api;

import io.netty.channel.Channel;
import org.rpc.exception.RemotingException;
import org.rpc.remoting.api.payload.RequestBytes;
import org.rpc.remoting.api.payload.ResponseBytes;

import java.util.concurrent.ExecutorService;

public interface RpcServer extends RpcService {

    void invokeSync(final Channel channel, final RequestBytes request, long timeoutMillis) throws RemotingException, InterruptedException;

    void invokeAsync(final Channel channel, final RequestBytes request
            , long timeoutMillis, InvokeCallback<ResponseBytes> invokeCallback) throws RemotingException, InterruptedException;

    void registerRequestProcess(RequestProcessor requestProcessor, ExecutorService executor);

}
