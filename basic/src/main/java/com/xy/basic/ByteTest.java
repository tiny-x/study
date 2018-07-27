package com.xy.basic;

/**
 * 位移运算符 负数反码参与运算
 * 1000 0000 --> -128
 * 0111 1111 --> 127
 * <p>
 * 1111 1111 --> -1
 * <p>
 * & 可以随意取想要的bit
 * | 可以做高低位相加操作
 */
public class ByteTest {

    public static void main(String[] args) {
        /*byte a = 0x09;
        byte b = 0x01;

        System.out.println(toByte(a, b));
        getAB((byte)-111);
        getAB2((byte)-111);*/

        byte x = -127;
        System.out.println((x >>> 1));
        System.out.println(x >> 1);
        System.out.println((-128 & 0xff) >> 1);
        System.out.println((byte)(Byte.MAX_VALUE + 2));

    }

    static byte toByte(byte a, byte b) {
        return (byte) ((a << 4) | b);
    }

    static void getAB(byte c) {
        System.out.print("a:" + ((c & 0xF0) >> 4));
        System.out.print(" b:" + (c & 0x0F) + "\n");
    }

    static void getAB2(byte c) {
        System.out.print("a:" + ((((int) c) & 0xFF) >> 4));
        System.out.print(" b:" + (c & 0x0F) + "\n");
    }
}
