package org.rpc.remoting.api.channel;

import io.netty.channel.Channel;
import org.rpc.comm.UnresolvedAddress;
import org.rpc.remoting.api.Directory;

/**
 * 同一 点对点 channel
 *
 */
public interface ChannelGroup {

    UnresolvedAddress remoteAddress();

    Channel next();

    boolean addChannel(Channel channel);

    boolean removeChannel(Channel channel);

    void setWeight(Directory directory, int weight);

    int getWeight(Directory directory);

    void removeWeight(Directory directory);

    boolean isAvailable();

    int size();
}
