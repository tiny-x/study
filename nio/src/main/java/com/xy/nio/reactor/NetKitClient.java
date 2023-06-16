package com.xy.nio.reactor;

import cn.hutool.json.JSONUtil;
import com.xy.model.Packet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class NetKitClient {
    static SocketChannel socketChannel = null;
    static Selector selector = null;

    static ByteBuffer input = ByteBuffer.allocate(10);

    public static void main(String[] args) throws IOException {
        selector = Selector.open();
        socketChannel = connect();

        //socketChannel.connect(new InetSocketAddress("127.0.0.1", 9223));

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                        write();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        process();
    }

    private static SocketChannel reconnect() throws IOException {
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(5);
                System.out.println("重连。。。。");
                return connect();
            } catch (Exception e) {
                // ignore
            }
        }
    }

    private static SocketChannel connect() throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 9765));
        while (!socketChannel.finishConnect()) {
            System.out.println("连接中...");
        }
        socketChannel.register(selector, SelectionKey.OP_READ);
        return socketChannel;
    }

    private static void process() throws IOException {
        while (true) {
            selector.select(1000);
            Set<SelectionKey> keys = selector.selectedKeys();//已选择键集
            Iterator<SelectionKey> it = keys.iterator();
            while (it.hasNext()) {  // 处理准备就绪的事件
                SelectionKey key = it.next();
                it.remove();  // 删除当前的键，避免重复消费
                if (key.isConnectable()) {
                    while (!socketChannel.finishConnect()) {
                        // 在非阻塞模式下connect也是非阻塞的，所以要确保连接已经建立完成
                        System.out.println("连接中...");
                    }
                    socketChannel.register(selector, SelectionKey.OP_READ);
                }
                System.out.println(socketChannel.isOpen());
                // 处理输入事件
                if (key.isReadable()) {
                    read();
                }
            }
        }
    }


    public static boolean read() throws IOException {
        int read = socketChannel.read(input);
        if (read == -1) {
            throw new IOException("EOF");
        }
        input.flip();
        if (input.limit() > 4) {
            int lengthField = input.getInt();
            if (input.limit() - 4 < lengthField) {
                byte[] array = input.array();
                // 不够解析协议体
                input = ByteBuffer.allocate(lengthField + 4);
                input.put(array);
                return false;
            }

            byte magic = input.get();
            if (magic != 10) {
                System.out.println("err magic");
            }
            int dataLen = input.getInt();
            short headLen = input.getShort();
            int messageLen = dataLen - headLen;

            if (headLen > 0) {
                Map<String, String> header = new HashMap<>();
                do {
                    short headKeyLen = input.getShort();
                    byte[] keyBytes = new byte[headKeyLen];
                    input.get(keyBytes, 0, headKeyLen);

                    int headValueLen = input.getInt();
                    byte[] valueBytes = new byte[headValueLen];
                    input.get(valueBytes, 0, headValueLen);

                    // 默认6 不知道是 bug 还是故意的
                    headLen = (short) (headLen - 2 - 4 - 6 - headKeyLen - headValueLen);
                    header.put(new String(keyBytes, Charset.defaultCharset()), new String(valueBytes, Charset.defaultCharset()));
                    System.out.println(header);
                } while (headLen > 0);
            }

            byte[] bytes = new byte[messageLen];
            input.get(bytes, 0, messageLen);
            System.out.println(new String(bytes));
        }
        input.clear();
        return true;
    }

    /**
     * ----------------------------------------------------------
     * | 第1字节  | 第2~5字节 | 第6字节   | 第7~N字节   | 剩余字节   |
     * ----------------------------------------------------------
     * | 协议标识 | 数据长度   | 协议头长度 | 扩展协议头  | 数据内容  |
     * -----------------------------------------------------------
     */
    public static void write() throws IOException {
        Packet packet = new Packet();
        packet.setId("11");
        packet.setType("request");
        packet.setCommand("HEARTBEAT");
        Map<String, String> header = new HashMap<>(4);
        header.put("$netkit.serialization", "json");
        packet.setHeader(header);
        packet.setPayload("hahha".getBytes());

        String s = JSONUtil.toJsonStr(packet);
        byte[] message = s.getBytes();
        //这里的 len
        int magicBytes = 1;
        int dateBytes = 4;
        int headerBytes = 2;
        int headerKeyBytes = 2;
        int headerValueBytes = 4;

        int lengthField = magicBytes + dateBytes + headerBytes + message.length;
        for (Map.Entry<String, String> entry : header.entrySet()) {
            lengthField = lengthField + headerKeyBytes + entry.getKey().getBytes(Charset.defaultCharset()).length + headerValueBytes + entry.getValue().getBytes(Charset.defaultCharset()).length;
        }
        ByteBuffer output = ByteBuffer.allocate(4 + lengthField);
        // lengthField
        output.putInt(lengthField);
        // magic
        output.put((byte) 10);

        // 第2~5字节, 其实就是去除了 magic
        output.putInt(lengthField - 1);
        // 第6字节（这里 xcenter 改成2个字节了，后面顺延）
        // 协议头长度默认是6 ，这个他也没写，加上扩展协议
        short headerLength = 6;
        for (Map.Entry<String, String> entry : header.entrySet()) {
            headerLength = (short) (headerLength + headerKeyBytes + entry.getKey().getBytes(Charset.defaultCharset()).length + headerValueBytes + entry.getValue().getBytes(Charset.defaultCharset()).length);
        }
        output.putShort(headerLength);
        // 第7~N字节（扩展协议头）
        for (Map.Entry<String, String> entry : header.entrySet()) {
            byte[] key = entry.getKey().getBytes(Charset.defaultCharset());
            short keySize = (short) key.length;
            output.putShort(keySize);
            output.put(key);

            byte[] value = entry.getValue().getBytes(Charset.defaultCharset());
            output.putInt(value.length);
            output.put(value);
        }
        // 数据内容
        output.put(message);
        output.flip();
        socketChannel.write(output);
    }
}
