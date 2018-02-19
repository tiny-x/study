package org.rpc.register.netty;

import org.rpc.remoting.api.RpcServer;

/**
 * 注册中心
 */
public interface RegisterServer {

    void start();

    RpcServer server();
}
