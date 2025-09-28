package work.chncyl.base.global.utils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import work.chncyl.base.global.pojo.ColumnInfo;
import work.chncyl.base.global.pojo.TableInfo;

import java.sql.*;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * mysql数据库工具类
 */
@Component
@Slf4j
@Data
public class MysqlDbUtils {
    @Value("${spring.datasource.dynamic.datasource.mysql.url}")
    private String DBURL;
    @Value("${spring.datasource.dynamic.datasource.mysql.username}")
    private String DBUser;
    @Value("${spring.datasource.dynamic.datasource.mysql.password}")
    private String DBPASSWORD;

    public boolean tableExists(String tableName) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // 加载数据库驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            // 建立连接
            connection = DriverManager.getConnection(DBURL, DBUser, DBPASSWORD);
            String sql = "SHOW TABLES LIKE ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, tableName);
            // 执行查询
            resultSet = preparedStatement.executeQuery();
            // 判断结果
            return resultSet.next(); // 如果有结果，则表存在
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            // 释放资源
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void createTable(TableInfo tableInfo) {
        List<ColumnInfo> columns = tableInfo.getColumns();
        String tableName = tableInfo.getTableName();
        if (StringUtils.isBlank(tableName) || columns == null || columns.isEmpty()) {
            throw new RuntimeException("参数缺失");
        }
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            // 加载数据库驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            // 建立连接
            connection = DriverManager.getConnection(DBURL, DBUser, DBPASSWORD);
            String sql;
            StringBuilder sb = new StringBuilder("CREATE TABLE ");
            sb.append(tableName).append("(");
            for (ColumnInfo column : columns) {
                sb.append(column.getColumnName())
                        .append(" ")
                        .append(column.getColumnType().getType())
                        .append(" ");
                if (column.getColumnType().getLimit() != 0 && column.getLength() != null) {
                    sb.append("(");
                    String limit = "";
                    if (column.getColumnType().getLimit() == 1) {
                        limit = column.getLength() + "";
                    } else if (column.getColumnType().getLimit() == 2) {
                        if (column.getPrecision() == null) {
                            column.setPrecision(0);
                        }
                        limit = column.getLength() + "," + column.getPrecision();
                    } else if (column.getColumnType().getLimit() == -1) {
                        StringJoiner joiner = new StringJoiner(",");
                        for (String s : column.getValue()) {
                            joiner.add("'" + s + "'");
                        }
                        limit = joiner.toString();
                    }
                    sb.append(limit).append(") ");
                }
                sb.append(column.isNotNull() ? " NOT NULL " : "")
                        .append(column.isUnsigned() && column.getColumnType().canUnsigned() ? " UNSIGNED " : "")
                        .append(StringUtils.isNotBlank(column.getDefaultValue()) ? " DEFAULT " + (column.getColumnType().isNum() ? column.getDefaultValue() : "'" + column.getDefaultValue() + "'") + " " : "")
                        .append(column.isAutoIncrement() && column.getColumnType().canAutoIncr() ? " AUTO_INCREMENT " : "")
                        .append(column.isPk() && column.getColumnType().canDoPK() ? " PRIMARY KEY " : "")
                        .append(StringUtils.isNotBlank(column.getComment()) ? " COMMENT '" + column.getComment() + "'" : "")
                        .append(",");
            }
            List<List<ColumnInfo>> indexColumn = tableInfo.getIndex();
            if (!indexColumn.isEmpty()) {
                for (List<ColumnInfo> columnInfo : indexColumn) {
                    if (columnInfo.isEmpty()) {
                        continue;
                    }
                    StringJoiner cl = new StringJoiner(",");
                    List<String> collect = columnInfo.stream().map(ColumnInfo::getColumnName).collect(Collectors.toList());
                    for (String s : collect) {
                        cl.add(s);
                    }
                    sb.append("INDEX ")
                            .append("idx_")
                            .append(UUID.randomUUID().toString().replaceAll("-", ""), 9, 16)
                            .append("(")
                            .append(cl)
                            .append("),")
                    ;
                }
            }
            sql = sb.substring(0, sb.length() - 1);
            sql += ");";
            log.info("创建sql：{}", sql);
            preparedStatement = connection.prepareStatement(sql);
            // 执行查询
            preparedStatement.executeUpdate();
            // 判断结果
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            // 释放资源
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
