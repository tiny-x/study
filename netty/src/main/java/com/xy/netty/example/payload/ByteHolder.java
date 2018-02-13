package com.xy.netty.example.payload;

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

    public void setSerializerCode(byte serializerCode) {
        this.serializerCode = serializerCode;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
