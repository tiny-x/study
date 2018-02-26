package org.rpc.register.netty;

import org.rpc.comm.UnresolvedAddress;
import org.rpc.register.AbstractRegisterService;
import org.rpc.register.RegisterService;
import org.rpc.register.RegisterType;
import org.rpc.register.model.RegisterMeta;
import org.rpc.rpc.model.ServiceMeta;
import org.rpc.utils.InetUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkArgument;

public class DefaultRegisterService extends AbstractRegisterService implements RegisterService {

    private ConcurrentMap<UnresolvedAddress, DefaultRegisterClient> regiters = new ConcurrentHashMap<>();

    public DefaultRegisterService() {
    }

    @Override
    public void connectToRegistryServer(String addresses) {
        UnresolvedAddress[] unresolvedAddresses = InetUtils.spiltAddress(addresses);
        for (UnresolvedAddress unresolvedAddress : unresolvedAddresses) {
            DefaultRegisterClient registerClient = new DefaultRegisterClient(unresolvedAddress, this);
            regiters.put(unresolvedAddress, registerClient);
        }
    }

    @Override
    public void doRegister(RegisterMeta registerMeta) {
        checkArgument(!regiters.isEmpty(), "not connect any registry server");

        for (Map.Entry<UnresolvedAddress, DefaultRegisterClient> register : regiters.entrySet()) {
            register.getValue().register(registerMeta);
        }
    }

    @Override
    public void doUnRegister(RegisterMeta registerMeta) {
        checkArgument(!regiters.isEmpty(), "not connect any registry server");

        for (Map.Entry<UnresolvedAddress, DefaultRegisterClient> register : regiters.entrySet()) {
            register.getValue().unRegister(registerMeta);
        }
    }

    @Override
    public void doSubscribe(ServiceMeta serviceMeta) {
        checkArgument(!regiters.isEmpty(), "not connect any registry server");

        for (Map.Entry<UnresolvedAddress, DefaultRegisterClient> register : regiters.entrySet()) {
            register.getValue().subscribe(serviceMeta);
        }
    }

    @Override
    public RegisterType registerType() {
        return RegisterType.DEFAULT;
    }
}
