package org.rpc.rpc;

import org.rpc.remoting.api.payload.ResponseBytes;
import org.rpc.rpc.model.ResponseWrapper;

public class Response {

    private ResponseBytes responseBytes;

    private ResponseWrapper responseWrapper;

    public Response() {
    }

    public Response(ResponseBytes responseBytes, ResponseWrapper responseWrapper) {
        this.responseBytes = responseBytes;
        this.responseWrapper = responseWrapper;
    }

    public ResponseBytes getResponseBytes() {
        return responseBytes;
    }

    public void setResponseBytes(ResponseBytes responseBytes) {
        this.responseBytes = responseBytes;
    }

    public ResponseWrapper getResponseWrapper() {
        return responseWrapper;
    }

    public void setResponseWrapper(ResponseWrapper responseWrapper) {
        this.responseWrapper = responseWrapper;
    }
}
