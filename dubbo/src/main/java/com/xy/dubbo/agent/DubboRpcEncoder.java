package com.xy.dubbo.agent;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class DubboRpcEncoder extends MessageToByteEncoder<DubboRpcRequest> {

    @Override
    protected void encode(ChannelHandlerContext ctx, DubboRpcRequest msg, ByteBuf out) throws Exception {
    }
}
