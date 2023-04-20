package com.xy.nio.demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @author yefei
 * @date 2018-02-03 16:43
 */
public class Server {

    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //
        serverSocketChannel.socket().bind(new InetSocketAddress(9222));
        // 设置非阻塞
        serverSocketChannel
                .configureBlocking(false)
                .register(selector, SelectionKey.OP_ACCEPT);

        serverSocketChannel.accept();

        while (true) {
            int select = selector.select();
            if (select != 0) {

                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {

                    SelectionKey selectionKey = iterator.next();iterator.remove();
                    if (!selectionKey.isValid())
                        iterator.remove();

                    if (selectionKey.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) selectionKey.channel();
                        SocketChannel socketChannel = server.accept();
                        System.out.printf("客户端连接：%s \n", socketChannel.getRemoteAddress());
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);

                        ByteBuffer byteBuffer = ByteBuffer.wrap("hello".getBytes());
                        socketChannel.write(byteBuffer);
                    } else if (selectionKey.isReadable()) {
                        SocketChannel channel = (SocketChannel) selectionKey.channel();
                        ByteBuffer allocate = ByteBuffer.allocate(20);
                        channel.read(allocate);
                        byte[] array = allocate.array();
                        System.out.printf("收到客户端消息：%s \n", new String(array));

                    }

                }

            }
        }
    }
}
