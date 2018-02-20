package org.rpc.remoting.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.rpc.remoting.api.InvokeCallback;
import org.rpc.remoting.api.future.ResponseFuture;
import org.rpc.remoting.api.payload.ByteHolder;
import org.rpc.remoting.api.payload.RequestBytes;
import org.rpc.remoting.api.ChannelEventListener;
import org.rpc.remoting.api.RequestProcessor;
import org.rpc.remoting.api.RpcServer;
import org.rpc.remoting.netty.event.ChannelEvent;
import org.rpc.remoting.netty.event.ChannelEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class NettyServer extends NettyServiceAbstract implements RpcServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private final ServerBootstrap serverBootstrap;

    private final NioEventLoopGroup nioEventLoopGroupWorker;

    private final NioEventLoopGroup nioEventLoopGroupMain;

    private final NettyServerConfig config;

    private final ChannelEventListener channelEventListener;

    public NettyServer(NettyServerConfig config) {
        this(config, null);
    }

    public NettyServer(NettyServerConfig config, ChannelEventListener listener) {
        super(256, 256);
        this.config = config;
        this.channelEventListener = listener;
        this.serverBootstrap = new ServerBootstrap();
        this.nioEventLoopGroupWorker = new NioEventLoopGroup();
        this.nioEventLoopGroupMain = new NioEventLoopGroup();
    }

    @Override
    public void invokeSync(Channel channel, RequestBytes request, long timeout, TimeUnit timeUnit) {

    }

    @Override
    public void invokeAsync(Channel channel, RequestBytes request, long timeout, TimeUnit timeUnit, InvokeCallback<ResponseFuture> invokeCallback) throws Exception {

    }

    @Override
    public void registerRequestProcess(RequestProcessor requestProcessor, ExecutorService executor) {
        defaultProcessor.setA(requestProcessor);
        defaultProcessor.setB(executor);
    }

    @Override
    public void start() {

        serverBootstrap.group(nioEventLoopGroupMain, nioEventLoopGroupWorker)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 32768)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast("NettyEncoder", new NettyEncoder());
                        socketChannel.pipeline().addLast("NettyDecoder", new NettyDecoder());
                        socketChannel.pipeline().addLast("NettyServerHandler", new NettyServerHandler());
                        socketChannel.pipeline().addLast("IdleStateHandler", new IdleStateHandler(0, 0, 10));
                        socketChannel.pipeline().addLast("NettyConnectManageHandler", new NettyConnectManageHandler());
                    }
                });
        try {
            serverBootstrap.bind(config.getPort()).sync();
        } catch (InterruptedException e) {
            logger.error("serverBootstrap start error ", e);
        }
    }

    class NettyServerHandler extends SimpleChannelInboundHandler<ByteHolder> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteHolder msg) throws Exception {
            processMessageReceived(ctx, msg);
        }
    }


    class NettyConnectManageHandler extends ChannelDuplexHandler {
        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            final String remoteAddress = ctx.channel().remoteAddress().toString();
            logger.info("NETTY SERVER PIPELINE: channelRegistered {}", remoteAddress);
            super.channelRegistered(ctx);
        }

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
            final String remoteAddress = ctx.channel().remoteAddress().toString();
            logger.info("NETTY SERVER PIPELINE: channelUnregistered, the channel[{}]", remoteAddress);
            super.channelUnregistered(ctx);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            final String remoteAddress = ctx.channel().remoteAddress().toString();
            logger.info("NETTY SERVER PIPELINE: channelActive, the channel[{}]", remoteAddress);
            super.channelActive(ctx);

            if (NettyServer.this.channelEventListener != null) {
                NettyServer.this.putChannelEvent(new ChannelEvent(ChannelEventType.CONNECT, remoteAddress, ctx.channel()));
            }
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            final String remoteAddress = ctx.channel().remoteAddress().toString();
            logger.info("NETTY SERVER PIPELINE: channelInactive, the channel[{}]", remoteAddress);
            super.channelInactive(ctx);

            if (NettyServer.this.channelEventListener != null) {
                NettyServer.this.putChannelEvent(new ChannelEvent(ChannelEventType.CLOSE, remoteAddress, ctx.channel()));
            }
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent event = (IdleStateEvent) evt;
                if (event.state().equals(IdleState.ALL_IDLE)) {
                    final String remoteAddress = ctx.channel().remoteAddress().toString();
                    logger.warn("NETTY SERVER PIPELINE: IDLE exception [{}]", remoteAddress);
                    if (NettyServer.this.channelEventListener != null) {
                        NettyServer.this
                                .putChannelEvent(new ChannelEvent(ChannelEventType.IDLE, remoteAddress, ctx.channel()));
                    }
                }
            }

            ctx.fireUserEventTriggered(evt);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            final String remoteAddress = ctx.channel().remoteAddress().toString();
            logger.warn("NETTY SERVER PIPELINE: exceptionCaught {}", remoteAddress);
            logger.warn("NETTY SERVER PIPELINE: exceptionCaught exception.", cause);

            if (NettyServer.this.channelEventListener != null) {
                NettyServer.this.putChannelEvent(new ChannelEvent(ChannelEventType.EXCEPTION, remoteAddress, ctx.channel()));
            }
            ctx.channel().close();
        }
    }

    private void putChannelEvent(ChannelEvent channelEvent) {
        this.channelEventExecutor.putChannelEvent(channelEvent);
    }

    @Override
    protected ChannelEventListener getChannelEventListener() {
        return this.channelEventListener;
    }

    @Override
    public void shutdown() {
        if (Objects.nonNull(nioEventLoopGroupMain)) {
            nioEventLoopGroupMain.shutdownGracefully();
        }
        if (Objects.nonNull(nioEventLoopGroupWorker)) {
            nioEventLoopGroupWorker.shutdownGracefully();
        }
    }

}
