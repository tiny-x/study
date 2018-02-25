package org.rpc.remoting.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import org.rpc.exception.RemotingException;
import org.rpc.exception.RemotingSendRequestException;
import org.rpc.exception.RemotingTimeoutException;
import org.rpc.exception.RemotingTooMuchRequestException;
import org.rpc.remoting.Pair;
import org.rpc.remoting.api.ChannelEventListener;
import org.rpc.remoting.api.InvokeCallback;
import org.rpc.remoting.api.RequestProcessor;
import org.rpc.remoting.api.ResponseStatus;
import org.rpc.remoting.api.future.ResponseFuture;
import org.rpc.remoting.api.payload.ByteHolder;
import org.rpc.remoting.api.payload.RequestBytes;
import org.rpc.remoting.api.payload.ResponseBytes;
import org.rpc.remoting.netty.event.ChannelEvent;
import org.rpc.serializer.Serializer;
import org.rpc.serializer.SerializerFactory;
import org.rpc.serializer.SerializerType;
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

    protected final Pair<RequestProcessor, ExecutorService> defaultProcessor = new Pair();

    protected final ChannelEventExecutor channelEventExecutor = new ChannelEventExecutor();

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
            logger.warn("processResponseCommand invokeId: {}, responseFuture is null!", invokeId);
        }
    }

    private void processRequestCommand(ChannelHandlerContext ctx, RequestBytes cmd) {
        SerializerType serializerType = SerializerType.parse(cmd.getSerializerCode());
        Serializer serializer = SerializerFactory.serializer(serializerType);

        if (defaultProcessor.getA() != null && defaultProcessor.getB() != null) {
            try {
                defaultProcessor.getB().submit(() -> {
                    if (defaultProcessor.getA().rejectRequest()) {
                        String message = "[REJECT_REQUEST] system busy, start flow control for a while";

                        ResponseBytes responseBytes = new ResponseBytes(
                                cmd.getSerializerCode(),
                                serializer.serialize(message));
                        responseBytes.setInvokeId(cmd.getInvokeId());
                        responseBytes.setStatus(ResponseStatus.FLOW_CONTROL.value());
                        ctx.channel().writeAndFlush(responseBytes);
                    } else {
                        ResponseBytes responseBytes = defaultProcessor.getA().process(ctx, cmd);
                        if (responseBytes != null) {
                            ctx.channel().writeAndFlush(responseBytes);
                        }
                    }
                });
            } catch (RejectedExecutionException e) {

                String message = "[OVERLOAD]system busy, start flow control for a while";

                ResponseBytes responseBytes = new ResponseBytes(
                        cmd.getSerializerCode(),
                        serializer.serialize(message));
                responseBytes.setInvokeId(cmd.getInvokeId());
                responseBytes.setStatus(ResponseStatus.SYSTEM_BUSY.value());
                ctx.channel().writeAndFlush(responseBytes);
            }
        } else {
            String message = "[ERROR]system error, request process not register";

            ResponseBytes responseBytes = new ResponseBytes(
                    cmd.getSerializerCode(),
                    serializer.serialize(message));
            responseBytes.setInvokeId(cmd.getInvokeId());
            responseBytes.setStatus(ResponseStatus.SERVER_ERROR.value());
            ctx.channel().writeAndFlush(responseBytes);

            logger.error(ctx.channel() + message);
        }
    }

    protected ResponseBytes invokeSync0(Channel channel, RequestBytes request, long timeout, TimeUnit timeUnit)
            throws RemotingException, InterruptedException {
        ResponseFuture<ResponseBytes> responseFuture = new ResponseFuture<>();
        responseTable.putIfAbsent(request.getInvokeId(), responseFuture);
        try {
            channel.writeAndFlush(request).addListener((ChannelFuture future) -> {
                responseFuture.setSuccess(future.isSuccess());
                if (!future.isSuccess()) {
                    responseTable.remove(request.getInvokeId());
                    responseFuture.complete(null);
                    responseFuture.failure(future.cause());
                    logger.warn("send a request command to channel <" + channel + "> failed.");
                }
            });
            ResponseBytes response = responseFuture.get(timeout, timeUnit);
            if (response == null) {
                if (responseFuture.isSuccess()) {
                    response.setInvokeId(request.getInvokeId());
                    response.setStatus(ResponseStatus.SERVER_TIME_OUT.value());
                    throw new RemotingTimeoutException(channel.remoteAddress().toString(),
                            timeUnit.convert(timeout, TimeUnit.MILLISECONDS));

                } else {
                    throw new RemotingSendRequestException("send request failed", responseFuture.cause());
                }
            }
            return response;
        } finally {
            responseTable.remove(request.getInvokeId());
        }
    }

    protected void invokeAsync0(Channel channel, RequestBytes request,
                                long timeout, TimeUnit timeUnit, InvokeCallback<ResponseBytes> invokeCallback)
            throws RemotingException, InterruptedException {

        ResponseFuture<ResponseBytes> responseFuture = new ResponseFuture<>(invokeCallback);
        responseTable.putIfAbsent(request.getInvokeId(), responseFuture);

        if (semaphoreAsync.tryAcquire(timeout, timeUnit)) {

            channel.writeAndFlush(request).addListener((ChannelFuture future) -> {
                responseFuture.setSuccess(future.isSuccess());
                if (!future.isSuccess()) {
                    responseTable.remove(request.getInvokeId());
                    responseFuture.complete(null);
                    responseFuture.executeInvokeCallback();
                    responseFuture.failure(future.cause());
                    logger.warn("send a request command to channel <" + channel + "> failed.");
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

    class ChannelEventExecutor implements Runnable {

        private final LinkedBlockingQueue<ChannelEvent> eventQueue = new LinkedBlockingQueue<>();
        private final int maxSize = 10000;

        public void putChannelEvent(final ChannelEvent event) {
            if (this.eventQueue.size() <= maxSize) {
                this.eventQueue.add(event);
            } else {
                logger.warn("event queue size[{}] enough, so drop this event {}", this.eventQueue.size(), event.toString());
            }
        }

        @Override
        public void run() {
            final ChannelEventListener listener = NettyServiceAbstract.this.getChannelEventListener();

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    ChannelEvent event = this.eventQueue.poll(3000, TimeUnit.MILLISECONDS);
                    if (event != null && listener != null) {
                        switch (event.getType()) {
                            case IDLE:
                                listener.onChannelIdle(event.getRemoteAddr(), event.getChannel());
                                break;
                            case CLOSE:
                                listener.onChannelClose(event.getRemoteAddr(), event.getChannel());
                                break;
                            case CONNECT:
                                listener.onChannelConnect(event.getRemoteAddr(), event.getChannel());
                                break;
                            case ACTIVE:
                                listener.onChannelActive(event.getRemoteAddr(), event.getChannel());
                                break;
                            case INACTIVE:
                                listener.onChannelInActive(event.getRemoteAddr(), event.getChannel());
                                break;
                            case EXCEPTION:
                                listener.onChannelException(event.getRemoteAddr(), event.getChannel());
                                break;
                            default:
                                break;
                        }
                    }
                } catch (Exception e) {
                    // ignore
                }
            }
        }

    }

    protected abstract ChannelEventListener getChannelEventListener();

}
