package com.xy.zookeeper.cuator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.CountDownLatch;

/**
 * 独占锁
 */
public class ExclusiveLock {
    static int i = 0;

    public static void main(String[] args) throws InterruptedException {
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .connectionTimeoutMs(3000)
                .sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        client.start();

        // 效率太低了
        InterProcessMutex lock = new InterProcessMutex(client, "/exclusive_lock");

        // ReentrantLock lock = new ReentrantLock();
        CountDownLatch countDownLatch = new CountDownLatch(100);

        long l = System.currentTimeMillis();
        for (int j = 0; j < 100; j++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int k = 0; k < 100; k++) {
                        try {
                            lock.acquire();
                            i++;
                        } catch (Exception e) {
                        } finally {
                            try {
                                lock.release();
                            } catch (Exception e) {
                            }
                        }
                    }
                    countDownLatch.countDown();
                }
            }).start();
        }
        countDownLatch.await();
        System.out.println(i);
        System.out.println((System.currentTimeMillis() - l) / 1000);

    }

    private static void unsafe() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(100);
        for (int j = 0; j < 100; j++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int k = 0; k < 100; k++) {
                        i++;
                    }
                    countDownLatch.countDown();
                }
            }).start();
        }
        countDownLatch.await();
        System.out.println(i);
    }


}
