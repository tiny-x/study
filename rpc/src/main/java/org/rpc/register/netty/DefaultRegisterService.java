package org.rpc.register.netty;

import org.rpc.register.AbstractRegisterService;
import org.rpc.register.RegisterService;
import org.rpc.register.model.RegisterMeta;
import org.rpc.rpc.model.ServiceMeta;

import java.util.List;

public class DefaultRegisterService extends AbstractRegisterService implements RegisterService {

    private DefaultRegisterClient registerClient;

    public DefaultRegisterService(String address) {
        registerClient = new DefaultRegisterClient(address, this);
    }

    @Override
    public void doRegister(RegisterMeta registerMeta) {
        registerClient.register(registerMeta);
    }

    @Override
    public void doUnRegister(RegisterMeta registerMeta) {

    }

    @Override
    public void doSubscribe(ServiceMeta serviceMeta) {
        registerClient.subscribe(serviceMeta);
    }

    @Override
    public void doUnSubscribe(ServiceMeta serviceMeta) {

    }

    @Override
    public List<RegisterMeta> lookup(RegisterMeta registerMeta) {
        return registerClient.lookup(registerMeta);
    }
}
