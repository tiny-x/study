package com.xy.netty.reconnect;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class Decoder extends LengthFieldBasedFrameDecoder {


    public Decoder() {
        super(Integer.MAX_VALUE, 0, 2, 0, 2);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        if (frame == null) {
            return null;
        }

        int i = frame.readableBytes();
        System.out.printf("------> LengthFieldBasedFrameDecoder %d \n", i);
        byte[] bytes = new byte[i];
        frame.readBytes(bytes);
        System.out.println("result:" + new String(bytes, "utf-8"));
        return new String(bytes, "utf-8");
        //out.add(i);
    }
}
