package com.xy.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author yefei
 * @date 2018-05-17 10:02
 */
public class HikariExample {

    public static void main(String[] args) throws Exception {
        HikariConfig config = newHikariConfig();
        config.setMinimumIdle(1);
        config.setMaximumPoolSize(2);
        config.setConnectionTestQuery("SELECT 1");
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.setJdbcUrl("jdbc:mysql://mysql.dev.xianglin.com:3306/jobsdb?useUnicode=true&amp;characterEncoding=utf-8&amp;allowMultiQueries=true");
        config.addDataSourceProperty("user", "jobsuser");
        config.addDataSourceProperty("password", "cUfPNDtk");

        try (HikariDataSource ds = new HikariDataSource(config);
             Connection conn = ds.getConnection();
        ) {
            PreparedStatement preparedStatement = conn.prepareStatement("select 1");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int anInt = resultSet.getInt(1);
                System.out.println(anInt);
            }
        }
    }

    public static HikariConfig newHikariConfig() {
        final StackTraceElement callerStackTrace = Thread.currentThread().getStackTrace()[2];

        String poolName = callerStackTrace.getMethodName();
        if ("setup".equals(poolName)) {
            poolName = callerStackTrace.getClassName();
        }

        final HikariConfig config = new HikariConfig();
        config.setPoolName(poolName);
        return config;
    }
}
