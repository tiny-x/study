package org.rpc.rpc;

import org.rpc.comm.utils.Proxies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The type Ppc proxy. 生成客户端代理对象
 *
 * @author yefei
 * @date 2017 -06-20 16:01
 */
public class RpcProxyFactory {

    /**
     * logger
     */
    private final static Logger logger = LoggerFactory.getLogger(RpcProxyFactory.class);

    private RpcClient rpcClient;

    private Class<?> interfaces;

    /**
     * Factory ppc proxy factory.
     *
     * @param interfaces the interfaces
     * @return the ppc proxy factory
     */
    public static RpcProxyFactory factory(Class<?> interfaces) {

        RpcProxyFactory rpcProxyFactory = new RpcProxyFactory();
        rpcProxyFactory.interfaces = interfaces;
        return rpcProxyFactory;
    }

    /**
     * Client.
     *
     * @param rpcClient the rpc client
     */
    public RpcProxyFactory client(RpcClient rpcClient) {
        this.rpcClient = rpcClient;
        return this;
    }

    /**
     * New proxy t.
     *
     * @param <T> the type parameter
     * @return the t
     */
    @SuppressWarnings("unchecked")
    public <T> T newProxy() {
        return (T) Proxies.getDefault().newProxy(interfaces, new SyncInvoker(rpcClient));
    }

}
