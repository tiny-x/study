package com.xy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

public class PostgreSqlJdbcConnAddDatas {

    public static void main(String args[]) throws Exception {
        Connection c = null;
        PreparedStatement stmt = null;

        Class.forName("org.postgresql.Driver");
        c = DriverManager.getConnection(
                "jdbc:postgresql://10.10.225.128:5432/postgres", "postgres",
                "123456");
        c.setAutoCommit(false);

        System.out.println("连接数据库成功！");
        stmt = c.prepareStatement("UPDATE t_user set NAME = ? WHERE ID = ?;");

        while (true) {
            try {
                TimeUnit.SECONDS.sleep(3);
                stmt.setString(1, "xx");
                stmt.setInt(2, 100);
                long l = System.currentTimeMillis();
                stmt.execute();
                System.out.printf("耗时 %s %s \n", System.currentTimeMillis() - l, "ms");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

}
