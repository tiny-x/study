package com.xy.zookeeper.cuator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class PathChildrenCacheListenerTest {

    public static void main(String[] args) throws Exception {

        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("zookeeper.dev.xianglin.com:2181")
                .connectionTimeoutMs(3000)
                .sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        client.start();

        PathChildrenCache childrenCache = new PathChildrenCache(client, "/xlschedule/method/com.xianglin.xlnodecore.shared.DataMigrationService.DataMigration.a174a06a", false);
        childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent)
                    throws Exception {
                System.out.println(pathChildrenCacheEvent.getType());
                System.out.println("path: " + pathChildrenCacheEvent.getData().getPath());
                String s = new String(curatorFramework.getData().forPath("/xlschedule/method/com.xianglin.xlnodecore.shared.DataMigrationService.DataMigration.a174a06a"));
                System.out.println("data: " + s);
            }
        });

        childrenCache.start();
        System.in.read();
    }
}
