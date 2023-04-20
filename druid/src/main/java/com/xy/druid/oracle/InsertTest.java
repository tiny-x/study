package com.xy.druid.oracle;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
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
        datasource.setUrl("jdbc:oracle:thin:@10.10.222.108:1521:helowin");
        //datasource.setUrl("jdbc:mysql://cdb-r8rnnc1s.cd.tencentcdb.com:10038/leaf-jobs?useSSL=false");
        datasource.setUsername("test");
        datasource.setPassword("test");
        datasource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
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

                    String sql = "INSERT INTO \"user\" (ID, NAME)\n" +
                            "VALUES (?, ?)";

                    preparedStatement = connection.prepareStatement(
                            sql
                    );

                    preparedStatement.setLong(1, 1);
                    preparedStatement.setString(2, "110");
                    int i = preparedStatement.executeUpdate();

                    System.out.println("effect line: " + i);
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
