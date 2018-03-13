package com.xy.guava.example;

import com.google.common.util.concurrent.RateLimiter;

import java.util.concurrent.TimeUnit;

/**
 * @author yefei
 * @date 2018-03-01 11:09
 */
public class RateLimiterExample {

    public static void main(String[] args) {

        tryAquireTest2();
    }

    private static void demo() {
        RateLimiter rateLimiter = RateLimiter.create(2);

        while (true) {
            System.out.println(rateLimiter.acquire(2));
            System.out.println(rateLimiter.acquire(10));
        }
    }

    public static void tryAquireTest() {
        RateLimiter rateLimiter = RateLimiter.create(1);
        for (int i = 0; i < 10; i++) {
            if (!rateLimiter.tryAcquire(1000, TimeUnit.MILLISECONDS)) {
                System.out.println("limit");
            } else {
                System.out.println("pass");
            }
        }
    }

    public static void tryAquireTest2() {
        RateLimiter rateLimiter = RateLimiter.create(1);
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!rateLimiter.tryAcquire(1000, TimeUnit.MILLISECONDS)) {
                        System.out.println("limit" + rateLimiter.getRate());
                    } else {
                        System.out.println("pass");
                    }
                }
            }).start();
        }
    }
}
