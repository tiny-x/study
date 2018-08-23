package com.xy.concurrent.aid;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchExample {

    public static void main(String[] args) throws Exception {

        CountDownLatch countDownLatch = new CountDownLatch(1);

        countDownLatch.await();
    }
}
