package com.xy.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
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
        datasource.setUrl("jdbc:mysql://10.10.225.128:3306/chaosblade?useSSL=false&useUnicode=true&characterEncoding=gbk&useServerPrepStmts=true");
        //datasource.setUrl("jdbc:mysql://cdb-r8rnnc1s.cd.tencentcdb.com:10038/leaf-jobs?useSSL=false");
        datasource.setUsername("root");
        datasource.setPassword("123456");
        datasource.setDriverClassName("com.mysql.jdbc.Driver");
        Properties properties = new Properties();
        datasource.setConnectProperties(properties);
        datasource.setMaxActive(20);
    }

    public static void main(String[] args) throws Exception {
        new SelectTest().select();
    }

    public void select() throws SQLException {
        final DruidPooledConnection connection = datasource.getConnection(3000);

        String sql = "select id from t_chaos_user where id = ?";

        executor.scheduleWithFixedDelay(new Runnable() {


            @Override
            public void run() {
                Statement statement = null;
                try {
                    PreparedStatement  preparedStatement = connection.prepareStatement(sql);
                    long start = System.currentTimeMillis();
                    preparedStatement.setLong(1, 1);
                    preparedStatement.execute();
                    ResultSet resultSet = preparedStatement.getResultSet();
                    while (resultSet.next()) {
                        System.out.println(resultSet.getString(1));
                    }
                    System.out.printf("耗时 %d\n", System.currentTimeMillis() - start);

                    statement = connection.createStatement();
                    resultSet = statement.executeQuery("select id from t_chaos_user where id = 1");
                    while (resultSet.next()) {
                        System.out.println("statement: " + resultSet.getString(1));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        statement.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
        }, 100, 3000, TimeUnit.MILLISECONDS);
    }
}
