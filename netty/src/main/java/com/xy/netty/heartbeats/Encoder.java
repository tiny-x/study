package com.xy.netty.heartbeats;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class Encoder extends MessageToByteEncoder<String> {

    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) throws Exception {
        System.out.printf("--------> encoder %d bytes", msg.getBytes().length);
        out.writeBytes(msg.getBytes());
    }
}
