package com.xy.basic;

import java.lang.ref.*;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * @author yefei
 * @date 2018-01-30 11:28
 */
public class ReferenceExample {

    static volatile boolean flag = false;

    public static void main(String[] args) {

        // -Xmx1m -XX:+PrintGC
        softReference();
        //weakReference();
    }

    private static void softReference() {

        final ReferenceQueue queue = new ReferenceQueue();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Reference reference = null;
                    try {
                        reference = queue.remove();

                        flag = true;
                        System.out.println("------> gc");

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        SoftReference reference = new SoftReference(new Object(), queue);

        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] bytes = new byte[1];
                while (!flag) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(1);
                        bytes = Arrays.copyOf(bytes, bytes.length + 10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        System.out.println(reference.get());
    }

    private static void weakReference() {
        final ReferenceQueue queue = new ReferenceQueue();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Reference reference = null;
                    try {
                        reference = queue.remove();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("------> gc");
                }
            }
        }).start();

        WeakReference reference = new WeakReference(new Object(), queue);
        System.gc();
        System.out.println(reference.get());
    }

    private static void phantomReference() {
        ReferenceQueue queue = new ReferenceQueue();
        PhantomReference reference = new PhantomReference(new Object(), queue);
        System.gc();
        System.out.println(reference.get());
    }


}
