package com.xy.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author yefei
 * @create 2020-05-28 17:08
 */
public class UpdateTest {

    static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);

    static DruidDataSource datasource = new DruidDataSource();

    private static final Logger logger = LoggerFactory.getLogger(UpdateTest.class);

    static {
        datasource.setUrl("jdbc:mysql://cdb-r8rnnc1s.cd.tencentcdb.com:10038/leaf-jobs?useSSL=false");
        datasource.setUsername("yefei");
        datasource.setPassword("yefei123456");
        datasource.setDriverClassName("com.mysql.jdbc.Driver");
        Properties properties = new Properties();
        datasource.setConnectProperties(properties);
    }

    public static void main(String[] args) throws Exception {
        new UpdateTest().select();
    }

    public void select() {
        executor.scheduleWithFixedDelay(new Runnable() {

            @Override
            public void run() {
                DruidPooledConnection connection = null;
                PreparedStatement preparedStatement = null;

                try {

                    connection = datasource.getConnection(3000);

                    String sql = "update t_user t set t.MOBILE_PHONE = ?, CREATE_DATE = ?, CREATOR = ? \n" +
                            "where USER_NAME = ? AND USER_ID >= ? AND USER_ID <= ?";

                    preparedStatement = connection.prepareStatement(
                            sql
                    );
                    preparedStatement.setString(1, "110");
                    preparedStatement.setDate(2, new Date(System.currentTimeMillis()));
                    preparedStatement.setString(3, "0");
                    preparedStatement.setString(4, "矮人狙击手");
                    preparedStatement.setLong(5, 1591176677495L);
                    preparedStatement.setLong(6, 1591177048704L);
                    preparedStatement.execute();
                    ResultSet resultSet = preparedStatement.getResultSet();
                    int updateCount = preparedStatement.getUpdateCount();
                    System.out.println("effect line: " + updateCount);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        preparedStatement.close();
                        connection.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        }, 100, 3000, TimeUnit.MILLISECONDS);
    }
}
