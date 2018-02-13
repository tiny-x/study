package org.rpc.remoting.netty;

public class NettyClientConfig {

    private static final long connectTimeoutMillis = 3000L;

    public long getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }
}
