package com.xy.netty.reconnect;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class Client {

    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    private static Bootstrap bootstrap = new Bootstrap();

    private static Timer timer = new HashedWheelTimer();

    public static void main(String[] args) throws Exception {
        NioEventLoopGroup work = new NioEventLoopGroup();

        bootstrap.group(work)
                .option(ChannelOption.SO_RCVBUF, 1)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast("encoder", new Encoder());
                        ch.pipeline().addLast("decoder", new Decoder());
                        ch.pipeline().addLast("ClientHandler", new ClientHandler());

                    }
                });
        bootstrap.connect("localhost", 9000).sync();
    }

    static class ClientHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            // 16 byte
            String fix = "01234567890ABCDEF";
            String send = "";
            for (int i = 0; i < 1024; i++) {
                send += fix;
            }

            Protocol protocol = new Protocol();
            protocol.setLength((short) (16 * 1024));
            protocol.setBody(send.getBytes());

            Channel channel = ctx.channel();
            channel.writeAndFlush(protocol);
            super.channelActive(ctx);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {

            timer.newTimeout(new TimerTask() {
                @Override
                public void run(Timeout timeout) throws Exception {
                    bootstrap.connect("127.0.0.1", 9000)
                            .addListener(new ChannelFutureListener() {
                                @Override
                                public void operationComplete(ChannelFuture future) throws Exception {

                                    logger.warn("Reconnects with 127.0.0.1, {}.", future.isSuccess() ? "succeed" : "failed");

                                    if (!future.isSuccess()) {

                                        future.channel().pipeline().fireChannelInactive();
                                    } else {

                                        // 16 byte
                                        String fix = "01234567890ABCDEF";
                                        String send = "";
                                        for (int i = 0; i < 1024; i++) {
                                            send += fix;
                                        }

                                        Protocol protocol = new Protocol();
                                        protocol.setLength((short) (16 * 1024));
                                        protocol.setBody(send.getBytes());

                                        Channel channel = ctx.channel();
                                        channel.writeAndFlush(protocol);
                                    }
                                }
                            });
                }
            }, 2, TimeUnit.SECONDS);
        }
    }

}
