package org.rpc.rpc.model;

public class ResponseWrapper {

    private Object result;

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public void setCase(Throwable cause) {
        this.result = cause;
    }

    @Override
    public String toString() {
        return "ResponseWrapper{" +
                "result=" + result +
                '}';
    }
}
