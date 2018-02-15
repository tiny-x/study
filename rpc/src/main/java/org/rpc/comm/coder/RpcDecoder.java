package org.rpc.comm.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.rpc.serializer.ProtoStuffSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * The type Rpc decoder. <br/>
 * rpc解码 rpcRequest --> byte[]
 *
 * @author yefei
 * @date 2017 -06-20 15:24
 */
public class RpcDecoder extends ByteToMessageDecoder {

    /**
     * logger
     */
    private final static Logger logger = LoggerFactory.getLogger(RpcDecoder.class);

    private Class<?> reponseClass;

    public RpcDecoder(Class<?> reponseClass) {
        this.reponseClass = reponseClass;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4) {
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        out.add(ProtoStuffSerializer.deserialize(data, reponseClass));
    }

}
