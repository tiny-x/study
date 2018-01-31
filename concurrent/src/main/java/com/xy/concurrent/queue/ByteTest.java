package com.xy.concurrent.queue;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yefei
 * @date 2018-01-10 10:25
 */
public class ByteTest {

    private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
    private static final int COUNT_BITS = Integer.SIZE - 3;
    private static final int CAPACITY   = (1 << COUNT_BITS) - 1;

    // runState is stored in the high-order bits
    private static final int RUNNING    = -1 << COUNT_BITS;
    private static final int SHUTDOWN   =  0 << COUNT_BITS;
    private static final int STOP       =  1 << COUNT_BITS;
    private static final int TIDYING    =  2 << COUNT_BITS;
    private static final int TERMINATED =  3 << COUNT_BITS;

    // Packing and unpacking ctl
    private static int runStateOf(int c)     { return c & ~CAPACITY; }
    private static int workerCountOf(int c)  { return c & CAPACITY; }
    private static int ctlOf(int rs, int wc) { return rs | wc; }

    public static void main(String[] args) {
        ByteTest byteTest = new ByteTest();

        System.out.println(byteTest.ctl.get());
        System.out.println(workerCountOf(byteTest.ctl.get()));
        System.out.println(runStateOf(byteTest.ctl.get()) >> COUNT_BITS); // RUNNING
        System.out.println(6 & -6 );

    }
}
