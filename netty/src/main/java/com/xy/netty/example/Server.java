package com.xy.netty.example;

import com.xy.netty.example.payload.ByteHolder;
import com.xy.netty.example.payload.RequestBytes;
import com.xy.netty.example.protocol.Protocol;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * ----------------------------
 * serverBootstrap.bind(9000)
 * new NioServerSocketChannel
 * channel 注册到 selector上
 * ----------------------------
 * new NioServerSocketChannel 步骤
 *  打开jdk ServerSocketChannel
 *  id = newId();  channel Id
 *  new unsafe();
 *  new channelPipeline();
 * ------------------------------
 * register channel
 *
 * ----------------------------
 *
 */
public class Server {

    public static void main(String[] args) throws Exception {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup work = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        // 设置TCP数据接收缓冲区大小 1K 验证tcp 拆包
        serverBootstrap.group(boss, work)
                .childOption(ChannelOption.SO_RCVBUF, 1)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast( new Encoder());
                        ch.pipeline().addLast("decoder", new Decoder());
                        ch.pipeline().addLast("ServerHandler", new ServerHandler());
                    }
                });

        ChannelFuture sync = serverBootstrap.bind(9000).sync();
    }

    static class ServerHandler extends SimpleChannelInboundHandler<ByteHolder> {

        public ServerHandler() {
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteHolder msg) throws Exception {
            System.out.printf("receive client: %s", new String(msg.getBody()));

            // 16 byte
            String fix = "hello client!\n";
            String send = "";
            for (int i = 0; i < 1024; i++) {
                send += fix;
            }
            RequestBytes requestBytes = new RequestBytes(Protocol.JAVA, send.getBytes());

            ctx.channel().writeAndFlush(requestBytes);
        }

    }
}
