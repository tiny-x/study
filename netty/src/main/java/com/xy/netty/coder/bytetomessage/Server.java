package com.xy.netty.coder.bytetomessage;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class Server {

    public static void main(String[] args) throws Exception {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup work = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boss, work)
                .childOption(ChannelOption.SO_RCVBUF, 1)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(
                                new ServerDecoder(),
                                new ServerHandler()
                        );
                    }
                });

        serverBootstrap.bind(9000).sync();
    }

    static class ServerDecoder extends ByteToMessageDecoder {

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
            while (in.readableBytes() > 0) {
                out.add(new String(in.readBytes(1).array()));
            }
        }
    }

    static class ServerHandler extends SimpleChannelInboundHandler<String> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            System.out.println(msg);
        }
    }
}
