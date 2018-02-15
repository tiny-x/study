package org.rpc.rpc.load.balancer;

import org.rpc.remoting.api.Directory;
import org.rpc.remoting.api.channel.ChannelGroup;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 软负载均衡
 */
public interface LoadBalancer {

    ChannelGroup select(CopyOnWriteArrayList<ChannelGroup> list, Directory directory);
}
