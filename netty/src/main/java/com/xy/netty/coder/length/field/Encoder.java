package com.xy.netty.coder.length.field;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class Encoder extends MessageToByteEncoder<Client.Protocol> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Client.Protocol msg, ByteBuf out) throws Exception {

        out.writeShort(msg.getLength());
        out.writeBytes(msg.getBody());
    }
}
