package org.rpc.rpc.consumer.dispatcher;

import org.rpc.exception.RemotingException;
import org.rpc.rpc.Request;

/**
 *
 */
public interface Dispatcher {

    <T> T dispatch(Request request, Class<T> classType, boolean sync) throws RemotingException, InterruptedException;

    Dispatcher timeoutMillis(long timeoutMillis);

}
