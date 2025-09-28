package work.chncyl.base.global.enums;

import lombok.Getter;

@Getter
public enum ColumnType {
    TINYINT("TINYINT", 1),
    SMALLINT("SMALLINT", 1),
    MEDIUMINT("MEDIUMINT", 1),
    INT("INT", 1),
    BIGINT("BIGINT", 1),
    FLOAT("FLOAT", 0),
    DOUBLE("DOUBLE", 0),
    DECIMAL("DECIMAL", 2),
    CHAR("CHAR", 1),
    VARCHAR("VARCHAR", 1),
    TINYTEXT("TINYTEXT", 0),
    TEXT("TEXT", 0),
    MEDIUMTEXT("MEDIUMTEXT", 0),
    LONGTEXT("LONGTEXT", 0),
    BINARY("BINARY", 1),
    VARBINARY("VARBINARY", 1),
    DATE("DATE", 0),
    TIME("TIME", 0),
    DATETIME("DATETIME", 0),
    YEAR("YEAR", 0),
    TIMESTAMP("TIMESTAMP", 0),
    ENUM("ENUM", -1),
    SET("SET", -1),
    JSON("JSON", 0),
    BLOB("BLOB", 0),
    ;

    private final String type;

    private final Integer limit;

    ColumnType(String type, Integer limit) {
        this.type = type;
        this.limit = limit;
    }

    public boolean isNum(){
        switch (this) {
            case TINYINT:
            case SMALLINT:
            case MEDIUMINT:
            case INT:
            case BIGINT:
            case FLOAT:
            case DOUBLE:
            case DECIMAL:
                return true;
            default:
                return false;
        }
    }

    public boolean canUnsigned() {
        switch (this) {
            case TINYINT:
            case SMALLINT:
            case MEDIUMINT:
            case INT:
            case BIGINT:
            case FLOAT:
            case DOUBLE:
            case DECIMAL:
                return true;
            default:
                return false;
        }
    }

    public boolean canHaveDefault() {
        // AUTO_INCREMENT 也不能设置默认值
        switch (this) {
            case TEXT:
            case BLOB:
            case JSON:
                return false;
            default:
                return true;
        }
    }

    public boolean canDoPK() {
        switch (this) {
            case TINYINT:
            case SMALLINT:
            case MEDIUMINT:
            case INT:
            case BIGINT:
            case CHAR:
            case VARCHAR:
            case DATE:
            case DATETIME:
            case TIMESTAMP:
                return true;
            default:
                return false;
        }
    }

    public boolean canAutoIncr() {
        switch (this) {
            case TINYINT:
            case SMALLINT:
            case MEDIUMINT:
            case INT:
            case BIGINT:
                return true;
            default:
                return false;
        }
    }
}
