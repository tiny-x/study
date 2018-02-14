package org.rpc.remoting.api.payload;

public abstract class ByteHolder {

    private byte serializerCode;

    private byte[] body;

    public ByteHolder(byte serializerCode, byte[] body) {
        this.serializerCode = serializerCode;
        this.body = body;
    }

    public byte getSerializerCode() {
        return serializerCode;
    }

    public byte[] getBody() {
        return body;
    }
}
