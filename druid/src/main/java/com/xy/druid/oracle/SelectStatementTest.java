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
public class SelectStatementTest {

    static ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);

    static DruidDataSource datasource = new DruidDataSource();

    private static final Logger logger = LoggerFactory.getLogger(UpdateTest.class);

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
        new SelectStatementTest().select();
    }

    public void select() throws SQLException {
        final DruidPooledConnection connection = datasource.getConnection(3000);

        String sql = "select * from \"user\" where id = 1";
        final Statement statement = connection.createStatement(

        );

        executor.scheduleWithFixedDelay(() -> {
            try {
                long start = System.currentTimeMillis();
                ResultSet resultSet = statement.executeQuery(sql);
                System.out.print("耗时: " + (System.currentTimeMillis() - start) + "ms  ");
                while (resultSet.next()) {
                    System.out.println(resultSet.getString(2));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 100, 3000, TimeUnit.MILLISECONDS);
    }
}
