package com.xy.zookeeper.cuator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

public class Demo {

    public static void main(String[] args) throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .connectionTimeoutMs(3000)
                .sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        client.start();

        if (client.checkExists().forPath("/demo") == null) {
            client.create().withMode(CreateMode.EPHEMERAL).forPath("/demo");
        }

        System.out.println(client.getChildren().forPath("/demo"));
    }
}
