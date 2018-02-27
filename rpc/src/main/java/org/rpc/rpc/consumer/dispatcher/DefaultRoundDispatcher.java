package org.rpc.rpc.consumer.dispatcher;

import org.rpc.exception.RemotingException;
import org.rpc.remoting.api.channel.ChannelGroup;
import org.rpc.remoting.api.payload.RequestCommand;
import org.rpc.remoting.api.procotol.ProtocolHead;
import org.rpc.rpc.Request;
import org.rpc.rpc.consumer.Consumer;
import org.rpc.rpc.consumer.InvokeType;
import org.rpc.rpc.load.balancer.LoadBalancer;
import org.rpc.rpc.model.RequestWrapper;
import org.rpc.serializer.Serializer;
import org.rpc.serializer.SerializerType;

public class DefaultRoundDispatcher extends AbstractDispatcher {

    public DefaultRoundDispatcher(
            Consumer consumer, LoadBalancer loadBalancer, SerializerType serializerType) {
        super(consumer, loadBalancer, serializerType);
    }

    @Override
    public <T> T dispatch(Request request, InvokeType invokeType) throws RemotingException, InterruptedException {

        final RequestWrapper requestWrapper = request.getRequestWrapper();

        // 通过软负载均衡选择一个channel
        ChannelGroup channelGroup = select(requestWrapper.getServiceMeta());
        Serializer serializer = getSerializer();

        byte[] bytes = serializer.serialize(requestWrapper);
        RequestCommand requestCommand = new RequestCommand(ProtocolHead.REQUEST, getSerializerCode(), bytes);
        request.setRequestCommand(requestCommand);

        return (T) invoke(channelGroup, request, DispatchType.ROUND, invokeType);
    }
}
