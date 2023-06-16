package com.xy.nio.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class TestOpenAndConnectClient {
    static SocketChannel socketChannel = null;
    static Selector selector = null;

    public static void main(String[] args) throws IOException {
        socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9222));
        socketChannel.configureBlocking(false);

        selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_READ);
        selector.wakeup();
        int result;
        int i = 1;
        Handler handler = new Handler(selector, socketChannel);
        while (!Thread.interrupted()) {
            while ((result = selector.select()) > 0) {
                System.out.printf("selector %dth loop, ready event number is %d%n \n", i++, result);
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey sk = iterator.next();
                    if (sk.isReadable()) {
                        handler.read(sk);
                        System.out.println("有数据可读");
                    }
                    iterator.remove();
                }
            }
        }
    }


    /**
     * 事件处理器
     */
    final static class Handler {

        final SocketChannel socket;
        ByteBuffer input = ByteBuffer.allocate(1024);
        ByteBuffer output = ByteBuffer.allocate(1024);
        static final int READING = 0, SENDING = 1;
        int state = READING;

        public Handler(Selector selector, SocketChannel c) throws IOException {
            socket = c;
            selector.wakeup();
        }

        boolean inputIsComplete() {
            return input.hasRemaining();
        }

        boolean outputIsComplete() {
            return output.hasRemaining();
        }

        void process() {
            //读数据
            StringBuilder reader = new StringBuilder();
            input.flip();
            while (input.hasRemaining()) {
                reader.append((char) input.get());
            }
            System.out.println("[Client-INFO]");
            System.out.println(reader);
            System.out.println("process over.... ");
        }

        void read(SelectionKey key) throws IOException {
            socket.read(input);
            if (inputIsComplete()) {
                process();
                state = SENDING;
                key.interestOps(SelectionKey.OP_WRITE);
            }
        }

        void send(SelectionKey key) throws IOException {
            output.flip();
            socket.write(output);
            if (outputIsComplete()) {
                key.cancel();
            }
            state = READING;
            key.interestOps(SelectionKey.OP_READ);
        }
    }
}
