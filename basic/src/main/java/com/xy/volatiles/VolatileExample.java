package com.xy.volatiles;

/**
 * ------------------------------------------
 * 假设线程1执行write()方法，线程2执行read()方法
 *
 * 根据 happen before原则
 * (1) happen before (2)
 * (2) happen before (3)
 * (3) happen before (4)
 *
 * 因此 (1)写入x 对于(4) 读取x是可见的。
 * ------------------------------------------
 *
 * ------------------------------------------
 * volatile 内存语义
 * 当第一个操作 volatile 写，第二个操作 volatile读， 不能重排序
 * 当第一个操作 volatile 读，第二个操作无论是什么都不能重排序。
 * 当第二个操作 volatile 写，第一个操作无论是什么都不能重排序
 *
 *
 */
public class VolatileExample {

    private int x;

    private volatile boolean flag;

    public void write() {
        x = 3;                             // (1)
        flag = true;                       // (2)  volatile 写
    }

    public void read() {
        if (flag) {                        // (3)  volatile 读
            System.out.println(x);         // (4)
        }
    }

    public static void main(String[] args) {
        VolatileExample ex = new VolatileExample();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ex.write();
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                ex.read();
            }
        }).start();
    }
}
