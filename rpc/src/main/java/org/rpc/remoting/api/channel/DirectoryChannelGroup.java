package org.rpc.remoting.api.channel;

import org.rpc.remoting.api.Directory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DirectoryChannelGroup {

    // key: 服务标识; value: 提供服务的节点列表(group list)
    private final ConcurrentMap<String, CopyOnWriteArrayList<ChannelGroup>> groups = new ConcurrentHashMap<>();

    public CopyOnWriteArrayList<ChannelGroup> find(Directory directory) {
        String _directory = directory.directory();
        CopyOnWriteArrayList groupList = groups.get(_directory);
        if (groupList == null) {
            CopyOnWriteArrayList newGroupList = new CopyOnWriteArrayList();
            groupList = groups.putIfAbsent(_directory, newGroupList);
            if (groupList == null) {
                groupList = newGroupList;
            }
        }
        return groupList;
    }

}
