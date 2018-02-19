package org.rpc.remoting.netty;

public class NettyClientConfig {

    private static final long CONNECT_TIMEOUT_MILLIS = 3000L;

    private static final long INVOKE_TIMEOUT_MILLIS = 3000L;

    private long connectTimeoutMillis = CONNECT_TIMEOUT_MILLIS;

    private long invokeTimeoutMillis = INVOKE_TIMEOUT_MILLIS;

    public long getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    public void setConnectTimeoutMillis(long connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
    }

    public long getInvokeTimeoutMillis() {
        return invokeTimeoutMillis;
    }

    public void setInvokeTimeoutMillis(long invokeTimeoutMillis) {
        this.invokeTimeoutMillis = invokeTimeoutMillis;
    }
}
