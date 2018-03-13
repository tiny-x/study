package com.xy.volatiles;

/**
 * 在构造函数中暴露this 可能会带来意想不到的风险
 *
 * 虽然 y 是final写，但是(2) (3)之间可能存在重排序，
 * (5)可能会拿到final还未写入的值。
 *
 */
public class FinalEscapeExample {

    private static FinalEscapeExample example;
    private final int y;

    public FinalEscapeExample() {
        y = 2;                                  // (2)
        example = this;                         // (3)
    }

    public static void init() {
        example = new FinalEscapeExample();     // (1)
    }

    public static void read() {
        if (example != null) {                  // (4)
            System.out.println(example.y);      // (5)
        }
    }
}
