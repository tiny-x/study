package org.rpc.remoting.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.SystemPropertyUtil;
import org.rpc.exception.RemotingException;
import org.rpc.exception.RemotingTimeoutException;
import org.rpc.exception.RemotingTooMuchRequestException;
import org.rpc.remoting.Pair;
import org.rpc.remoting.api.ChannelEventListener;
import org.rpc.remoting.api.InvokeCallback;
import org.rpc.remoting.api.RequestProcessor;
import org.rpc.remoting.api.future.ResponseFuture;
import org.rpc.remoting.api.payload.ByteHolder;
import org.rpc.remoting.api.payload.RequestBytes;
import org.rpc.remoting.api.payload.ResponseBytes;
import org.rpc.remoting.api.procotol.ProtocolHead;
import org.rpc.remoting.netty.event.ChannelEvent;
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

        if (defaultProcessor != null && defaultProcessor.getA() != null && defaultProcessor.getB() != null) {
            defaultProcessor.getB().submit(() -> {
                if (defaultProcessor.getA().rejectRequest()) {
                    logger.warn("[REJECT] provider reject request!");
                    ResponseBytes responseBytes = new ResponseBytes(
                            ProtocolHead.RESPONSE,
                            cmd.getSerializerCode(),
                            null);
                    responseBytes.setInvokeId(cmd.getInvokeId());
                    responseBytes.setStatus(ProtocolHead.STATUS_SYSTEM_BUSY);
                    if (responseBytes != null) {
                        ctx.channel().writeAndFlush(responseBytes);
                    }
                    return;
                }
                ResponseBytes responseBytes = defaultProcessor.getA().process(ctx, cmd);
                if (responseBytes != null) {
                    ctx.channel().writeAndFlush(responseBytes);
                }
            });
        } else {
            logger.warn("requestProcessor is null!");
        }
    }

    protected ResponseBytes invokeSync0(Channel channel, RequestBytes request, long timeout, TimeUnit timeUnit) throws RemotingException, InterruptedException {
        ResponseFuture<ResponseBytes> responseFuture = new ResponseFuture<>();
        responseTable.putIfAbsent(request.getInvokeId(), responseFuture);
        ResponseBytes response = new ResponseBytes(request.getSerializerCode(), null);

        try {
            channel.writeAndFlush(request).addListener((ChannelFuture future) -> {
                if (!future.isSuccess()) {
                    ResponseBytes responseFail = new ResponseBytes(request.getSerializerCode(), null);

                    responseTable.remove(request.getInvokeId());
                    responseFail.setInvokeId(request.getInvokeId());
                    responseFail.setStatus(ProtocolHead.STATUS_ERROR);
                    responseFuture.complete(responseFail);
                    throw new RemotingException("", future.cause());
                }
            });
            response = responseFuture.get(timeout, timeUnit);
        } catch (ExecutionException e) {
            throw new RemotingException("ExecutionException", e);
        } catch (TimeoutException e) {
            response.setInvokeId(request.getInvokeId());
            response.setStatus(ProtocolHead.STATUS_TIMEOUT);
            throw new RemotingTimeoutException(channel.remoteAddress().toString(), timeUnit.convert(timeout, TimeUnit.MILLISECONDS));
        } finally {
            responseTable.remove(request.getInvokeId());
        }
        return response;
    }

    protected void invokeAsync0(Channel channel, RequestBytes request,
                                long timeout, TimeUnit timeUnit, InvokeCallback<ResponseBytes> invokeCallback) throws RemotingException, InterruptedException {

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
