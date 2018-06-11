package com.xy.instrument;

import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author yefei
 * @date 2018-05-31 11:03
 */
public class Main {

    /**
     * -javaagent:D:\workspace\idea\study\instrument\target\instrument-1.0.0.jar
     *
     * @param args
     */
    public static void main(String[] args) {
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1,
                (Runnable r) -> {
                    Thread thread = new Thread(r);
                    thread.setDaemon(false);
                    return thread;

                });

        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            Random random = new Random();
            int add = new Main().add(random.nextInt(), random.nextInt());
            System.out.println(add);
        }, 1, 3, TimeUnit.SECONDS);

    }

    public int add(int a, int b) {
        return a + b;
    }
}
