package org.rpc.remoting.api;

import io.netty.channel.ChannelHandlerContext;
import org.rpc.remoting.api.payload.RequestBytes;
import org.rpc.remoting.api.payload.ResponseBytes;

public interface RequestProcessor {

    ResponseBytes process(ChannelHandlerContext context, RequestBytes request);

    boolean rejectRequest();
}
