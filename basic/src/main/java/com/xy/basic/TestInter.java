package com.xy.basic;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TestInter implements Runnable {

    int b = 100;

    static ExecutorService executorService;

    static {
        executorService = Executors.newFixedThreadPool(5);
    }


    public static void main(String[] args) throws Exception {
        TestInter tt = new TestInter();
        Thread t = new Thread();  //1
        t.start(); //2

        System.out.println("main thread b=" + tt.b); //4
        TimeUnit.SECONDS.sleep(3);
        ThreadPoolExecutor executorService1 = (ThreadPoolExecutor) executorService;
        long taskCount = executorService1.getTaskCount();
        System.out.println(taskCount);
        t.interrupt();
        taskCount = executorService1.getTaskCount();
        System.out.println(taskCount);
    }

    @Override
    public void run() {
        try {
            for (; ; ) {
                TimeUnit.SECONDS.sleep(1);
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while (true) {
                                TimeUnit.MILLISECONDS.sleep(5000);
                            }
                        } catch (InterruptedException e) {
                            // ignore
                        }
                    }
                });
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
