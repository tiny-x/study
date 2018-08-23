package com.xy.concurrent.aid;

import java.util.concurrent.Semaphore;

public class SemaphoreExample {

    public static void main(String[] args) throws Exception {

        Semaphore semaphore = new Semaphore(2);
        semaphore.acquire();
    }

}
