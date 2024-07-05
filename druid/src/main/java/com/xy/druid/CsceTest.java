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

public class CsceTest {
    static DruidDataSource datasource = new DruidDataSource();

    private static final Logger logger = LoggerFactory.getLogger(UpdateTest.class);

    static {
        datasource.setUrl("jdbc:mysql://110.42.205.63:31306/cscectc-cloud?useSSL=false&useUnicode=true&characterEncoding=gbk&useServerPrepStmts=true");
        //datasource.setUrl("jdbc:mysql://cdb-r8rnnc1s.cd.tencentcdb.com:10038/leaf-jobs?useSSL=false");
        datasource.setUsername("root");
        datasource.setPassword("DTapAAyowU");
        datasource.setDriverClassName("com.mysql.jdbc.Driver");
    }

    public static void main(String[] args) throws Exception {
        final DruidPooledConnection connection = datasource.getConnection(30000);

        String sql = "select id from t_chaos_user where id = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            long start = System.currentTimeMillis();
            preparedStatement.setLong(1, 1);
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();
            while (resultSet.next()) {
                System.out.println(resultSet.getString(1));
            }
            System.out.printf("耗时 %d\n", System.currentTimeMillis() - start);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
