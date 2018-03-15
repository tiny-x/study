package com.xy.netty.coder.base;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.Arrays;
import java.util.List;

public class Decoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int i = in.readableBytes();
        System.out.printf("------> decoder %d bytes \n", i);
        byte[] bytes = new byte[i];
        in.readBytes(bytes);
        System.out.println("asc" + Arrays.toString(bytes));
        System.out.println(new String(bytes, "utf-8"));
        out.add(i);

        ctx.channel().writeAndFlush("leaf>");
    }
}
