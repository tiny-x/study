package org.rpc.remoting.api;

import io.netty.channel.Channel;
import org.rpc.remoting.api.future.ResponseFuture;
import org.rpc.remoting.api.payload.RequestBytes;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public interface RpcServer extends RpcService {

    void invokeSync(final Channel channel, final RequestBytes request
                    , long timeout, TimeUnit timeUnit);

    void invokeAsync(final Channel channel, final RequestBytes request
            , long timeout, TimeUnit timeUnit, InvokeCallback<ResponseFuture> invokeCallback) throws Exception;

    void registerRequestProcess(RequestProcessor requestProcessor, ExecutorService executor);

}
