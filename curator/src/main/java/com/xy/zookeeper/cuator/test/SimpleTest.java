package com.xy.zookeeper.cuator.test;


import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;

import java.io.File;

public class SimpleTest {

    static String path = "/zookeeper";

    public static void main(String[] args) throws Exception {
       // TestingServer server = new TestingServer(2181, new File("/tmp/zookeeper"));
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("/")
                .sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        client.start();

        System.out.println(client.getChildren().forPath(path));
    }
}
