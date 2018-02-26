package org.rpc.remoting.api.payload;

import org.rpc.remoting.api.ResponseStatus;
import org.rpc.remoting.api.procotol.ProtocolHead;

public class ResponseCommand extends ByteHolder {

    /**
     * 响应状态
     */
    private byte status;

    private long invokeId;

    public ResponseCommand(byte serializerCode, byte[] body, long invokeId) {
        this(ProtocolHead.RESPONSE, serializerCode, body, invokeId);
    }

    public ResponseCommand(byte messageCode, byte serializerCode, byte[] body, long invokeId) {
        super(messageCode, serializerCode, body);
        this.status = ResponseStatus.SUCCESS.value();
        this.invokeId = invokeId;
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
        return "ResponseCommand{" +
                "status=" + status +
                ", invokeId=" + invokeId +
                '}';
    }
}
