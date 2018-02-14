package org.rpc.remoting.netty;

public class NettyClientConfig {

    private static final long CONNECT_TIMEOUT_MILLIS = 3000L;

    private static final String DEFAULT_REMOTE_ADDRESS = "127.0.0.1";

    private static final int DEFAULT_REMOTE_PORT = 9000;

    private String remoteAddress = DEFAULT_REMOTE_ADDRESS;

    private int remotePort = DEFAULT_REMOTE_PORT;

    private long connectTimeoutMillis = CONNECT_TIMEOUT_MILLIS;

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    public long getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    public void setConnectTimeoutMillis(long connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
    }
}
