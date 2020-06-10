package com.xy.zookeeper.cuator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

public class Demo {

    public static void main(String[] args) throws Exception {
//        CuratorFramework client = CuratorFrameworkFactory.builder()
//                .connectString("121.43.175.216:2181")
//                .connectionTimeoutMs(3000)
//                .sessionTimeoutMs(5000)
//                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
//                .build();

        CuratorFramework client = CuratorFrameworkFactory.newClient(
                "121.43.175.216:2181", 60000, 15000, new ExponentialBackoffRetry(500, 20));
        client.start();

        client.create().withMode(CreateMode.EPHEMERAL).forPath("/yy/demo");

        Runtime.getRuntime().addShutdownHook(new Thread(client::close));
        System.in.read();
    }
}
