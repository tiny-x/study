package org.rpc.remoting;

import org.rpc.remoting.future.ResponseFuture;

import java.util.concurrent.ExecutionException;

public interface InvokeCallback<T> {

    void operationComplete(final ResponseFuture<T> responseFuture) throws ExecutionException, InterruptedException;
}
