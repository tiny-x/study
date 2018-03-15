package com.xy.threadpool;

import java.util.concurrent.*;

public class ThreadPoolExample {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();

        /**
         * -------------------------------------------------------------------------
         *
         **   offer()        +---------+--------+--------+-------+
         *    +------------> |         |        |        |       +------------------+
         *                   +---------+--------+--------+-------+                  |
         *                                                                          |
         *    +------------------------------------------------------------------+  |   take()/poll()
         *    |                                                                  |  |
         *    |  while (task != null || (task = getTask()) != null)              | <+
         *    |                                                                  |
         *    |  worker --> runnable.run()                                       |
         *    |                                                                  |
         *    +------------------------------------------------------------------+
         **
         *
         * 线程池里边的worker类工作原理
         *  worker类 包含 thread 和 runnable 任务，worker本身继承runnable，
         *
         *  由thread.start() 启动线程，调用worker.run(), 调用 worker.runWorker(Worker w)方法，
         *  循环判断本身的runnable任务是否null，本身的runnable任务执行完成，
         *  不断取阻塞队列任务，执行runnable.run()来达到线程复用的效果。
         *
         * -------------------------------------------------------
         * corePoolSize 核心线程，默认线程不中断，会一直 从阻塞队列调用take阻塞
         * maximumPoolSize 最大线程个数
         * keepAliveTime unit 工作线程存活时间
         * workQueue 任务队列
         *----------------------------------------------------------
         * 任务提交后，如果corePoolSize不足，将任务存放任务队列，
         * 队列空间不足，创建工作线程处理（会使用全局锁），
         * 当线程数量等于maximumPoolSize, 再次提交的任务会由饱和策略梳理
         * 默认策略 AbortPolicy， 直接异常抛出。
         * ----------------------------------------------------------
         */
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                1,
                5,
                500L,
                TimeUnit.MILLISECONDS,
                new SynchronousQueue<>());

        /**
         * 允许核心线程超时，小于corePoolSize线程也会被中断
         */
        threadPoolExecutor.allowCoreThreadTimeOut(true);

        /**
         * 初始创建核心线程
         */
        threadPoolExecutor.prestartAllCoreThreads();

        /**
         * AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0))
         *
         * threadPoolExecutor 内部通过 AtomicInteger 维护线程状态，线程个数。
         * ctl高3位 维护线程池状态， 低29位表示线程个数。
         *
         * 大于corePoolSize 调用阻塞队列take poll(keepAliveTime, unit) 超时退出循环，直到线程结束
         *
         */
        for (int i = 0; i < 3; i++) {
            threadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        TimeUnit.SECONDS.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("hello thread pool");
                }
            });
        }

    }
}
