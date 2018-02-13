package com.xy.netty.coder.replaying;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class Encoder extends MessageToByteEncoder<Object> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        // 16 byte
        String fix = "01234567890ABCDEF";
        String send = "";
        for (int i = 0; i < 1024; i++) {
            send += fix;
        }
        out.writeBytes(send.getBytes());
    }
}
