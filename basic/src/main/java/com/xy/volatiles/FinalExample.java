package com.xy.volatiles;

/**
 * 线程1执行 init() 方法，线程2执行 read() 方法
 *
 * -------------------------------------------------------------------------------
 * 假设线程2已经执行到(6),
 * (2)是普通写， 可能重排序到 (1) 后面，因此线程2执行6 可能输出0。
 * (3)是final写，会在(3)和(1)之间生成store-store屏障，禁止重排序，因此线程2执行(7)是安全的。
 * X86处理器中 不会生成内存屏障
 * --------------------------------------------------------------------------------
 *
 * --------------------------------------------------------------------------------
 * 申明 private volatile static FinalExample example;
 * (6),(7)的读都是安全的
 * --------------------------------------------------------------------------------
 *
 * 不可变量尽量申明final
 *
 */
public class FinalExample {

    private int x;

    private final int y;

    private static FinalExample example;

    public FinalExample() {
        x = 1;                                  // (2)
        y = 2;                                  // (3)
    }

    public static void init() {
        example = new FinalExample();           // (1)
    }

    public static void read() {
        if (example != null) {                  // (5)
            System.out.println(example.x);      // (6)
            System.out.println(example.y);      // (7)
        }
    }

}
