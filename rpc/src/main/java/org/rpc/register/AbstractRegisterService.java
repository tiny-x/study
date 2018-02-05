package org.rpc.register;

import org.rpc.comm.collection.ConcurrentSet;
import org.rpc.register.bean.URL;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author yefei
 * @date 2017-06-28 10:56
 */
public abstract class AbstractRegisterService implements RegisterService {

    private final ReentrantReadWriteLock registriesLock = new ReentrantReadWriteLock();

    /**
     * 订阅者监听器
     */
    private final ConcurrentMap<URL, NotifyListener> subscribeListeners = new ConcurrentHashMap<>();

    /**
     * 服务提供者
     */
    protected final ConcurrentSet<URL> providers = new ConcurrentSet<>();

    /**
     * 服务订阅者
     */
    protected final ConcurrentSet<URL> consumers = new ConcurrentSet<>();

    @Override
    public void register(URL url) {
        providers.add(url);
        doRegister(url);
    }

    @Override
    public void unRegister(URL url) {
        consumers.remove(url);
        doUnRegister(url);
    }

    @Override
    public void subscribe(URL url, NotifyListener notifyListener) {
        subscribeListeners.putIfAbsent(url, notifyListener);
        doSubscribe(url);
    }

    @Override
    public void unSubscribe(URL url) {
        subscribeListeners.remove(url);
        doUnSubscribe(url);
    }

    protected void notify(List<URL> urls) {

    }

    protected abstract void doRegister(URL url);

    protected abstract void doUnRegister(URL url);

    protected abstract void doSubscribe(URL url);

    protected abstract void doUnSubscribe(URL url);


}
