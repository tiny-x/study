package com.xy;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author xf.yefei
 */
@Slf4j
public class OceanbaseTest {

    static JdbcTemplate jdbcTemplate;

    static {
        Map<String, String> map = new HashMap<String, String>();
        map.put("url", "jdbc:oceanbase://10.10.228.15:2883/test");
        map.put("driverClassName", "com.oceanbase.jdbc.Driver");
        map.put("username", "root");
        map.put("password", "Kz**6|e4%:.0Av?t");
        try {
            Class.forName(map.get("driverClassName"));
            jdbcTemplate = new JdbcTemplate(DruidDataSourceFactory.createDataSource(map));
            //防止异常语句,没有这两句，会出错
            //jdbcTemplate.execute("set transaction_isolation = 'READ-COMMITTED';");
            //jdbcTemplate.execute("set tx_isolation = 'READ-COMMITTED';");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        String sql = "select * from user where id =?;";
        String sql2 = "insert into user(id, name) values(?, ?) ";
        while (true) {
            TimeUnit.SECONDS.sleep(2);
            long start = System.currentTimeMillis();
            jdbcTemplate.query(sql2, new Object[]{(int) (Math.random() * 1000000), 1}, new RowCallbackHandler() {
                @Override
                public void processRow(ResultSet resultSet) throws SQLException {
                    log.info("name: {}", resultSet.getString("name"));
                }
            });
            log.info("耗时 {}", System.currentTimeMillis() - start);
        }
    }

    public static void createByOrcTypeDate() {
        String sql = "create table D_DPRECORD(DEV_ID VARCHAR2(50)," +
                "CAR_SPEED NUMBER(3)," +
                "CAP_DATE TIMESTAMP WITH LOCAL TIME ZONE," +
                "DEV_CHNID VARCHAR2(50) not null," +
                "TRSFMARK NUMBER(1) default 0," +
                "CREATE_TIME DATE default sysdate" +
                ")";
        jdbcTemplate.execute(sql);
    }
}
