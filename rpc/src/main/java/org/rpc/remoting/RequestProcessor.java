package org.rpc.remoting;

import io.netty.channel.ChannelHandlerContext;
import org.rpc.remoting.payload.RequestBytes;
import org.rpc.remoting.payload.ResponseBytes;

public interface RequestProcessor {

    ResponseBytes process(ChannelHandlerContext context, RequestBytes request);

    boolean rejectRequest();
}
