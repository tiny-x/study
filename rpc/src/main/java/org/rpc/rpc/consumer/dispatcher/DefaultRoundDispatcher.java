/*
 * Copyright (c) 2015 The Jupiter Project
 *
 * Licensed under the Apache License, version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.rpc.rpc.consumer.dispatcher;

import io.netty.channel.Channel;
import org.rpc.remoting.api.payload.RequestBytes;
import org.rpc.remoting.api.payload.ResponseBytes;
import org.rpc.remoting.api.procotol.ProtocolHead;
import org.rpc.rpc.Request;
import org.rpc.rpc.consumer.Consumer;
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
    public <T> T dispatch(Request request, Class<T> returnType) {

        final RequestWrapper requestWrapper = request.getRequestWrapper();

        // 通过软负载均衡选择一个channel
        Channel channel = select(requestWrapper.getServiceMeta());
        Serializer serializer = getSerializer();

        byte[] bytes = serializer.serialize(requestWrapper);
        RequestBytes requestBytes = new RequestBytes(ProtocolHead.REQUEST, getSerializerCode(), bytes);
        request.setRequestBytes(requestBytes);

        ResponseBytes responseBytes = invoke(channel, request, DispatchType.ROUND, true);

        T result = serializer.deserialize(responseBytes.getBody(), returnType);
        return result;
    }
}
