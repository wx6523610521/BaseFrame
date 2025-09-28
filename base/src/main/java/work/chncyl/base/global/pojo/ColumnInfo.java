package work.chncyl.base.global.pojo;

import lombok.Builder;
import lombok.Data;
import work.chncyl.base.global.enums.ColumnType;

import java.util.List;

@Builder
@Data
public class ColumnInfo {
    /**
     * 列名
     */
    private String columnName;

    /**
     * 列类型
     */
    private ColumnType columnType;

    /**
     * 长度
     */
    private Integer length;

    /**
     * 精度
     */
    private Integer precision;

    /**
     * 为SET 或者 ENUM 的列的枚举值
     */
    private List<String> value;

    /**
     * 是否非空
     */
    private boolean notNull;

    /**
     * 是否无符号值
     */
    private boolean unsigned;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 是否主键
     */
    private boolean pk;

    /**
     * 是否自增
     */
    private boolean autoIncrement;

    /**
     * 列注释
     */
    private String comment;

}
