package org.rpc.remoting.future;

import org.rpc.remoting.InvokeCallback;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ResponseFuture<T> {

    private final CompletableFuture<T> future = new CompletableFuture();

    private InvokeCallback<T> invokeCallback;

    public void executeInvokeCallback() throws ExecutionException, InterruptedException {
        if (invokeCallback != null) {
            invokeCallback.operationComplete(this);
        }
    }

    public ResponseFuture() {
    }

    public ResponseFuture(InvokeCallback invokeCallback) {
        this.invokeCallback = invokeCallback;
    }

    public T get() throws ExecutionException, InterruptedException {
        return future.get();
    }

    public T get(long timeout, TimeUnit timeUnit)
            throws ExecutionException, InterruptedException, TimeoutException {
        return future.get(timeout, timeUnit);
    }

    public void complete(T t) {
        future.complete(t);
    }

    public InvokeCallback<T> getInvokeCallback() {
        return invokeCallback;
    }
}
