package org.rpc.register.netty;

import org.rpc.register.AbstractRegisterService;
import org.rpc.register.RegisterService;
import org.rpc.register.bean.RegisterMeta;

import java.util.List;

public class DefaultRegisterService extends AbstractRegisterService implements RegisterService {

    private DefaultRegisterClient registerClient;

    public DefaultRegisterService(String address) {
        registerClient = new DefaultRegisterClient(address);
    }

    @Override
    public void doRegister(RegisterMeta registerMeta) {
        registerClient.register(registerMeta);
    }

    @Override
    public void doUnRegister(RegisterMeta registerMeta) {

    }

    @Override
    public void doSubscribe(RegisterMeta registerMeta) {
        registerClient.subscribe(registerMeta);
    }

    @Override
    public void doUnSubscribe(RegisterMeta RegisterMeta) {

    }

    @Override
    public List<RegisterMeta> lookup(RegisterMeta registerMeta) {
        return registerClient.lookup(registerMeta);
    }
}
