package org.rpc.register.zookeeperRegister;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.rpc.register.AbstractRegisterService;
import org.rpc.register.bean.DirectoryEnums;
import org.rpc.register.bean.URL;
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
    private final ConcurrentMap<URL, PathChildrenCache> pathChildrenCaches = new ConcurrentHashMap<>();
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
                    // 重新订阅
                    for (URL url : consumers) {
                        doSubscribe(url);
                    }
                    // 重新发布服务
                    for (URL url : providers) {
                        doRegister(url);
                    }
                }
            }
        });
        curatorFramework.start();
    }

    @Override
    public void doRegister(URL url) {
        try {
            if (curatorFramework.checkExists().forPath(url.getInterfaceName()) == null) {
                curatorFramework.create().forPath(url.getInterfaceName());
                for (DirectoryEnums directoryEnums : DirectoryEnums.values()) {
                    curatorFramework.create().forPath(url.getInterfaceName() + "/" + directoryEnums.path);
                }
            }
            curatorFramework.create().withMode(CreateMode.EPHEMERAL)
                    .forPath(url.getInterfaceName() + "/" + DirectoryEnums.PROVIDERS.path + "/" + url.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void doUnRegister(URL url) {
        try {
            curatorFramework.delete().deletingChildrenIfNeeded()
                    .forPath(url.getInterfaceName() + "/" + DirectoryEnums.PROVIDERS.path + "/" + url.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void doSubscribe(URL url) {
        PathChildrenCache newChildrenCache = new PathChildrenCache(curatorFramework,
                url.getInterfaceName() + "/" + DirectoryEnums.PROVIDERS.path, false);
        pathChildrenCaches.putIfAbsent(url, newChildrenCache).getListenable()
                .addListener(new PathChildrenCacheListener() {
                    @Override
                    public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                        List<String> children = client.getChildren().forPath(event.getData().getPath());
//TODO
                        ZookeeperRegisterService.this.notify();
                    }
                });

    }

    @Override
    public void doUnSubscribe(URL url) {
        try {
            pathChildrenCaches.get(url).clearAndRefresh();
            pathChildrenCaches.remove(url);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public List<URL> lookup(URL url) {
        return null;
    }

}
