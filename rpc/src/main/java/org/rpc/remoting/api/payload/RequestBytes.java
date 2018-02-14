package org.rpc.remoting.api.payload;

import java.util.concurrent.atomic.AtomicLong;

public class RequestBytes extends ByteHolder {

    private static final AtomicLong REQUEST_ID = new AtomicLong(0l);

    private long timestamp ;

    private long invokeId;

    public RequestBytes(byte serializerCode, byte[] body) {
        super(serializerCode, body);
        this.invokeId = REQUEST_ID.incrementAndGet();
        this.timestamp = System.currentTimeMillis();
    }

    public RequestBytes(Long invokeId, byte serializerCode, byte[] body) {
        super(serializerCode, body);
        this.invokeId = invokeId;
        this.timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getInvokeId() {
        return invokeId;
    }

    @Override
    public String toString() {
        return "RequestBytes{" +
                "timestamp=" + timestamp +
                ", invokeId=" + invokeId +
                '}';
    }
}
