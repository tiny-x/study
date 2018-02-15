package org.rpc.remoting.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.rpc.comm.UnresolvedAddress;
import org.rpc.remoting.api.channel.ChannelGroup;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class NettyChannelGroup implements ChannelGroup {

    private final CopyOnWriteArrayList<Channel> channels = new CopyOnWriteArrayList<>();

    private AtomicInteger index = new AtomicInteger(0);

    private final UnresolvedAddress address;

    private volatile int weight = 50;

    public NettyChannelGroup(UnresolvedAddress address) {
        this.address = address;
    }

    // 连接断开时自动被移除
    private final ChannelFutureListener remover = new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            removeChannel(future.channel());
        }
    };

    @Override
    public UnresolvedAddress remoteAddress() {
        return address;
    }

    @Override
    public Channel next() {
        for (; ; ) {
            int length = channels.size();
            if (length == 0) {
                throw new IllegalStateException("no channel");
            }
            if (length == 1) {
                return channels.get(0);
            }
            int offset = Math.abs(index.incrementAndGet() % length);
            return channels.get(offset);
        }
    }

    @Override
    public boolean addChannel(Channel channel) {
        boolean added = channels.add(channel);
        if (added) {
            channel.closeFuture().addListener(remover);
        }
        return added;
    }

    @Override
    public boolean removeChannel(Channel channel) {
        return channels.remove(channel);
    }

    @Override
    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public int getWeight() {
        return weight;
    }

    @Override
    public boolean isAvailable() {
        return !channels.isEmpty();
    }

    @Override
    public int size() {
        return channels.size();
    }
}
