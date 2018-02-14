package org.rpc.remoting.api.channel;

import io.netty.channel.Channel;
import org.rpc.comm.UnresolvedAddress;

/**
 * 同一 点对点 channel
 *
 */
public interface ChannelGroup {

    UnresolvedAddress remoteAddress();

    Channel next();

    boolean addChannel(Channel channel);

    boolean removeChannel(Channel channel);

    void setWeight(int weight);

    int getWeight();

    boolean isAvailable();

    int size();
}
