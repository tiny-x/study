package org.rpc.rpc.consumer.dispatcher;

import org.rpc.rpc.Request;

/**
 *
 */
public interface Dispatcher {

    <T> T dispatch(Request request, Class<T> classType);

    Dispatcher timeoutMillis(long timeoutMillis);

}
