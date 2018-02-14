package org.rpc.rpc.processor;

import io.netty.channel.ChannelHandlerContext;
import org.rpc.comm.utils.SerializationUtil;
import org.rpc.remoting.api.RequestProcessor;
import org.rpc.remoting.api.payload.RequestBytes;
import org.rpc.remoting.api.payload.ResponseBytes;
import org.rpc.rpc.model.RequestWrapper;
import org.rpc.rpc.model.ServiceWrapper;
import org.rpc.rpc.provider.Provider;

public class DefaultProcessor implements RequestProcessor {

    private Provider provider;

    public DefaultProcessor(Provider provider) {
        this.provider = provider;
    }

    @Override
    public ResponseBytes process(ChannelHandlerContext context, RequestBytes request) {
        RequestWrapper requestWrapper = SerializationUtil.deserialize(request.getBody(), RequestWrapper.class);
        ServiceWrapper serviceWrapper = provider.lookupService(requestWrapper.getServiceMeta());


        return null;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
