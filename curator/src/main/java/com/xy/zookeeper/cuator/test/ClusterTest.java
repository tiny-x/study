package com.xy.zookeeper.cuator.test;

import org.apache.curator.test.TestingCluster;
import org.apache.curator.test.TestingZooKeeperServer;

import java.util.concurrent.TimeUnit;

public class ClusterTest {

    public static void main(String[] args) throws Exception {
        TestingCluster cluster = new TestingCluster(3);
        cluster.start();
        TimeUnit.SECONDS.sleep(2);

        TestingZooKeeperServer leader = null;
        for (TestingZooKeeperServer zs : cluster.getServers()) {
            System.out.println(zs.getInstanceSpec().getServerId());
            System.out.println(zs.getQuorumPeer().getServerState());
            String absolutePath = zs.getInstanceSpec().getDataDirectory().getAbsolutePath();
            System.out.println(absolutePath);

            if (zs.getQuorumPeer().getServerState().equals("leading")) {
                leader = zs;
            }
        }
        leader.kill();
        System.out.println("----------leader kill");


        for (TestingZooKeeperServer zs : cluster.getServers()) {
            System.out.println(zs.getInstanceSpec().getServerId());
            System.out.println(zs.getQuorumPeer().getServerState());
            String absolutePath = zs.getInstanceSpec().getDataDirectory().getAbsolutePath();
            System.out.println(absolutePath);
        }
    }
}
