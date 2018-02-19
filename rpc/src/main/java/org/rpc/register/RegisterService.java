package org.rpc.register;

import org.rpc.register.bean.RegisterMeta;

import java.util.List;


public interface RegisterService {

    void register(RegisterMeta registerMeta);

    void unRegister(RegisterMeta RegisterMeta);

    void subscribe(RegisterMeta RegisterMeta, NotifyListener notifyListener);

    void unSubscribe(RegisterMeta RegisterMeta);

    List<RegisterMeta> lookup(RegisterMeta RegisterMeta);
}
