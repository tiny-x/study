package org.rpc.remoting.api;

import org.rpc.remoting.api.future.ResponseFuture;

import java.util.concurrent.ExecutionException;

public interface InvokeCallback<T> {

    void operationComplete(final ResponseFuture<T> responseFuture) throws ExecutionException, InterruptedException;
}
