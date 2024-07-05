package com.xy.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author yefei
 * @create 2020-05-28 17:08
 */
public class ConnectionTest {

    static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);

    static DruidDataSource datasource = new DruidDataSource();

    private static final Logger logger = LoggerFactory.getLogger(UpdateTest.class);

    static {
        datasource.setUrl("jdbc:mysql://10.10.220.46:3306/chaosblade?useSSL=false&useUnicode=true&characterEncoding=gbk&useServerPrepStmts=true");
        //datasource.setUrl("jdbc:mysql://cdb-r8rnnc1s.cd.tencentcdb.com:10038/leaf-jobs?useSSL=false");
        datasource.setUsername("root");
        datasource.setPassword("123456");
        datasource.setMaxActive(50);
        datasource.setDriverClassName("com.mysql.jdbc.Driver");
    }

    public static void main(String[] args) throws Exception {
        //System.out.println(ConnectionTest.class.getName());
        new ConnectionTest().select();
    }

    public void select() throws SQLException {

        String sql = "select id from t_chaos_user where id = ?";


        executor.scheduleWithFixedDelay(new Runnable() {

            @Override
            public void run() {
                PreparedStatement preparedStatement = null;
                DruidPooledConnection connection = null;
                try {
                    connection = datasource.getConnection(3000);
                    preparedStatement = connection.prepareStatement(
                            sql
                    );

                    preparedStatement.setLong(1, 1);
                    long l = System.currentTimeMillis();
                    preparedStatement.execute();
                    ResultSet resultSet = preparedStatement.getResultSet();

                    while (resultSet.next()) {
                        System.out.println(resultSet.getString(1) + " " + (System.currentTimeMillis() - l));
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        try {
                            connection.close();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }, 100, 3000, TimeUnit.MILLISECONDS);
    }
}
