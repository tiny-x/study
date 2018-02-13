package org.rpc.remoting.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.rpc.comm.UnresolvedAddress;
import org.rpc.remoting.InvokeCallback;
import org.rpc.remoting.future.ResponseFuture;
import org.rpc.remoting.payload.ByteHolder;
import org.rpc.remoting.payload.RequestBytes;
import org.rpc.remoting.RequestProcessor;
import org.rpc.remoting.RpcClient;
import org.rpc.remoting.payload.ResponseBytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NettyClient extends NettyServiceAbstract implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private final Bootstrap bootstrap = new Bootstrap();

    private final NioEventLoopGroup nioEventLoopGroupWorker = new NioEventLoopGroup();

    private final NettyClientConfig nettyClientConfig;

    private final Lock lockChannelTables = new ReentrantLock();
    private final Lock lockNamesrvChannel = new ReentrantLock();
    private static final long LOCK_TIMEOUT_MILLIS = 3000;

    private final AtomicReference<List<String>> nameServerAddressList = new AtomicReference<>();
    private final AtomicReference<String> nameServerAddressChose = new AtomicReference<>();

    private final ConcurrentMap<String, ChannelWrapper> channelTables = new ConcurrentHashMap<>();

    private final AtomicInteger namesrvIndex = new AtomicInteger(initValueIndex());

    private static int initValueIndex() {
        Random r = new Random();

        return Math.abs(r.nextInt() % 999) % 999;
    }

    public NettyClient(NettyClientConfig nettyClientConfig) {
        super(256, 256);
        this.nettyClientConfig = nettyClientConfig;
    }

    static class ChannelWrapper {
        private final ChannelFuture channelFuture;

        public ChannelWrapper(ChannelFuture channelFuture) {
            this.channelFuture = channelFuture;
        }

        public boolean isOK() {
            return this.channelFuture.channel() != null && this.channelFuture.channel().isActive();
        }

        public boolean isWritable() {
            return this.channelFuture.channel().isWritable();
        }

        private Channel getChannel() {
            return this.channelFuture.channel();
        }

        public ChannelFuture getChannelFuture() {
            return channelFuture;
        }
    }

    @Override
    public ResponseBytes invokeSync(UnresolvedAddress address, RequestBytes request, long timeout, TimeUnit timeUnit) throws Exception {
        Channel channel = getAndCreateChannel(address.toString());
        return invokeSync0(channel, request, timeout, timeUnit);
    }

    @Override
    public void invokeAsync(UnresolvedAddress address, RequestBytes request,
                            long timeout, TimeUnit timeUnit, InvokeCallback<ResponseBytes> invokeCallback) throws Exception {
        Channel channel = getAndCreateChannel(address.toString());
        invokeAsync0(channel, request, timeout, timeUnit, invokeCallback);
    }

    @Override
    public void registerRequestProcess(RequestProcessor requestProcessor, ExecutorService executor) {

    }

    private Channel getAndCreateChannel(final String address) throws InterruptedException {
        if (null == address)
            return getAndCreateNameserverChannel();

        ChannelWrapper cw = this.channelTables.get(address);
        if (cw != null && cw.isOK()) {
            return cw.getChannel();
        }

        return this.createChannel(address);
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

    private Channel getAndCreateNameserverChannel() throws InterruptedException {
        String addr = this.nameServerAddressChose.get();
        if (addr != null) {
            ChannelWrapper cw = this.channelTables.get(addr);
            if (cw != null && cw.isOK()) {
                return cw.getChannel();
            }
        }

        final List<String> addrList = this.nameServerAddressList.get();
        if (this.lockNamesrvChannel.tryLock(LOCK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
            try {
                addr = this.nameServerAddressChose.get();
                if (addr != null) {
                    ChannelWrapper cw = this.channelTables.get(addr);
                    if (cw != null && cw.isOK()) {
                        return cw.getChannel();
                    }
                }

                if (addrList != null && !addrList.isEmpty()) {
                    for (int i = 0; i < addrList.size(); i++) {
                        int index = this.namesrvIndex.incrementAndGet();
                        index = Math.abs(index);
                        index = index % addrList.size();
                        String newAddr = addrList.get(index);

                        this.nameServerAddressChose.set(newAddr);
                        logger.info("new name remoting is chosen. OLD: {} , NEW: {}. namesrvIndex = {}", addr, newAddr, namesrvIndex);
                        Channel channelNew = this.createChannel(newAddr);
                        if (channelNew != null)
                            return channelNew;
                    }
                }
            } catch (Exception e) {
                logger.error("getAndCreateNameserverChannel: create name remoting channel exception", e);
            } finally {
                this.lockNamesrvChannel.unlock();
            }
        }
        return null;
    }

    private Channel createChannel(final String addr) throws InterruptedException {
        ChannelWrapper cw = this.channelTables.get(addr);
        if (cw != null && cw.isOK()) {
            return cw.getChannel();
        }

        if (this.lockChannelTables.tryLock(LOCK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
            try {
                boolean createNewConnection = false;
                cw = this.channelTables.get(addr);
                if (cw != null) {

                    if (cw.isOK()) {
                        return cw.getChannel();
                    } else if (!cw.getChannelFuture().isDone()) {
                        createNewConnection = false;
                    } else {
                        this.channelTables.remove(addr);
                        createNewConnection = true;
                    }
                } else {
                    createNewConnection = true;
                }

                if (createNewConnection) {
                    String[] split = addr.split(":");
                    InetSocketAddress inetSocketAddress = new InetSocketAddress(split[0], Integer.valueOf(split[1]));
                    ChannelFuture channelFuture = this.bootstrap.connect(inetSocketAddress);
                    logger.info("createChannel: begin to connect remote host[{}] asynchronously", addr);
                    cw = new ChannelWrapper(channelFuture);
                    this.channelTables.put(addr, cw);
                }
            } catch (Exception e) {
                logger.error("createChannel: create channel exception", e);
            } finally {
                this.lockChannelTables.unlock();
            }
        } else {
            logger.warn("createChannel: try to lock channel table, but timeout, {}ms", LOCK_TIMEOUT_MILLIS);
        }

        if (cw != null) {
            ChannelFuture channelFuture = cw.getChannelFuture();
            if (channelFuture.awaitUninterruptibly(this.nettyClientConfig.getConnectTimeoutMillis())) {
                if (cw.isOK()) {
                    logger.info("createChannel: connect remote host[{}] success, {}", addr, channelFuture.toString());
                    return cw.getChannel();
                } else {
                    logger.warn("createChannel: connect remote host[" + addr + "] failed, " + channelFuture.toString(), channelFuture.cause());
                }
            } else {
                logger.warn("createChannel: connect remote host[{}] timeout {}ms, {}", addr, this.nettyClientConfig.getConnectTimeoutMillis(),
                        channelFuture.toString());
            }
        }

        return null;
    }

}
