package org.rpc.register.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.rpc.register.AbstractRegisterService;
import org.rpc.register.RegisterType;
import org.rpc.register.model.RegisterMeta;
import org.rpc.rpc.model.ServiceMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author yefei
 * @date 2017-06-28 13:07
 */
public class ZookeeperRegisterService extends AbstractRegisterService {

    /**
     * logger
     */
    private final static Logger logger = LoggerFactory.getLogger(ZookeeperRegisterService.class);

    private final int sessionTimeoutMs = 3000;

    private final int connectionTimeoutMs = 3000;
    private final ConcurrentMap<RegisterMeta, PathChildrenCache> pathChildrenCaches = new ConcurrentHashMap<>();
    private CuratorFramework curatorFramework;

    public ZookeeperRegisterService(String connectString) {
        curatorFramework = CuratorFrameworkFactory
                .newClient(connectString, sessionTimeoutMs, connectionTimeoutMs, new ExponentialBackoffRetry(500, 20))
                .usingNamespace("rpc");

        curatorFramework.getConnectionStateListenable().addListener(new ConnectionStateListener() {
            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {
                logger.info("Zookeeper connection state changed {}.", newState);
                if (newState == ConnectionState.RECONNECTED) {
                    logger.info("Zookeeper connection has been re-established, will re-subscribe and re-register.");

                    // 重新发布服务
                    for (RegisterMeta RegisterMeta : providers) {
                        doRegister(RegisterMeta);
                    }
                }
            }
        });
        curatorFramework.start();
    }

    @Override
    public void doRegister(RegisterMeta RegisterMeta) {

    }

    @Override
    public void doUnRegister(RegisterMeta RegisterMeta) {

    }

    @Override
    public void doSubscribe(ServiceMeta serviceMeta) {


    }

    @Override
    public RegisterType registerType() {
        return RegisterType.ZOOKEEPER;
    }

    @Override
    public List<RegisterMeta> lookup(RegisterMeta RegisterMeta) {
        return null;
    }

}
