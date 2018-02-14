package org.rpc.remoting.api;

import io.netty.channel.Channel;
import org.rpc.comm.UnresolvedAddress;
import org.rpc.exception.RemotingConnectException;
import org.rpc.exception.RemotingException;
import org.rpc.remoting.api.channel.ChannelGroup;
import org.rpc.remoting.api.payload.RequestBytes;
import org.rpc.remoting.api.payload.ResponseBytes;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public interface RpcClient extends RpcService {

    void connect(UnresolvedAddress address)
            throws InterruptedException, RemotingConnectException;

    ChannelGroup group(UnresolvedAddress address);

    ResponseBytes invokeSync(final Channel channel, final RequestBytes request, long timeout, TimeUnit timeUnit)
            throws RemotingException, InterruptedException;

    void invokeAsync(final Channel channel, final RequestBytes request
            , long timeout, TimeUnit timeUnit, InvokeCallback<ResponseBytes> invokeCallback)
            throws RemotingException, InterruptedException;

    void registerRequestProcess(RequestProcessor requestProcessor, ExecutorService executor);
}
