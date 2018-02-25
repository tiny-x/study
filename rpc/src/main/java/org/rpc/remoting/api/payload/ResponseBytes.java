package org.rpc.remoting.api.payload;

import org.rpc.remoting.api.ResponseStatus;
import org.rpc.remoting.api.procotol.ProtocolHead;

public class ResponseBytes extends ByteHolder {

    /**
     * 响应状态
     */
    private byte status;

    private long invokeId;

    public ResponseBytes(byte serializerCode, byte[] body) {
        this(ProtocolHead.RESPONSE, serializerCode, body);
    }

    public ResponseBytes(byte messageCode, byte serializerCode, byte[] body) {
        super(messageCode, serializerCode, body);
        this.status = ResponseStatus.SUCCESS.value();
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
