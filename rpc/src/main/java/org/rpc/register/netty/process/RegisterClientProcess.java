package org.rpc.register.netty.process;

import io.netty.channel.ChannelHandlerContext;
import org.rpc.remoting.api.RequestProcessor;
import org.rpc.remoting.api.payload.RequestBytes;
import org.rpc.remoting.api.payload.ResponseBytes;

public class RegisterClientProcess implements RequestProcessor {

    @Override
    public ResponseBytes process(ChannelHandlerContext context, RequestBytes request) {
        // TODO notify
        return null;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
