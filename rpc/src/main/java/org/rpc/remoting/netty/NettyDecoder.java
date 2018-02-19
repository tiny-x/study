package org.rpc.remoting.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.rpc.remoting.api.payload.RequestBytes;
import org.rpc.remoting.api.payload.ResponseBytes;
import org.rpc.remoting.api.procotol.ProtocolHead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class NettyDecoder extends ReplayingDecoder<NettyDecoder.State> {

    private static final Logger logger = LoggerFactory.getLogger(NettyDecoder.class);

    public NettyDecoder() {
        super(State.HEADER_MAGIC);
    }

    private final ProtocolHead head = new ProtocolHead();

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
                boolean isRequest = ((head.getMessageCode() % 2) == 1);
                if (isRequest) {
                    byte[] body = new byte[head.getBodyLength()];
                    in.readBytes(body);
                    RequestBytes requestBytes = new RequestBytes(
                            head.getMessageCode(),
                            head.getInvokeId(),
                            head.getSerializerCode(),
                            body);
                    out.add(requestBytes);
                } else {
                    byte[] body = new byte[head.getBodyLength()];
                    in.readBytes(body);
                    ResponseBytes responseBytes = new ResponseBytes(
                            head.getMessageCode(),
                            head.getSerializerCode(),
                            body);
                    responseBytes.setInvokeId(head.getInvokeId());
                    responseBytes.setStatus(head.getStatus());
                    out.add(responseBytes);
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
