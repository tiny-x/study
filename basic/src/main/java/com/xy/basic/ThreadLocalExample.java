package com.xy.basic;

/**
 * @author yefei
 * @date 2018-01-31 9:59
 */
public class ThreadLocalExample {

    private static ThreadLocal<Object> t1 = new ThreadLocal<>();

    private static ThreadLocal<Object> t2 = new ThreadLocal<>();

    public static void main(String[] args) {

        /**
         * ThreadLocal.ThreadLocalMap threadLocals 是 thread 变量
         * 因此 t1，t2中的ThreadLocalMap 对于同一个线程是同一个 ThreadLocalMap 对象。
         */
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                t1.set(Thread.currentThread().getName());
                t2.set(Thread.currentThread().getName());
            }
        });
        thread.start();

        /**
         * ThreadLocalMap
         * @see java.lang.ThreadLocal.ThreadLocalMap(线程探测处理hash冲突)
         *
         *  Entry extends WeakReference<ThreadLocal<?>>
         *
         */



    }
}
