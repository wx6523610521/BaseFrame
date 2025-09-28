package work.chncyl.base.global.enums;

public enum LogType implements work.chncyl.base.global.enums.EnumMessage {
    LOGIN(1, "登录"),
    OPERATION(2, "操作"),
    SELECT(3, "查询操作"),
    ADD(4, "新增操作"),
    UPDATE(5, "更新操作"),
    DELETE(6, "删除操作"),
    IMPORT(7, "导入操作"),
    OUTPUT(8, "导出操作"),
    ;

    final int value;
    final String description;

    LogType(int value, String description) {
        this.value = value;
        this.description = description;
    }

    @Override
    public Integer getValue() {
        return 0;
    }

    @Override
    public String getMessage() {
        return "";
    }
}
