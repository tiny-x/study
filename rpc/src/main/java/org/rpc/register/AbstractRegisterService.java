package org.rpc.register;

import org.rpc.comm.collection.ConcurrentSet;
import org.rpc.register.model.RegisterMeta;
import org.rpc.rpc.model.ServiceMeta;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class AbstractRegisterService implements RegisterService {

    /**
     * 订阅者监听器
     */
    private final ConcurrentMap<ServiceMeta, NotifyListener> subscribeListeners = new ConcurrentHashMap<>();

    /**
     * 服务提供者
     */
    protected final ConcurrentSet<RegisterMeta> providers = new ConcurrentSet<>();

    /**
     * 已经订阅的服务
     */
    protected final ConcurrentSet<ServiceMeta> consumers = new ConcurrentSet<>();


    @Override
    public void register(RegisterMeta registerMeta) {
        providers.add(registerMeta);
        doRegister(registerMeta);
    }

    @Override
    public void unRegister(RegisterMeta registerMeta) {
        consumers.remove(registerMeta);
        doUnRegister(registerMeta);
    }

    @Override
    public void subscribe(ServiceMeta serviceMeta, NotifyListener notifyListener) {
        subscribeListeners.put(serviceMeta, notifyListener);
        consumers.add(serviceMeta);
        doSubscribe(serviceMeta);
    }

    @Override
    public void unSubscribe(ServiceMeta serviceMeta) {
        subscribeListeners.remove(serviceMeta);
        doUnSubscribe(serviceMeta);
    }

    public void notify(ServiceMeta serviceMeta, NotifyEvent event, List<RegisterMeta> registerMetas) {
        if (registerMetas != null && registerMetas.size() > 0) {
            NotifyListener notifyListener = subscribeListeners.get(serviceMeta);
            for (RegisterMeta registerMeta : registerMetas) {
                notifyListener.notify(registerMeta, event);
            }
        }
    }

    protected abstract void doRegister(RegisterMeta registerMeta);

    protected abstract void doUnRegister(RegisterMeta registerMeta);

    protected abstract void doSubscribe(ServiceMeta serviceMeta);

    protected abstract void doUnSubscribe(ServiceMeta serviceMeta);


}
