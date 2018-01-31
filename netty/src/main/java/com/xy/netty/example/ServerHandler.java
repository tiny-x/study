/**
 *
 */
package com.xy.netty.example;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author leaf 2017年6月13日下午10:07:55
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * logger
     */
    private final static Logger logger = LoggerFactory.getLogger(ServerHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("server channelRead..");
        logger.info("{} --> ServerExample : {}", ctx.channel().remoteAddress(), msg.toString());
        ctx.writeAndFlush("hello client!");
        super.channelRead(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        super.exceptionCaught(ctx, cause);
    }

}
