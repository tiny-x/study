package org.rpc.example.register;

import org.rpc.register.netty.DefaultRegisterServer;
import org.rpc.register.netty.RegisterServer;
import org.rpc.remoting.netty.NettyServerConfig;

public class RegisterServerExample {

    public static void main(String[] args) {
        NettyServerConfig config = new NettyServerConfig();
        config.setPort(9876);

        RegisterServer registerServer = new DefaultRegisterServer(config);
        registerServer.start();
    }
}
