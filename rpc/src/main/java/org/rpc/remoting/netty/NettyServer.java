package org.rpc.remoting.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.rpc.remoting.api.InvokeCallback;
import org.rpc.remoting.api.future.ResponseFuture;
import org.rpc.remoting.api.payload.ByteHolder;
import org.rpc.remoting.api.payload.RequestBytes;
import org.rpc.remoting.api.ChannelEventListener;
import org.rpc.remoting.api.RequestProcessor;
import org.rpc.remoting.api.RpcServer;
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

    public NettyServer(NettyServerConfig config) {
        this(config, null);
    }

    public NettyServer(NettyServerConfig config, ChannelEventListener listener) {
        super(256, 256);
        this.config = config;
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
                .childOption(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast("NettyEncoder", new NettyEncoder());
                        socketChannel.pipeline().addLast("NettyDecoder", new NettyDecoder());
                        socketChannel.pipeline().addLast("NettyServerHandler", new NettyServerHandler());
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
