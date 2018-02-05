package org.rpc.comm.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.rpc.comm.utils.SerializationUtil;

/**
 * The type Rpc encoder.<br />
 * rpc编码 byte[] --> Object
 *
 * @author yefei
 * @date 2017 -06-20 14:59
 */
public class RpcEncoder extends MessageToByteEncoder<Object> {

    private Class<?> requestCalss;

    public RpcEncoder(Class<?> requestCalss) {
        this.requestCalss = requestCalss;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (requestCalss.isInstance(msg)) {
            byte[] bytes = SerializationUtil.serialize(msg);
            out.writeInt(bytes.length);
            out.writeBytes(bytes);
        }
    }
}
