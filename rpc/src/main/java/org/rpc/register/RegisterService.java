package org.rpc.register;

import org.rpc.comm.UnresolvedAddress;
import org.rpc.register.model.RegisterMeta;
import org.rpc.rpc.model.ServiceMeta;

import java.util.List;


public interface RegisterService {

    void register(RegisterMeta registerMeta);

    void unRegister(RegisterMeta RegisterMeta);

    void subscribe(ServiceMeta serviceMeta, NotifyListener notifyListener);

    void unSubscribe(ServiceMeta serviceMeta);

    List<RegisterMeta> lookup(RegisterMeta RegisterMeta);

    void offlineListening(UnresolvedAddress address, OfflineListener listener);
}
