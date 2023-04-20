package com.xy.zookeeper.cuator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.server.SessionTracker;

public class PathChildrenCacheListenerTest {

    public static void main(String[] args) throws Exception {

        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("10.10.228.213:2182")
                .connectionTimeoutMs(3000)
                .sessionTimeoutMs(20000)
                .namespace("dubbo")
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        client.start();

        //PathChildrenCache childrenCache = new PathChildrenCache(client, "/yy", false);
        PathChildrenCache childrenCache = new PathChildrenCache(client, "/com.xy.dubbo.demo.HelloService/providers", false);
        //PathChildrenCache childrenCache = new PathChildrenCache(client, "/jupiter/provider/test/org.jupiter.example.ServiceTest/1.0.0.daily", false);
        childrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent)
                    throws Exception {

                System.out.println(pathChildrenCacheEvent.getType());
                System.out.println("path: " + pathChildrenCacheEvent.getData().getPath());
                System.out.println("------------------");

            }
        });

        childrenCache.start();
        System.in.read();
    }
}
