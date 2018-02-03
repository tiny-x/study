package com.xy.basic;

/**
 * @author yefei
 * @date 2018-01-31 14:26
 */
public class ByteModel {

    public static void main(String[] args) {

        System.out.println(tableSizeFor(151));
    }

    /**
     * @param a any
     * @param b powerOfTwo
     * @return
     */
    public static int powerOfTwoModel(int a, int b) {
        return a & (b - 1);
    }

    /**
     * 00001000
     * 11110111
     * ||
     * \/
     * 8
     *
     * @param val
     * @return
     */
    private static boolean isPowerOfTwo(int val) {
        return (val & -val) == val;
    }


    /**
     * 00001000
     * 00000111
     * ||
     * \/
     * 0
     *
     * @param val
     * @return
     */
    private static boolean isPowerOfTwo_(int val) {
        return (val & (val - 1)) == 0;
    }


    /**
     * 求 大于c 得最近得2得幂次方
     *
     * @param c
     * @return
     */
    private static final int tableSizeFor(int c) {
        int n = c - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= Integer.MAX_VALUE) ? Integer.MAX_VALUE : n + 1;
    }
}
