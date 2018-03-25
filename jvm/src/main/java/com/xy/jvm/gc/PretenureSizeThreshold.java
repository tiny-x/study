package com.xy.jvm.gc;

/**
 * 验证大对象直接进入老年代
 * -verbose:gc -Xms20m -Xmx20m -Xmn10m -XX:+PrintGCDetails -XX:SurvivorRatio=8 -XX:+UseConcMarkSweepGC
 * -XX:PretenureSizeThreshold=3145728 //3m 只对 serial 和 parNew有效
 *
 */
public class PretenureSizeThreshold {

    private static final int _1MB = 1024 * 1024;

    public static void main(String[] args) {
        byte[] allocation1;
        allocation1 = new byte[4 * _1MB];
    }
}
