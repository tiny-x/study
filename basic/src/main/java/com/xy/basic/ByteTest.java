package com.xy.basic;

public class ByteTest {

    public static void main(String[] args) {
        byte a = 0x09;
        byte b = 0x01;

        System.out.println(toByte(a, b));
        getAB((byte)-111);
        getAB2((byte)-111);
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
