package com.xy.netty.heartbeats;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {

    private static final Logger logger = LoggerFactory.getLogger(Server.class);

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
                        ch.pipeline().addLast("encoder", new Encoder());
                        ch.pipeline().addLast("decoder", new Decoder());
                        ch.pipeline().addLast("idle", new IdleStateHandler(0, 0, 4));
                        ch.pipeline().addLast("heartbeat", new HeartbeatServerHandler());
                    }
                });

        serverBootstrap.bind(9000).sync();
    }

    static class HeartbeatServerHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent event = (IdleStateEvent) evt;
                if (event.state().equals(IdleState.ALL_IDLE)) {
                    logger.info(event.state().name());
                }
            }
            ctx.fireUserEventTriggered(evt);
        }
    }
}
