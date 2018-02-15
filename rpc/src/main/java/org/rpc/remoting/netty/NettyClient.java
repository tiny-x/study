package org.rpc.remoting.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.rpc.comm.UnresolvedAddress;
import org.rpc.exception.RemotingConnectException;
import org.rpc.exception.RemotingException;
import org.rpc.exception.RemotingTimeoutException;
import org.rpc.remoting.api.Directory;
import org.rpc.remoting.api.InvokeCallback;
import org.rpc.remoting.api.RequestProcessor;
import org.rpc.remoting.api.RpcClient;
import org.rpc.remoting.api.channel.ChannelGroup;
import org.rpc.remoting.api.channel.DirectoryChannelGroup;
import org.rpc.remoting.api.payload.ByteHolder;
import org.rpc.remoting.api.payload.RequestBytes;
import org.rpc.remoting.api.payload.ResponseBytes;
import org.rpc.remoting.channel.NettyChannelGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ConnectException;
import java.util.concurrent.*;

import static com.google.common.base.Preconditions.checkNotNull;

public class NettyClient extends NettyServiceAbstract implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private final Bootstrap bootstrap = new Bootstrap();

    private final NioEventLoopGroup nioEventLoopGroupWorker = new NioEventLoopGroup();

    private final NettyClientConfig nettyClientConfig;

    private final ConcurrentMap<String, ChannelGroup> addressGroups = new ConcurrentHashMap<>();

    private final DirectoryChannelGroup directoryChannelGroup = new DirectoryChannelGroup();

    public NettyClient(NettyClientConfig nettyClientConfig) {
        super(256, 256);
        this.nettyClientConfig = nettyClientConfig;
    }

    @Override
    public void connect(UnresolvedAddress address) throws InterruptedException, RemotingConnectException {
        checkNotNull(address);
        ChannelGroup group = group(address);
        ChannelFuture future = bootstrap.connect(address.getHost(), address.getPort());

        long connectTimeoutMillis = this.nettyClientConfig.getConnectTimeoutMillis();
        if (future.awaitUninterruptibly(connectTimeoutMillis, TimeUnit.MILLISECONDS)) {
            if (future.channel() != null && future.channel().isActive()) {
                group.addChannel(future.channel());
            } else {
                throw new RemotingConnectException(address.toString());
            }
        } else {
            throw new RemotingConnectException(address.toString());
        }
    }

    @Override
    public ChannelGroup group(UnresolvedAddress address) {

        ChannelGroup group = addressGroups.get(address);
        if (group == null) {
            ChannelGroup newGroup = new NettyChannelGroup(address);
            group = addressGroups.putIfAbsent(address.toString(), newGroup);
            if (group == null) {
                group = newGroup;
            }
        }
        return group;
    }

    @Override
    public boolean addChannelGroup(Directory directory, ChannelGroup group) {
        CopyOnWriteArrayList groups = directoryChannelGroup.find(directory);
        boolean added = groups.addIfAbsent(group);
        if (added) {
            if (logger.isInfoEnabled()) {
                logger.info("Added channel group: {} to {}.", group, directory.directory());
            }
        }
        return added;
    }

    @Override
    public boolean removeChannelGroup(Directory directory, ChannelGroup group) {
        CopyOnWriteArrayList groups = directoryChannelGroup.find(directory);
        boolean added = groups.remove(group);
        if (added) {
            if (logger.isInfoEnabled()) {
                logger.info("Added channel group: {} to {}.", group, directory.directory());
            }
        }
        return added;
    }

    @Override
    public CopyOnWriteArrayList<ChannelGroup> directory(Directory directory) {
        return directoryChannelGroup.find(directory);
    }

    @Override
    public boolean isDirectoryAvailable(Directory directory) {
        CopyOnWriteArrayList<ChannelGroup> groups = directory(directory);
        for (ChannelGroup g : groups) {
            if (g.isAvailable()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ResponseBytes invokeSync(final Channel channel, RequestBytes request, long timeout, TimeUnit timeUnit) throws RemotingException, InterruptedException {
        return invokeSync0(channel, request, timeout, timeUnit);
    }

    @Override
    public void invokeAsync(final Channel channel, RequestBytes request,
                            long timeout, TimeUnit timeUnit, InvokeCallback<ResponseBytes> invokeCallback) throws RemotingException, InterruptedException {
        invokeAsync0(channel, request, timeout, timeUnit, invokeCallback);
    }

    @Override
    public void registerRequestProcess(RequestProcessor requestProcessor, ExecutorService executor) {

    }

    @Override
    public void start() {
        bootstrap.group(nioEventLoopGroupWorker)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, false)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast("NettyEncoder", new NettyEncoder());
                        socketChannel.pipeline().addLast("NettyDecoder", new NettyDecoder());
                        socketChannel.pipeline().addLast("NettyClientHandler", new NettyClientHandler());
                    }
                });

    }

    class NettyClientHandler extends SimpleChannelInboundHandler<ByteHolder> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteHolder msg) throws Exception {
            processMessageReceived(ctx, msg);
        }
    }

    @Override
    public void shutdown() {

    }

}
