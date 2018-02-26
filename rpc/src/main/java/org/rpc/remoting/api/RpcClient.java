package org.rpc.remoting.api;

import io.netty.channel.Channel;
import org.rpc.comm.UnresolvedAddress;
import org.rpc.exception.RemotingConnectException;
import org.rpc.exception.RemotingException;
import org.rpc.remoting.api.channel.ChannelGroup;
import org.rpc.remoting.api.payload.RequestCommand;
import org.rpc.remoting.api.payload.ResponseCommand;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

public interface RpcClient extends RpcService {

    void connect(UnresolvedAddress address)
            throws InterruptedException, RemotingConnectException;

    boolean addChannelGroup(Directory directory, UnresolvedAddress address);

    boolean removeChannelGroup(Directory directory, UnresolvedAddress address);

    ChannelGroup group(UnresolvedAddress address);

    boolean hasAvailableChannelGroup(UnresolvedAddress address);

    CopyOnWriteArrayList<ChannelGroup> directory(Directory directory);

    boolean isDirectoryAvailable(Directory directory);

    ResponseCommand invokeSync(final Channel channel, final RequestCommand request, long timeoutMillis)
            throws RemotingException, InterruptedException;

    ResponseCommand invokeSync(final UnresolvedAddress address, final RequestCommand request, long timeoutMillis)
            throws RemotingException, InterruptedException;

    void invokeAsync(final Channel channel, final RequestCommand request
            , long timeoutMillis, InvokeCallback<ResponseCommand> invokeCallback)
            throws RemotingException, InterruptedException;

    void invokeAsync(final UnresolvedAddress address, final RequestCommand request
            , long timeoutMillis, InvokeCallback<ResponseCommand> invokeCallback)
            throws RemotingException, InterruptedException;

    void invokeOneWay(final Channel channel, final RequestCommand request, long timeoutMillis)
            throws RemotingException, InterruptedException;

    void invokeeOneWay(final UnresolvedAddress address, final RequestCommand request, long timeoutMillis)
            throws RemotingException, InterruptedException;

    void registerRequestProcess(RequestProcessor requestProcessor, ExecutorService executor);
}
