package com.xy.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author yefei
 * @create 2020-05-28 17:08
 */
public class UpdateMultithreadingTest {

    static DruidDataSource datasource = new DruidDataSource();


    static {
        datasource.setUrl("jdbc:mysql://cdb-r8rnnc1s.cd.tencentcdb.com:10038/leaf-jobs?useSSL=false");
        datasource.setUsername("yefei");
        datasource.setPassword("yefei123456");
        datasource.setDriverClassName("com.mysql.jdbc.Driver");
        Properties properties = new Properties();
        datasource.setConnectProperties(properties);
    }

    public static void main(String[] args) throws Exception {
        new UpdateMultithreadingTest().select();
    }

    public void select() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        List<User> users = users();
        while (true) {
            Thread.sleep(10);
            executorService.execute(() -> {
                DruidPooledConnection connection = null;
                PreparedStatement preparedStatement = null;

                String sql = "update t_user set MOBILE_PHONE = ?, CREATE_DATE = ?, CREATOR = ? \n" +
                        "where USER_NAME = ? AND USER_ID = ? AND MOBILE_PHONE = '110'";
                try {
                    Random random = new Random();
                    User user = users.get(random.nextInt(users.size()));
                    connection = datasource.getConnection();
                    preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.setString(1, user.phone);
                    preparedStatement.setDate(2, new Date(user.createDate.getTime()));
                    preparedStatement.setString(3, random.nextInt(1000) + "");
                    preparedStatement.setString(4, user.userMame);
                    preparedStatement.setLong(5, user.id);
                    preparedStatement.execute();
                    int updateCount = preparedStatement.getUpdateCount();
                    System.out.println(Thread.currentThread().getName() + "\t effect line: " + updateCount);
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
            });
        }
    }

    public List<User> users() {
        List<User> users = new ArrayList<>();
        Random random = new Random();
        for (int i = 1; i <= 8; i++) {
            User user = new User();
            user.phone = "110";
            user.createDate = new java.util.Date();
            user.creator = random.nextInt(100) + "";
            user.userMame = "矮人狙击手";
            user.id = Long.valueOf(i);
            users.add(user);
        }
        return users;
    }

    static class User {
        public Long id;
        public String phone;
        public java.util.Date createDate;
        public String userMame;
        public String creator;
    }
}
