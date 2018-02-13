package com.xy.netty.example;

import com.xy.netty.example.payload.RequestBytes;
import com.xy.netty.example.payload.ResponseBytes;
import com.xy.netty.example.protocol.Protocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Decoder extends ReplayingDecoder<Decoder.State> {

    private static final Logger logger = LoggerFactory.getLogger(Decoder.class);

    public Decoder() {
        super(State.HEADER_MAGIC);
    }

    private final Protocol head = new Protocol();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        switch (state()) {
            case HEADER_MAGIC:
                in.readShort();
                checkpoint(State.HEADER_SIGN);
            case HEADER_SIGN:
                head.setSign(in.readByte());
                checkpoint(State.HEADER_STATUS);
            case HEADER_STATUS:
                head.setStatus(in.readByte());
                checkpoint(State.HEADER_ID);
            case HEADER_ID:
                head.setInvokeId(in.readLong());
                checkpoint(State.HEADER_BODY_LENGTH);
            case HEADER_BODY_LENGTH:
                head.setBodyLength(in.readInt());
                checkpoint(State.BODY);
            case BODY:
                switch (head.getMessageCode()) {

                    case Protocol.REQUEST: {
                        byte[] body = new byte[head.getBodyLength()];
                        in.readBytes(body);
                        RequestBytes requestBytes = new RequestBytes(head.getSerializerCode(), body);
                        logger.info("REQUEST decode: {}", requestBytes);
                        out.add(requestBytes);
                        break;
                    }
                    case Protocol.RESPONSE: {
                        byte[] body = new byte[head.getBodyLength()];
                        in.readBytes(body);
                        ResponseBytes responseBytes = new ResponseBytes(
                                head.getSerializerCode(),
                                body,
                                head.getStatus(),
                                head.getInvokeId());
                        logger.info("RESPONSE decode: {}", responseBytes);
                        out.add(responseBytes);
                        break;
                    }
                }
                checkpoint(State.HEADER_MAGIC);
        }
    }


    enum State {
        HEADER_MAGIC,
        HEADER_SIGN,
        HEADER_STATUS,
        HEADER_ID,
        HEADER_BODY_LENGTH,
        BODY
    }
}
