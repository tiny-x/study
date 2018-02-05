package org.rpc.rpc.future;

import java.util.EventListener;

/**
 * The interface future listener.
 *
 * @param <V> the type parameter
 * @author yefei
 * @date 2017 -07-10 15:03
 */
public interface FutureListener<V> extends EventListener {

    /**
     * Operation completed.
     *
     * @param future the future
     * @throws Exception the exception
     */
    void operationCompleted(Future<V> future) throws Exception;

}
