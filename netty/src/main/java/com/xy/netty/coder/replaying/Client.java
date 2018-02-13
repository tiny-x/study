package com.xy.netty.coder.replaying;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Client {

    public static void main(String[] args) throws Exception {
        NioEventLoopGroup work = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(work)
                .option(ChannelOption.SO_RCVBUF, 1)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast("encoder", new Encoder());
                        ch.pipeline().addLast("decoder", new Decoder());

                    }
                });
        ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 9000).sync();

        // 16 byte
        String fix = "01234567890ABCDEF";
        String send = "";
        for (int i = 0; i < 1024; i++) {
            send += fix;
        }

        channelFuture.channel().writeAndFlush(send).sync();
        channelFuture.channel().closeFuture().sync();
    }
}
