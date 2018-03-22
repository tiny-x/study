package com.xy.zookeeper.cuator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

public class AtomicIntegerZK {

    static volatile int addI = 0;

    static volatile Stat stat;

    public static void main(String[] args) throws Exception {

        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .connectionTimeoutMs(3000)
                .sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        client.start();

        String path = "/atomic_integer/i";
        if (client.checkExists().forPath(path) == null) {
            client.create().creatingParentsIfNeeded().forPath(path);
        }
        stat = new Stat();
        client.getData().storingStatIn(stat).forPath(path);
        stat = client.setData().withVersion(stat.getVersion()).forPath(path, ("0").getBytes());

        CountDownLatch countDownLatch = new CountDownLatch(100);
        for (int j = 0; j < 100; j++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // cas
                    try {
                        for (int j = 0; j < 100; j++) {
                            increment();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        countDownLatch.countDown();
                    }
                }

                private void increment() throws Exception {
                    try {
                        stat = client.setData().withVersion(stat.getVersion()).forPath(path, (addI + "").getBytes());
                        addI++;
                    } catch (Exception e) {
                        increment();
                    }
                }

            }).start();
        }

        countDownLatch.await();
        System.out.println(new String(client.getData().forPath(path)));
    }
}
