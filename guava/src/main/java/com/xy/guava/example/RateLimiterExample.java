package com.xy.guava.example;

import com.google.common.util.concurrent.RateLimiter;

/**
 * @author yefei
 * @date 2018-03-01 11:09
 */
public class RateLimiterExample {

    public static void main(String[] args) {

        RateLimiter rateLimiter = RateLimiter.create(2);

        while (true) {
            System.out.println(rateLimiter.acquire(2));
            System.out.println(rateLimiter.acquire(10));
        }
    }
}
