import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

import java.io.StringWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseUtil {

    public static String parseAndReplaceTableNames(final String sql, final String dbTypeName) throws SQLException {
        //DbType dbType = DbType.of(dbTypeName);
        // new MySQL Parser
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbTypeName);
        if (parser == null) {
            throw new SQLException("dbType not support" + " dbType" + dbTypeName + " sql" + sql);
        }

        Map<String, String> mappingTable = new HashMap<String, String>(4);
        // 使用Parser解析生成AST，这里SQLStatement就是AST
        final StringWriter val = new StringWriter();
        try {
            final List<SQLStatement> sqlStatements = parser.parseStatementList();

            for (final SQLStatement sqlStatement : sqlStatements) {
                SQLASTOutputVisitor visitor = SQLUtils.createOutputVisitor(val, DbType.mysql);
                SchemaStatVisitor visitor2 = SQLUtils.createSchemaStatVisitor(DbType.mysql);
                sqlStatement.accept(visitor2);

                final Map<TableStat.Name, TableStat> originTableName = visitor2.getTables();
                final Map<String, String> additionalTableNames = new HashMap<String, String>();
                for (final TableStat.Name name : originTableName.keySet()) {
                    /**
                     * 过滤掉函数
                     */
                    String tableName = name.getName();
                    if (StrUtil.indexOf(tableName, '(') != -1 && StrUtil.indexOf(tableName, ')') != -1) {
                        continue;
                    }
                    String nameTemp = name.getName();
                    String schema = "";
                    if (nameTemp != null && StrUtil.contains(nameTemp, ".")) {
                        schema = StrUtil.subBefore(nameTemp, ".", false);
                        nameTemp = StrUtil.subBefore(nameTemp, ".", false);
                    }

                    if (StrUtil.indexOf(schema, '\"') != -1) {
                        schema = StrUtil.replace(schema, "\"", "");
                    }
                    if (StrUtil.indexOf(nameTemp, '\"') != -1) {
                        nameTemp = StrUtil.replace(nameTemp, "\"", "");
                    }

                    String shadowTableName = "shadowTableName";
                    if (StrUtil.isNotBlank(schema)) {
                        additionalTableNames.put(schema + "." + nameTemp, schema + "." + shadowTableName);
                    } else {
                        additionalTableNames.put(nameTemp, shadowTableName);
                    }
                }

                if (additionalTableNames.size() > 0) {
                    mappingTable.putAll(additionalTableNames);
                }

                /**
                 * 设置sql table别名
                 */
                visitor.setTableMapping(mappingTable);
                sqlStatement.accept(visitor);
            }
        } catch (Throwable e) {
            //log.error("[Shadow Table] handle shadow table error ,sql:{} ,dbType:{}", sql, dbTypeName, e);
            throw e;
        }
        return val.toString();
    }
}
