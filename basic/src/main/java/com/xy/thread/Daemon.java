package com.xy.thread;

import java.util.concurrent.TimeUnit;

public class Daemon {

    public static void main(String[] args) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        //thread.setDaemon(false);
        thread.start();
        System.out.println(Thread.currentThread().isDaemon());

    }
}
