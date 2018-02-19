package org.rpc.register;

import org.rpc.comm.collection.ConcurrentSet;
import org.rpc.register.bean.RegisterMeta;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class AbstractRegisterService implements RegisterService {

    private final ReentrantReadWriteLock registriesLock = new ReentrantReadWriteLock();

    /**
     * 订阅者监听器
     */
    private final ConcurrentMap<RegisterMeta, NotifyListener> subscribeListeners = new ConcurrentHashMap<>();

    /**
     * 服务提供者
     */
    protected final ConcurrentSet<RegisterMeta> providers = new ConcurrentSet<>();

    /**
     * 服务订阅者
     */
    protected final ConcurrentSet<RegisterMeta> consumers = new ConcurrentSet<>();


    @Override
    public void register(RegisterMeta RegisterMeta) {
        providers.add(RegisterMeta);
        doRegister(RegisterMeta);
    }

    @Override
    public void unRegister(RegisterMeta RegisterMeta) {
        consumers.remove(RegisterMeta);
        doUnRegister(RegisterMeta);
    }

    @Override
    public void subscribe(RegisterMeta RegisterMeta, NotifyListener notifyListener) {
        subscribeListeners.putIfAbsent(RegisterMeta, notifyListener);
        doSubscribe(RegisterMeta);
    }

    @Override
    public void unSubscribe(RegisterMeta RegisterMeta) {
        subscribeListeners.remove(RegisterMeta);
        doUnSubscribe(RegisterMeta);
    }

    protected void notify(List<RegisterMeta> RegisterMetas) {

    }

    protected abstract void doRegister(RegisterMeta RegisterMeta);

    protected abstract void doUnRegister(RegisterMeta RegisterMeta);

    protected abstract void doSubscribe(RegisterMeta RegisterMeta);

    protected abstract void doUnSubscribe(RegisterMeta RegisterMeta);


}
