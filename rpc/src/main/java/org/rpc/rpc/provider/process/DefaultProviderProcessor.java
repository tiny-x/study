package org.rpc.rpc.provider.process;

import io.netty.channel.ChannelHandlerContext;
import org.rpc.remoting.api.RemotingCommandFactory;
import org.rpc.remoting.api.RequestProcessor;
import org.rpc.remoting.api.ResponseStatus;
import org.rpc.remoting.api.payload.RequestCommand;
import org.rpc.remoting.api.payload.ResponseCommand;
import org.rpc.remoting.api.procotol.ProtocolHead;
import org.rpc.rpc.flow.controller.FlowController;
import org.rpc.rpc.model.RequestWrapper;
import org.rpc.rpc.model.ServiceWrapper;
import org.rpc.rpc.provider.Provider;
import org.rpc.serializer.Serializer;
import org.rpc.serializer.SerializerFactory;
import org.rpc.serializer.SerializerType;
import org.rpc.utils.Reflects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RejectedExecutionException;

public class DefaultProviderProcessor implements RequestProcessor {

    /**
     * logger
     */
    private final static Logger logger = LoggerFactory.getLogger(DefaultProviderProcessor.class);

    private final Provider provider;

    public DefaultProviderProcessor(Provider provider) {
        this.provider = provider;
    }

    @Override
    public ResponseCommand process(ChannelHandlerContext context, RequestCommand request) {
        Serializer serializer = SerializerFactory.serializer(SerializerType.parse(request.getSerializerCode()));
        switch (request.getMessageCode()) {
            case ProtocolHead.REQUEST: {
                RequestWrapper requestWrapper = serializer.deserialize(request.getBody(), RequestWrapper.class);
                ServiceWrapper serviceWrapper = provider.lookupService(requestWrapper.getServiceMeta());

                ResponseCommand responseCommand = null;
                if (serviceWrapper == null) {
                    String message = String.format(
                            "%s service: [%s] not found",
                            context.channel(),
                            requestWrapper.getServiceMeta()
                    );
                    logger.warn(message);

                    if (!request.isOneWay()) {
                        responseCommand = RemotingCommandFactory.createResponseCommand(
                                request.getSerializerCode(),
                                serializer.serialize(message),
                                request.getInvokeId()
                        );

                        responseCommand.setStatus(ResponseStatus.SERVICE_NOT_FOUND.value());

                        responseCommand.setInvokeId(request.getInvokeId());
                    }
                } else {
                    Object result = Reflects.Invoke(
                            serviceWrapper.getServiceProvider(),
                            requestWrapper.getMethodName(),
                            requestWrapper.getArgs()
                    );
                    if (!request.isOneWay()) {
                        responseCommand = RemotingCommandFactory.createResponseCommand(
                                request.getSerializerCode(),
                                serializer.serialize(result),
                                request.getInvokeId()
                        );
                    }
                }
                return responseCommand;
            }
            default: {
                String errorMessage = String.format("DefaultProviderProcessor Unsupported MessageCode: %d",
                        request.getMessageCode());
                throw new UnsupportedOperationException(errorMessage);
            }
        }
    }

    @Override
    public boolean rejectRequest() {
        FlowController[] flowControllers = provider.globalFlowController();
        if (flowControllers != null && flowControllers.length > 0) {
            for (FlowController flowController : flowControllers) {
                try {
                    flowController.flowController();
                } catch (RejectedExecutionException e) {
                    logger.error(e.getMessage(), e);
                    return true;
                }
            }
        }
        return false;
    }
}