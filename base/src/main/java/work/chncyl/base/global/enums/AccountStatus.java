package work.chncyl.base.global.enums;

public enum AccountStatus implements work.chncyl.base.global.enums.EnumMessage {
    DISABLE(0, "禁用"),
    ENABLE(1, "启用");

    final Integer code;
    final String msg;

    AccountStatus(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public Integer getValue() {
        return code;
    }

    @Override
    public String getMessage() {
        return msg;
    }
}
