package work.chncyl.base.global.pojo;

import lombok.Data;

import java.util.List;

@Data
public class TableInfo {
    /**
     * 表名
     */
    private String tableName;

    /**
     * 表注释
     */
    private String comment;

    /**
     * 索引信息
     */
    private List<List<ColumnInfo>> index;

    /**
     * 字段信息
     */
    private List<ColumnInfo> columns;
}
