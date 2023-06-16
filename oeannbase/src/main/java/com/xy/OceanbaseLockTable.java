package com.xy;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author xf.yefei
 */
@Slf4j
public class OceanbaseLockTable {
   static DataSource dataSource;

    static {
        Map<String, String> map = new HashMap<String, String>();
        map.put("url", "jdbc:oceanbase://10.10.228.15:2883/test");
        map.put("driverClassName", "com.oceanbase.jdbc.Driver");
        map.put("username", "root");
        map.put("password", "Kz**6|e4%:.0Av?t");
        try {
            Class.forName(map.get("driverClassName"));
            dataSource = DruidDataSourceFactory.createDataSource(map);
            //防止异常语句,没有这两句，会出错
            //jdbcTemplate.execute("set transaction_isolation = 'READ-COMMITTED';");
            //jdbcTemplate.execute("set tx_isolation = 'READ-COMMITTED';");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        String sql = "lock tables user read";
        TimeUnit.SECONDS.sleep(2);
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.execute();
        // lock
        System.in.read();
        connection.commit();
    }

}
