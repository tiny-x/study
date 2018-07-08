package com.xy.jvm.gc;

import java.nio.ByteBuffer;

public class SystemGC {

    public static void main(String[] args) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(10);
        "".intern();
    }
}
