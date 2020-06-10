package com.xy.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class SelectTest {

    static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);

    static DruidDataSource datasource = new DruidDataSource();

    private static final Logger logger = LoggerFactory.getLogger(UpdateTest.class);

    static {
        datasource.setUrl("jdbc:mysql://cdb-r8rnnc1s.cd.tencentcdb.com:10038/leaf-jobs?useSSL=false&useUnicode=true&characterEncoding=gbk");
        //datasource.setUrl("jdbc:mysql://cdb-r8rnnc1s.cd.tencentcdb.com:10038/leaf-jobs?useSSL=false");
        datasource.setUsername("yefei");
        datasource.setPassword("yefei123456");
        datasource.setDriverClassName("com.mysql.jdbc.Driver");
        Properties properties = new Properties();
        datasource.setConnectProperties(properties);
    }

    public static void main(String[] args) throws Exception {
        new SelectTest().select();
    }

    public void select() throws SQLException {
        executor.scheduleWithFixedDelay(new Runnable() {

            @Override
            public void run() {
                DruidPooledConnection connection = null;
                PreparedStatement preparedStatement = null;

                try {

                    connection = datasource.getConnection(3000);

                    String sql = "select USER_NAME from t_user where USER_NAME = ?;";

                    preparedStatement = connection.prepareStatement(
                            sql
                    );
                    preparedStatement.setString(1, "admin");
                    preparedStatement.execute();
                    ResultSet resultSet = preparedStatement.getResultSet();
                    while (resultSet.next()) {
                        System.out.println(resultSet.getString(1));
                    }
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
