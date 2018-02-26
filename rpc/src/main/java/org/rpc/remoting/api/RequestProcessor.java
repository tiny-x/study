package org.rpc.remoting.api;

import io.netty.channel.ChannelHandlerContext;
import org.rpc.remoting.api.payload.RequestCommand;
import org.rpc.remoting.api.payload.ResponseCommand;

public interface RequestProcessor {

    ResponseCommand process(ChannelHandlerContext context, RequestCommand request);

    boolean rejectRequest();
}
