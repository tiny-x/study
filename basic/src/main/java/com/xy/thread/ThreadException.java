package com.xy.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author yefei
 * @date 2018-03-02 16:29
 */
public class ThreadException {

    static ExecutorService executorService = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        Thread task = new Thread(new Runnable() {
            @Override
            public void run() {
                int a = 1/0;
            }
        });
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                e.printStackTrace();
            }
        });
        executorService.submit(task);

    }
}
