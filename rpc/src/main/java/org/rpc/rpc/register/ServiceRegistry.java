package org.rpc.rpc.register;

import org.rpc.rpc.model.ServiceWrapper;

/**
 * 本地服务注册.
 */
public interface ServiceRegistry {

    /**
     * 设置服务权重(0 < weight <= 100).
     */
    ServiceRegistry weight(int weight);

    /**
     * 注册服务到本地容器.
     */
    ServiceWrapper register();
}