package com.xy.jdbc;

import java.sql.*;

/**
 * @author yefei
 * @date 2018-05-17 9:54
 */
public class JdbcExample {

    public static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://mysql.dev.xianglin.com:3306/jobsdb?useUnicode=true&amp;characterEncoding=utf-8&amp;allowMultiQueries=true", "jobsuser", "cUfPNDtk");
            PreparedStatement preparedStatement = connection.prepareStatement("select 1");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int anInt = resultSet.getInt(1);
                System.out.println(anInt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
