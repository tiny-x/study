package com.xy.jvm.gc;

/**
 * -verbose:gc -Xms20m -Xmx20m -Xmn10m -XX:+PrintGCDetails -XX:SurvivorRatio=8 -XX:+UseConcMarkSweepGC
 *
 * [gc] 老年代担保，不清理内存，因此不会发生全局停顿
 * [full-gc] stop-the-world, system.gc()
 *
 */
public class Ygc {

    private static final int _1MB = 1024 * 1024;

    /**
     * eden     -->  8M
     * survivor -->  1M
     * old      -->  10M
     *
     *  allocation4 = new byte[4 * _1MB];
     *  新生代已经放不下了，出现ygc，把新生到6m通过担保到老年代，
     *  新分配的4m到了新生代。1
     *
     * @param args
     */
    public static void main(String[] args) {
        byte[] allocation1, allocation2, allocation3, allocation4;
        allocation1 = new byte[2 * _1MB];
        allocation2 = new byte[2 * _1MB];
        allocation3 = new byte[2 * _1MB];
        allocation4 = new byte[4 * _1MB]; //出现一次 新生代gc
    }
}
