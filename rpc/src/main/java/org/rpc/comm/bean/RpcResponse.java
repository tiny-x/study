package org.rpc.comm.bean;

/**
 * The type Rpc response.
 *
 * @author yefei
 * @date 2017 -6-20 13:13:03
 */
public class RpcResponse {

    private String requestId;
    private Exception exception;
    private Object result;

    /**
     * Has exception boolean.
     *
     * @return the boolean
     */
    public boolean hasException() {
        return exception != null;
    }

    /**
     * Gets request id.
     *
     * @return the request id
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Sets request id.
     *
     * @param requestId the request id
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * Gets exception.
     *
     * @return the exception
     */
    public Throwable getException() {
        return exception;
    }

    /**
     * Sets exception.
     *
     * @param exception the exception
     */
    public void setException(Exception exception) {
        this.exception = exception;
    }

    /**
     * Gets result.
     *
     * @return the result
     */
    public Object getResult() {
        return result;
    }

    /**
     * Sets result.
     *
     * @param result the result
     */
    public void setResult(Object result) {
        this.result = result;
    }
}
