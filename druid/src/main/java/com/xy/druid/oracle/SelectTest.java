package com.xy.druid.oracle;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.xy.druid.UpdateTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
        datasource.setUrl("jdbc:oracle:thin:@10.10.225.128:1521:helowin");
        //datasource.setUrl("jdbc:mysql://cdb-r8rnnc1s.cd.tencentcdb.com:10038/leaf-jobs?useSSL=false");
        datasource.setUsername("test");
        datasource.setPassword("123456");
        datasource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        Properties properties = new Properties();
        datasource.setConnectProperties(properties);
    }

    public static void main(String[] args) throws Exception {
        new SelectTest().select();
    }

    public void select() throws SQLException {
        final DruidPooledConnection connection = datasource.getConnection(3000);

        String sql = "select * from \"TX_TAB\" where id = ?";
        final PreparedStatement preparedStatement = connection.prepareStatement(
                sql
        );

        executor.scheduleWithFixedDelay(() -> {
            try {
                preparedStatement.setLong(1, 1);
                long start = System.currentTimeMillis();
                preparedStatement.executeQuery();
                System.out.println("耗时: " + (System.currentTimeMillis() - start) + "ms  ");
                ResultSet resultSet = preparedStatement.getResultSet();
                while (resultSet.next()) {
                   // System.out.println(resultSet.getString(2));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 100, 3000, TimeUnit.MILLISECONDS);
    }
}
