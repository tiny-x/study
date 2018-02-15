package org.rpc.rpc.consumer;

import org.rpc.comm.UnresolvedAddress;
import org.rpc.remoting.api.RpcClient;

public interface Consumer {

    RpcClient client() ;

    String application();

    void connect(UnresolvedAddress address);

}
