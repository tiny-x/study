package com.xy.netty.example;

import com.xy.netty.config.LoggerConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author leaf 2017年6月13日下午10:15:50
 */
public class ClientExample {

    /**
     * logger
     */
    private final static Logger logger = LoggerFactory.getLogger(ClientExample.class);

    static {
        LoggerConfig.initConfig();
    }

    public static void main(String[] args) {
        new ClientExample().init("localhost", 9091);
    }

    public void init(String hostname, int port) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast("decoder", new StringDecoder());
                            p.addLast("encoder", new StringEncoder());
                            p.addLast(new ClientHandler());
                        }
                    });

            SocketAddress socketAddress = new InetSocketAddress(hostname, port);
            ChannelFuture future = b.connect(socketAddress).sync();
            future.channel().writeAndFlush("hello world").sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}
