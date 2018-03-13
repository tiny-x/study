package com.xy.netty.coder.bytetomessage;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
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
                    }
                });
        ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 9000).sync();

        // 16 byte
        String fix = "01234567890ABCDEF";
        //ByteBuf byteBuf = Unpooled.buffer().writeBytes(fix.getBytes());
        // zero copy
        ByteBuf byteBuf = Unpooled.wrappedBuffer(fix.getBytes());

        channelFuture.channel().writeAndFlush(byteBuf).sync();
        channelFuture.channel().closeFuture().sync();
    }
}
