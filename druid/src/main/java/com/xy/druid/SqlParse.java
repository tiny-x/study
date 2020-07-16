package com.xy.druid;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;

/**
 * @author yefei
 * @create 2020-06-19 17:19
 */
public class SqlParse {

    public static void main(String[] args) {
        String sql = "INSERT INTO t_user (USER_ID, MOBILE_PHONE, USER_NAME, EMAIL, PASSWORD, CREATOR, UPDATER, CREATE_DATE,\n" +
                "                                UPDATE_DATE, COMMENTS)\n" +
                "VALUES (?, ?, ?, '185120555@qq.com', '670b14728ad9902aecba32e22fa4f6bd', ?, null,\n" +
                "        ?, ?, null)" +
                ", (?, ?, ?, '185120555@qq.com', '670b14728ad9902aecba32e22fa4f6bd', ?, null,\n" +
                "        ?, ?, null)";
        String updateSql = "Update t_user set USER_ID = x, NAME = ? WHERE (USER_ID = y AND NAME = ?)";
        String selectSql = "SELECT * FROM t_user WHERE USER_ID = x AND NAME = ? OR  (USER_ID = y AND A = ?)";

        SQLStatement sqlStatement = SQLUtils.parseSingleMysqlStatement(updateSql);
        for (SQLObject child : sqlStatement.getChildren()) {
            System.out.println(child.getAttributes());
        }

    }
}
