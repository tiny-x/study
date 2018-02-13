package org.rpc.remoting.netty;

public class NettyServerConfig {

    private static final int DEFAULT_PORT = 9000;

    private int port = DEFAULT_PORT;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
