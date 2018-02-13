package org.rpc.remoting;

import org.rpc.comm.UnresolvedAddress;
import org.rpc.remoting.payload.RequestBytes;
import org.rpc.remoting.payload.ResponseBytes;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public interface RpcClient extends RpcService {

    ResponseBytes invokeSync(final UnresolvedAddress address, final RequestBytes request
            , long timeout, TimeUnit timeUnit) throws Exception;

    void invokeAsync(final UnresolvedAddress address, final RequestBytes request
            , long timeout, TimeUnit timeUnit, InvokeCallback<ResponseBytes> invokeCallback) throws Exception;

    void registerRequestProcess(RequestProcessor requestProcessor, ExecutorService executor);
}
