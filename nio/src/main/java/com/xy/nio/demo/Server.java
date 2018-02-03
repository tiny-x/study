package com.xy.nio.demo;

import java.io.IOException;
import java.net.InetSocketAddress;
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
        serverSocketChannel
                .socket()
                .bind(new InetSocketAddress(9000));
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
                    iterator.remove();
                    SelectionKey selectionKey = iterator.next();

                    if (selectionKey.isAcceptable()) {
                        SocketChannel channel = (SocketChannel) selectionKey.channel();
                        channel.register(selector, SelectionKey.OP_READ);
                    }

                }

            }
        }
    }
}
