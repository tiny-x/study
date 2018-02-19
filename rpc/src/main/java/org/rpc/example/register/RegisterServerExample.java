package org.rpc.example.register;

import org.rpc.register.netty.DefaultRegisterServer;
import org.rpc.register.netty.RegisterServer;

public class RegisterServerExample {

    public static void main(String[] args) {

        RegisterServer registerServer = new DefaultRegisterServer();
        registerServer.start();
    }
}
