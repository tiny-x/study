package com.xy.netty.example;

import com.xy.netty.config.LoggerConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author leaf 2017年6月13日下午9:56:50
 */
public class ServerExample {

    /**
     * logger
     */
    private final static Logger logger = LoggerFactory.getLogger(ServerExample.class);

    static {
        LoggerConfig.initConfig();
    }

    public void main(String[] args) {
        new ServerExample().init(9091);
    }

    public void init(int port) {
        // 主进程
        EventLoopGroup mainGroup = new NioEventLoopGroup(1);
        // 工作进程
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            // 绑定线程组--> 设置channel --> channleHandler --> pipeline add ChannelHandler
            serverBootstrap.group(mainGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("decoder", new StringDecoder());
                            ch.pipeline().addLast("encoder", new StringEncoder());
                            ch.pipeline().addLast(new ServerHandler());
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            logger.info("server start listen port: {}", port);
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 优雅关闭
            mainGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
