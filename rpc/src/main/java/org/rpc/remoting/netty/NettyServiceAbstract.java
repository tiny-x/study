package org.rpc.remoting.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import org.rpc.exception.RemotingTimeoutException;
import org.rpc.exception.RemotingTooMuchRequestException;
import org.rpc.remoting.InvokeCallback;
import org.rpc.remoting.payload.ByteHolder;
import org.rpc.remoting.payload.RequestBytes;
import org.rpc.remoting.payload.ResponseBytes;
import org.rpc.remoting.procotol.ProtocolHead;
import org.rpc.remoting.Pair;
import org.rpc.remoting.RequestProcessor;
import org.rpc.remoting.future.ResponseFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.concurrent.*;


public abstract class NettyServiceAbstract {

    private static final Logger logger = LoggerFactory.getLogger(NettyServiceAbstract.class);

    protected final ConcurrentMap<Long, ResponseFuture<ResponseBytes>> responseTable =
            new ConcurrentHashMap(256);

    protected final HashMap<Integer/* request code */, Pair<RequestProcessor, ExecutorService>> processorTable =
            new HashMap(64);

    protected final Semaphore semaphoreAsync;

    protected final Semaphore semaphoreOneWay;

    protected final Pair<RequestProcessor, ExecutorService> defaultProcessor = new Pair(64);

    public NettyServiceAbstract(final int permitsAsync, final int permitsOneWay) {
        this.semaphoreAsync = new Semaphore(permitsAsync, true);
        this.semaphoreOneWay = new Semaphore(permitsOneWay, true);
    }

    public void processMessageReceived(ChannelHandlerContext ctx, ByteHolder msg) throws Exception {
        final ByteHolder cmd = msg;
        if (cmd != null) {
            if (msg instanceof RequestBytes) {
                processRequestCommand(ctx, (RequestBytes) msg);
            } else if (msg instanceof ResponseBytes) {
                processResponseCommand(ctx, (ResponseBytes) msg);
            }
        }
    }

    private void processResponseCommand(ChannelHandlerContext ctx, ResponseBytes cmd) throws Exception {
        long invokeId = cmd.getInvokeId();
        ResponseFuture<ResponseBytes> future = responseTable.get(invokeId);
        if (future != null) {
            future.complete(cmd);
            if (future.getInvokeCallback() != null) {
                responseTable.remove(invokeId);
                future.executeInvokeCallback();
            }
        } else {
            logger.warn("processResponseCommand invokeId: {}, ResponseFuture is null!", invokeId);
        }
    }

    private void processRequestCommand(ChannelHandlerContext ctx, RequestBytes cmd) {

        if (defaultProcessor != null && defaultProcessor.getA() != null && defaultProcessor.getB() != null) {
            defaultProcessor.getB().submit(() -> {
                ResponseBytes responseBytes = defaultProcessor.getA().process(ctx, cmd);
                if (responseBytes != null) {
                    ctx.channel().writeAndFlush(responseBytes);
                }
            });
        } else {
            logger.warn("RequestProcessor is null!");
        }
    }

    protected ResponseBytes invokeSync0(Channel channel, RequestBytes request, long timeout, TimeUnit timeUnit) throws Exception {
        ResponseFuture<ResponseBytes> responseFuture = new ResponseFuture<>();
        responseTable.putIfAbsent(request.getInvokeId(), responseFuture);
        ResponseBytes responseBytes;

        try {
            channel.writeAndFlush(request).addListener((ChannelFuture future) -> {
                if (!future.isSuccess()) {
                    responseTable.remove(request.getInvokeId());
                    ResponseBytes response = new ResponseBytes(request.getSerializerCode(), null);
                    response.setInvokeId(request.getInvokeId());
                    response.setStatus(ProtocolHead.STATUS_ERROR);
                    responseFuture.complete(response);
                    logger.error("invokeSync0 error!", future.cause());
                }
            });
            responseBytes = responseFuture.get(timeout, timeUnit);
        } catch (TimeoutException e) {
            responseBytes = new ResponseBytes(request.getSerializerCode(), null);
            responseBytes.setInvokeId(request.getInvokeId());
            responseBytes.setStatus(ProtocolHead.STATUS_TIMEOUT);

            logger.error("invokeSync0 timeout!", e);
        } finally {
            responseTable.remove(request.getInvokeId());
        }

        return responseBytes;
    }

    protected void invokeAsync0(Channel channel, RequestBytes request,
                                long timeout, TimeUnit timeUnit, InvokeCallback<ResponseBytes> invokeCallback) throws Exception {

        ResponseFuture<ResponseBytes> responseFuture = new ResponseFuture<>(invokeCallback);
        responseTable.putIfAbsent(request.getInvokeId(), responseFuture);

        if (semaphoreAsync.tryAcquire(timeout, timeUnit)) {

            channel.writeAndFlush(request).addListener((ChannelFuture future) -> {
                if (!future.isSuccess()) {
                    responseTable.remove(request.getInvokeId());
                    ResponseBytes response = new ResponseBytes(request.getSerializerCode(), null);
                    response.setInvokeId(request.getInvokeId());
                    response.setStatus(ProtocolHead.STATUS_ERROR);

                    responseFuture.complete(response);
                    responseFuture.executeInvokeCallback();

                    logger.error("invokeAsync0 error!", future.cause());
                }
            });
        } else {
            if (timeout <= 0) {
                throw new RemotingTooMuchRequestException("invokeAsyncImpl invoke too fast");
            } else {
                String info =
                        String.format("invokeAsyncImpl tryAcquire semaphore timeout, %dms, waiting thread nums: %d semaphoreAsyncValue: %d",
                                timeout,
                                this.semaphoreAsync.getQueueLength(),
                                this.semaphoreAsync.availablePermits()
                        );
                logger.warn(info);
                throw new RemotingTimeoutException(info);
            }
        }


    }

}
