package com.xy.zookeeper.cuator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.concurrent.TimeUnit;

/**
 * 当一台机器宕机之后，可重新进行选举
 */
public class MasterSelect {

    public static void main(String[] args) throws Exception {

        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("127.0.0.1:2181")
                .connectionTimeoutMs(3000)
                .sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        client.start();

        String masterPath = "/master_slect";
        if (client.checkExists().forPath(masterPath) == null) {
            client.create().withMode(CreateMode.EPHEMERAL).forPath(masterPath);
        }

        LeaderSelector leaderSelector = new LeaderSelector(client, masterPath, new LeaderSelectorListener() {
            @Override
            public void takeLeadership(CuratorFramework curatorFramework) throws Exception {
                System.out.println("成功master");
                TimeUnit.SECONDS.sleep(2);
                System.out.println("执行完释放master角色, 重新开始选举。");
            }

            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {
                System.out.println("stateChanged" + newState.name());
            }
        });
        // leaderSelector.autoRequeue();
        leaderSelector.start();
        System.in.read();
    }
}

