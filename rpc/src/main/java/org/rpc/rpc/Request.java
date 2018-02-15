package org.rpc.rpc;

import org.rpc.remoting.api.payload.RequestBytes;
import org.rpc.rpc.model.RequestWrapper;

public class Request {

    private RequestBytes requestBytes;

    private RequestWrapper requestWrapper;

    public Request() {
    }

    public Request(RequestBytes requestBytes, RequestWrapper requestWrapper) {
        this.requestBytes = requestBytes;
        this.requestWrapper = requestWrapper;
    }

    public RequestBytes getRequestBytes() {
        return requestBytes;
    }

    public void setRequestBytes(RequestBytes requestBytes) {
        this.requestBytes = requestBytes;
    }

    public RequestWrapper getRequestWrapper() {
        return requestWrapper;
    }

    public void setRequestWrapper(RequestWrapper requestWrapper) {
        this.requestWrapper = requestWrapper;
    }
}
