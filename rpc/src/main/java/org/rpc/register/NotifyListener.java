package org.rpc.register;

import org.rpc.register.model.RegisterMeta;

import java.util.List;

public interface NotifyListener {

    void notify(RegisterMeta registerMeta, NotifyEvent event);
}
