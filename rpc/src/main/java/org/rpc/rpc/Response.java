package org.rpc.rpc;

import org.rpc.remoting.api.payload.ResponseCommand;
import org.rpc.rpc.model.ResponseWrapper;

public class Response {

    private ResponseCommand responseCommand;

    private ResponseWrapper responseWrapper;

    public Response() {
    }

    public Response(ResponseCommand responseCommand, ResponseWrapper responseWrapper) {
        this.responseCommand = responseCommand;
        this.responseWrapper = responseWrapper;
    }

    public ResponseCommand getResponseCommand() {
        return responseCommand;
    }

    public void setResponseCommand(ResponseCommand responseCommand) {
        this.responseCommand = responseCommand;
    }

    public ResponseWrapper getResponseWrapper() {
        return responseWrapper;
    }

    public void setResponseWrapper(ResponseWrapper responseWrapper) {
        this.responseWrapper = responseWrapper;
    }
}
