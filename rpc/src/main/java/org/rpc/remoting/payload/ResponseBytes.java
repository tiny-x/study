package org.rpc.remoting.payload;

public class ResponseBytes extends ByteHolder {

    /**
     * 响应状态
     */
    private byte status;

    private long invokeId;

    public ResponseBytes(byte serializerCode, byte[] body) {
        super(serializerCode, body);
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public void setInvokeId(long invokeId) {
        this.invokeId = invokeId;
    }

    public byte getStatus() {
        return status;
    }

    public long getInvokeId() {
        return invokeId;
    }

    @Override
    public String toString() {
        return "ResponseBytes{" +
                "status=" + status +
                ", invokeId=" + invokeId +
                '}';
    }
}
