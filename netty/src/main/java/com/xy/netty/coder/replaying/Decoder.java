package com.xy.netty.coder.replaying;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

public class Decoder extends ReplayingDecoder<Void> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int i = in.readableBytes();
        System.out.printf("------> ReplayingDecoder %d \n", i);
        byte[] bytes = new byte[1024*16];
        in.readBytes(bytes);
        System.out.println("result:" + new String(bytes, "utf-8"));
        out.add(new String(bytes, "utf-8"));
        //out.add(i);

    }
}
