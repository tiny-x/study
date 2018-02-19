package org.rpc.rpc.consumer.process;

import com.sun.org.apache.regexp.internal.RE;
import io.netty.channel.ChannelHandlerContext;
import org.rpc.register.NotifyListener;
import org.rpc.register.bean.RegisterMeta;
import org.rpc.remoting.api.RequestProcessor;
import org.rpc.remoting.api.payload.RequestBytes;
import org.rpc.remoting.api.payload.ResponseBytes;
import org.rpc.remoting.api.procotol.ProtocolHead;
import org.rpc.rpc.consumer.Consumer;
import org.rpc.rpc.model.ServiceMeta;
import org.rpc.serializer.Serializer;
import org.rpc.serializer.SerializerFactory;
import org.rpc.serializer.SerializerType;

import static org.rpc.remoting.api.procotol.ProtocolHead.SUBSCRIBE_RECEIVE;

public class SubscribeProcess implements RequestProcessor {

    private Consumer consumer;

    public SubscribeProcess(Consumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public ResponseBytes process(ChannelHandlerContext context, RequestBytes request) {
        Serializer serializer = SerializerFactory.serializer(SerializerType.parse(request.getSerializerCode()));
        RegisterMeta registerMeta = serializer.deserialize(request.getBody(), RegisterMeta.class);

        switch (request.getMessageCode()) {
            case SUBSCRIBE_RECEIVE: {
                consumer.connect(registerMeta.getAddress());
                ServiceMeta serviceMeta = new ServiceMeta(registerMeta.getServiceMeta().getGroup(),
                        registerMeta.getServiceMeta().getServiceProviderName(),
                        registerMeta.getServiceMeta().getVersion());
                consumer.client().addChannelGroup(serviceMeta, consumer.client().group(registerMeta.getAddress()));
                break;
            }
            default:
                throw new UnsupportedOperationException("RegisterProcess Unsupported MessageCode: " + request.getMessageCode());
        }
        return null;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
