package org.rpc.rpc.consumer.dispatcher;

import org.rpc.exception.RemotingException;
import org.rpc.rpc.Request;
import org.rpc.rpc.consumer.InvokeType;

/**
 *
 */
public interface Dispatcher {

    <T> T dispatch(Request request, InvokeType invokeType) throws RemotingException, InterruptedException;

    Dispatcher timeoutMillis(long timeoutMillis);

}
