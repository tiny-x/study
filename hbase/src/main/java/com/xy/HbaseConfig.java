package com.xy;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.IOException;

public class HbaseConfig {

    private static final String SUPER_USER = "hbase";

    String zkHHost = "hbase";

    private static final String ZK_ZNODE = "/hbase";
    Connection connection;

    public org.apache.hadoop.conf.Configuration configuration() {
        org.apache.hadoop.conf.Configuration configuration = HBaseConfiguration.create();

        configuration.set("hbase.zookeeper.quorum", "10.10.228.213");
        configuration.set("hbase.zookeeper.property.clientPort", "2181");
        configuration.set("zookeeper.znode.parent", ZK_ZNODE);
        return configuration;
    }


    // 指定用户
    public org.apache.hadoop.conf.Configuration setUser(org.apache.hadoop.conf.Configuration conf, String user) {
        UserGroupInformation.setConfiguration(conf);
        UserGroupInformation romoteUser = UserGroupInformation.createRemoteUser(user);
        UserGroupInformation.setLoginUser(romoteUser);
        return conf;
    }

    public Connection hbaseConnection() throws IOException {
        if (connection == null || connection.isClosed() || connection.isAborted()) {
            org.apache.hadoop.conf.Configuration configuration = setUser(configuration(), SUPER_USER);
            connection = ConnectionFactory.createConnection(configuration);
        }
        return connection;
    }

}
