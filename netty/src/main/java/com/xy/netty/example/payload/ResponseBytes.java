package com.xy.netty.example.payload;

public class ResponseBytes extends ByteHolder {

    /**
     * 响应状态
     */
    private byte status;

    private long invokeId;

    public ResponseBytes(byte serializerCode, byte[] body, byte status, long invokeId) {
        super(serializerCode, body);
        this.status = status;
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
