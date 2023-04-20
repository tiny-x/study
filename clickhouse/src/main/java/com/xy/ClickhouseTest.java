package com.xy;

import com.clickhouse.jdbc.ClickHouseConnection;
import com.clickhouse.jdbc.ClickHouseDataSource;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author xf.yefei
 */
public class ClickhouseTest {

    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        ClickHouseDataSource dataSource = new ClickHouseDataSource("jdbc:clickhouse://10.10.228.211:8123", properties);
        ClickHouseConnection connection = dataSource.getConnection();
        Statement stmt = connection.createStatement();

        while (true) {
            TimeUnit.SECONDS.sleep(2);
            long start = System.currentTimeMillis();
            ResultSet resultSet2 = stmt.executeQuery("SELECT * FROM test.t1 ");
            while (resultSet2.next()) {
                System.out.println("ck查询成功：" + resultSet2.getInt(1) + "," + resultSet2.getString(2));
            }
            System.out.printf("耗时 %d \n", System.currentTimeMillis() - start);
        }

    }
}
