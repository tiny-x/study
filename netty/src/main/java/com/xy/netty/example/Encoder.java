package com.xy.netty.example;

import com.xy.netty.example.exception.RemotingException;
import com.xy.netty.example.payload.ByteHolder;
import com.xy.netty.example.payload.RequestBytes;
import com.xy.netty.example.payload.ResponseBytes;
import com.xy.netty.example.protocol.Protocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Encoder extends MessageToByteEncoder<ByteHolder> {

    private static final Logger logger = LoggerFactory.getLogger(Encoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteHolder msg, ByteBuf out) throws Exception {
        try {
            if (msg instanceof RequestBytes) {
                doEncodeRequest((RequestBytes) msg, out);
            } else if (msg instanceof ResponseBytes) {
                doEncodeResponse((ResponseBytes) msg, out);
            } else {
                throw new RemotingException("not support byte holder" + msg.getClass());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            ctx.channel().close().addListener((c) -> logger.info("channel close {}", c.isSuccess()));
        }
    }

    private void doEncodeRequest(RequestBytes request, ByteBuf out) {
        byte sign = Protocol.toSign(Protocol.REQUEST, request.getSerializerCode());
        long invokeId = request.getInvokeId();
        byte[] bytes = request.getBody();
        int length = bytes.length;

        out.writeShort(Protocol.MAGIC)
                .writeByte(sign)
                .writeByte(0x00)
                .writeLong(invokeId)
                .writeInt(length)
                .writeBytes(bytes);
    }

    private void doEncodeResponse(ResponseBytes response, ByteBuf out) {

        byte sign = Protocol.toSign(Protocol.RESPONSE, response.getSerializerCode());
        byte status = response.getStatus();
        long invokeId = response.getInvokeId();
        byte[] bytes = response.getBody();
        int length = bytes.length;
        out.writeShort(Protocol.MAGIC)
                .writeByte(sign)
                .writeByte(status)
                .writeLong(invokeId)
                .writeInt(length)
                .writeBytes(bytes);
    }

}
