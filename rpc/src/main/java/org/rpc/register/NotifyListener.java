package org.rpc.register;

import org.rpc.register.bean.RegisterMeta;

import java.util.List;

public interface NotifyListener {

    void notify(List<RegisterMeta> registerMetas);
}
