package org.rpc.rpc;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.rpc.comm.UnresolvedAddress;
import org.rpc.comm.bean.RpcRequest;
import org.rpc.comm.bean.RpcResponse;
import org.rpc.comm.coder.RpcDecoder;
import org.rpc.comm.coder.RpcEncoder;
import org.rpc.rpc.future.InvokeFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;

/**
 * The type Rpc client.
 *
 * @author yefei
 * @date 2017 -06-20 16:24
 */
public class RpcClient {

    /**
     * logger
     */
    private final static Logger logger = LoggerFactory.getLogger(RpcClient.class);

    private Bootstrap bootstrap;

    private Map<String, ChannelFuture> channelGroup = new ConcurrentHashMap<>();

    private UnresolvedAddress unresolvedAddress;

    private Map<String, InvokeFuture<RpcResponse>> message = new ConcurrentHashMap<>();

    /**
     * Instantiates a new Rpc client.
     *
     * @param unresolvedAddress the unresolved address
     */
    public RpcClient(UnresolvedAddress unresolvedAddress) {
        this.unresolvedAddress = unresolvedAddress;
        initialize();
    }

    /**
     * Gets channel group.
     *
     * @return the channel group
     */
    public Map<String, ChannelFuture> getChannelGroup() {
        return channelGroup;
    }

    /**
     * Get unresolved address unresolved address.
     *
     * @return the unresolved address
     */
    public UnresolvedAddress getUnresolvedAddress() {
        return unresolvedAddress;
    }

    /**
     * Initialize.
     */
    public void initialize() {
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            bootstrap = new Bootstrap();
            bootstrap.group(worker)//
                    .channel(NioSocketChannel.class)//
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new RpcEncoder(RpcRequest.class));
                            ch.pipeline().addLast(new RpcDecoder(RpcResponse.class));
                            ch.pipeline().addLast(new RpcClientHandler());
                        }
                    });

            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            ChannelFuture channelFuture = bootstrap.connect(unresolvedAddress.getHost(), unresolvedAddress.getPort());
            channelGroup.put(unresolvedAddress.toString(), channelFuture);

        } catch (Throwable e) {
            logger.error("invoke rpc error!", e);
            worker.shutdownGracefully();
        }
    }

    /**
     * The type Rpc client handler.
     *
     * @author yefei
     * @date 2017 -6-26 15:45:08
     */
    class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
            processServerMessage(msg);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            super.exceptionCaught(ctx, cause);
            ctx.close();
        }
    }

    /**
     * Process remoting message.
     *
     * @param msg the msg
     * @throws Exception the exception
     */
    public void processServerMessage(RpcResponse msg) {
        try {
            message.get(msg.getRequestId()).setSuccess(msg);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            message.remove(msg.getRequestId());
        }
    }

    /**
     * Invoke.
     *
     * @param rpcRequest   the rpc request
     * @param invokeFuture the invoke future
     */
    public void invoke(RpcRequest rpcRequest, InvokeFuture<RpcResponse> invokeFuture) {
        message.put(rpcRequest.getRequestId(), invokeFuture);
    }

}
