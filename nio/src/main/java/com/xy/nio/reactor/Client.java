package com.xy.nio.reactor;

import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author yefei
 * @date 2018-06-20 11:09
 */
public class Client {

    public static void main(String[] args) throws Exception {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("127.0.0.1", 9222));

    }
}
