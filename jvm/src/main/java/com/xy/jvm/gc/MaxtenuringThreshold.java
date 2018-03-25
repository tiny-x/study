package com.xy.jvm.gc;

/**
 *
 * -verbose:gc -Xms20m -Xmx20m -Xmn10m -XX:+PrintGCDetails -XX:SurvivorRatio=8 -XX:+UseConcMarkSweepGC
 * -XX:MaxTenuringThreshold=1 默认年龄到达15之后进入老年代
 * -XX:PrintTenuringDistribution=true
 * ------------------------------------------------------
 * 并没有测试出效果
 * ------------------------------------------------------
 * survivor中相同年龄的对象个数大于survivor中的一半进入老年代
 */
public class MaxtenuringThreshold {

    private static final int _1MB = 1024 * 1024;

    public static void main(String[] args) {
        byte[] allocation1, allocation2, allocation3;
        allocation1 = new byte[_1MB / 4];
        allocation2 = new byte[4 * _1MB];
        allocation3 = new byte[4 * _1MB];
        allocation3 = null;
        allocation3 = new byte[4 * _1MB];
    }
}
