package org.rpc.rpc;

import org.rpc.comm.utils.Proxies;
import org.rpc.remoting.api.Directory;
import org.rpc.rpc.consumer.Consumer;

public class ProxyFactory {

    private Directory directory;

    private Class<?> interfaces;

    private Consumer consumer;

    public static ProxyFactory factory(Class<?> interfaces) {

        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.interfaces = interfaces;
        return proxyFactory;
    }

    public ProxyFactory directory(Directory directory) {
        this.directory = directory;
        return this;
    }

    public ProxyFactory consumer(Consumer consumer) {
        this.consumer = consumer;
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T newProxy() {
        return (T) Proxies.getDefault().newProxy(interfaces,
                new SyncInvoker(consumer, directory));
    }

}
