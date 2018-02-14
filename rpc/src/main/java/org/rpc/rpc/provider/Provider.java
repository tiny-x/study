package org.rpc.rpc.provider;

import org.rpc.remoting.api.Directory;
import org.rpc.rpc.model.ServiceWrapper;
import org.rpc.rpc.register.ServiceRegistry;

public interface Provider {

    void start();

    ServiceWrapper lookupService(Directory directory);

    ServiceRegistry serviceRegistry();
}
