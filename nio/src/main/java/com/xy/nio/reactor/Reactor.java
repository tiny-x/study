package com.xy.nio.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @see  <a href=""http://gee.cs.oswego.edu/dl/cpjslides/nio.pdf></a>
 * reactor 单线程模型
 *
 */
public class Reactor implements Runnable {

    final Selector selector;
    final ServerSocketChannel serverSocketChannel;

    public Reactor(int port) throws IOException {
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);
        SelectionKey key = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        key.attach(new Acceptor());
    }

    public void run() {
        try {
            while (!Thread.interrupted()) {
                //等待事件
                int n = selector.select();
                if (n == 0)
                    continue;
                Set<SelectionKey> set = selector.selectedKeys();
                Iterator<SelectionKey> iterator = set.iterator();
                while (iterator.hasNext()) {
                    // 事件分发
                    dispatch(iterator.next());
                }
                set.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dispatch(SelectionKey k) {
        Runnable runnable = (Runnable) k.attachment();
        if (runnable != null)
            runnable.run();
    }

    /**
     * 接收线程
     */
    class Acceptor implements Runnable {
        public void run() {
            try {
                //接收请求
                SocketChannel c = serverSocketChannel.accept();
                if (c != null) {
                    System.out.println("New Connection ...");
                    new Handler(selector, c);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 事件处理器
     */
    final class Handler implements Runnable {

        final SocketChannel socket;
        final SelectionKey key;
        ByteBuffer input = ByteBuffer.allocate(1024);
        ByteBuffer output = ByteBuffer.allocate(1024);
        static final int READING = 0, SENDING = 1;
        int state = READING;

        public Handler(Selector selector, SocketChannel c) throws IOException {
            socket = c;
            socket.configureBlocking(false);
            key = socket.register(selector, 0);
            key.attach(this);
            //注册可读事件
            key.interestOps(SelectionKey.OP_READ);
            selector.wakeup();
        }

        public void run() {
            try {
                if (state == READING)
                    read();
                else if (state == SENDING)
                    send();
            } catch (Exception e) {
                e.printStackTrace();
            }
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
            System.out.println(reader.toString());
            String str = "HTTP/1.1 200 OK\r\nDate: Fir, 10 March 2017 21:20:01 GMT\r\n" +
                    "Content-Type: text/html;charset=UTF-8\r\nContent-Length: 23\r\nConnection:close" +
                    "\r\n\r\nHelloWorld" + System.currentTimeMillis();
            output.put(str.getBytes());
            System.out.println("process over.... ");
        }

        void read() throws IOException {
            socket.read(input);
            if (inputIsComplete()) {
                process();
                state = SENDING;
                key.interestOps(SelectionKey.OP_WRITE);
            }
        }

        void send() throws IOException {
            output.flip();
            socket.write(output);
            if (outputIsComplete()) {
                key.cancel();
            }
            state = READING;
            key.interestOps(SelectionKey.OP_READ);
        }
    }

    public static void main(String[] args) throws IOException {
        new Thread(new Reactor(9223)).start();
        System.out.println("Server start...");
    }
}
