package org.rpc.remoting.api.procotol;


/**
 *   *                                          ProtocolHead
 *  ┌ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┐
 *       2   │   1   │    1   │     8     │      4      │
 *  ├ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┤
 *           │       │        │           │             │
 *  │  MAGIC   Sign    Status   Invoke Id   Body Length                   Body Content              │
 *           │       │        │           │             │
 *  └ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ─ ┘
 *
 */
public class ProtocolHead {

    // ====== messageCode 奇数 request 偶数 response
    public static final byte REQUEST = 0x01;     // Request
    public static final byte RESPONSE = 0x02;     // Response

    public static final byte REGISTER_SERVICE = 0x03;     // 注册服务
    public static final byte REGISTER_RECEIVE = 0x04;

    public static final byte SUBSCRIBE_SERVICE = 0x05;    // 监听服务
    public static final byte SUBSCRIBE_RECEIVE = 0x06;

    public static final byte OFFLINE_SERVICE = 0x07;  // 下线服务
    public static final byte OFFLINE_RECEIVE = 0x08;

    // ====== serializerCode
    public static final byte PROTO_STUFF = 0x01;
    public static final byte JSON = 0x02;

    public static final short MAGIC = (short) (0xcaff);

    public static final byte STATUS_SUCCESS = 0x01; // success
    public static final byte STATUS_ERROR = 0x02; //
    public static final byte STATUS_TIMEOUT = 0x03;

    /**
     * 消息类型（高四位），序列化方式（低四位）
     */
    private byte sign;

    private byte messageCode;

    private byte serializerCode;

    private byte status;

    private long invokeId;

    private int bodyLength;

    public ProtocolHead() {

    }

    public static byte toSign(byte messageCode, byte serializerCode) {
        return (byte) ((messageCode << 4) | serializerCode);
    }

    public void setSign(byte sign) {
        this.serializerCode = (byte) (sign & 0x0F);
        this.messageCode = (byte) ((sign & 0xF0) >> 4);
    }

    public byte getSign(byte sign) {
        return sign;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public long getInvokeId() {
        return invokeId;
    }

    public void setInvokeId(long invokeId) {
        this.invokeId = invokeId;
    }

    public int getBodyLength() {
        return bodyLength;
    }

    public void setBodyLength(int bodyLength) {
        this.bodyLength = bodyLength;
    }

    public byte getMessageCode() {
        return messageCode;
    }

    public byte getSerializerCode() {
        return serializerCode;
    }

}
