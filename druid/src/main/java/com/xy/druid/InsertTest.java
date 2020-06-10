package com.xy.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Properties;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author yefei
 * @create 2020-05-28 17:08
 */
public class InsertTest {

    static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);

    static DruidDataSource datasource = new DruidDataSource();

    private static final Logger logger = LoggerFactory.getLogger(InsertTest.class);

    static {
        //datasource.setUrl("jdbc:mysql://cdb-r8rnnc1s.cd.tencentcdb.com:10038/leaf-jobs?useSSL=false");
        datasource.setUrl("jdbc:mysql://cdb-r8rnnc1s.cd.tencentcdb.com:10038/leaf-jobs?useSSL=false&useUnicode=true&characterEncoding=gbk");
        datasource.setUsername("yefei");
        datasource.setPassword("yefei123456");
        datasource.setDriverClassName("com.mysql.jdbc.Driver");
        Properties properties = new Properties();
        datasource.setConnectProperties(properties);
    }

    public static void main(String[] args) throws Exception {
        new InsertTest().select();
    }

    public void select() throws SQLException {
        executor.scheduleWithFixedDelay(new Runnable() {

            @Override
            public void run() {
                DruidPooledConnection connection = null;
                PreparedStatement preparedStatement = null;

                try {

                    connection = datasource.getConnection(3000);

                    String sql = "INSERT INTO t_user (USER_ID, MOBILE_PHONE, USER_NAME, EMAIL, PASSWORD, CREATOR, UPDATER, CREATE_DATE,\n" +
                            "                                UPDATE_DATE, COMMENTS)\n" +
                            "VALUES (?, ?, ?, '185120555@qq.com', '670b14728ad9902aecba32e22fa4f6bd', ?, null,\n" +
                            "        ?, ?, null);";

                    preparedStatement = connection.prepareStatement(
                            sql
                    );

                    preparedStatement.setLong(1, System.currentTimeMillis());
                    preparedStatement.setString(2, "110");
                    preparedStatement.setString(3, "矮人狙击手");
                    preparedStatement.setLong(4, 100);
                    preparedStatement.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
                    preparedStatement.setString(6, "2020-03-30 16:45:10");
                    preparedStatement.executeUpdate();
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
