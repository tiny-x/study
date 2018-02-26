package org.rpc.remoting.api;

import io.netty.channel.Channel;
import org.rpc.exception.RemotingException;
import org.rpc.remoting.api.payload.RequestCommand;
import org.rpc.remoting.api.payload.ResponseCommand;

import java.util.concurrent.ExecutorService;

public interface RpcServer extends RpcService {

    void invokeSync(final Channel channel, final RequestCommand request, long timeoutMillis) throws RemotingException, InterruptedException;

    void invokeAsync(final Channel channel, final RequestCommand request
            , long timeoutMillis, InvokeCallback<ResponseCommand> invokeCallback) throws RemotingException, InterruptedException;

    void invokeOneWay(final Channel channel, final RequestCommand request, long timeoutMillis)
            throws RemotingException, InterruptedException;

    void registerRequestProcess(RequestProcessor requestProcessor, ExecutorService executor);

}
