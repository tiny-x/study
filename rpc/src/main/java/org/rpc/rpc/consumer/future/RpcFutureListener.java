package org.rpc.rpc.consumer.future;

public interface RpcFutureListener<T> {

    void complete(T result);

    void failure(Throwable cause);
}
