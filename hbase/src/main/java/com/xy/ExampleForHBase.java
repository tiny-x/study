package com.xy;

import org.apache.hadoop.hbase.client.Connection;

import java.io.IOException;

public class ExampleForHBase {

    static HbaseConfig hbaseConfig;
    static Connection connection;

    static HbaseHelper hbaseHelper;

    public static void init() {
        hbaseHelper = new HbaseHelper();
        hbaseConfig = new HbaseConfig();
        String tableName = "test";
        try {
            connection = hbaseConfig.hbaseConnection();
            hbaseHelper.createTable(connection, tableName, new String[]{"f1", "f2"});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        init();
        Connection connection = hbaseConfig.hbaseConnection();
        String tableName = "test";
        hbaseHelper.getRows(connection, tableName, "row-1");
    }
}
