package com.xy.bio;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author yefei
 * @date 2018-06-20 9:51
 */
public class Server {

    public static void main(String[] args) throws Exception {

        ServerSocket serverSocket = new ServerSocket(12345);
        while (true) {
            // io线程阻塞式 接受连接
            Socket socket = serverSocket.accept();
            System.out.println("client：" + socket.getRemoteSocketAddress().toString() + "接入連接。");
            // 无法支撑过多的连接数
            new Thread(() -> {
                try {
                    // io线程阻塞式等待读事件(bio无法拆分读事件和读操作)
                    InputStream inputStream = socket.getInputStream();
                    // 设配器模式 装饰器模式 编解码
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    System.out.println("recive -->" + socket.getRemoteSocketAddress().toString() + ":" +reader.readLine());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
