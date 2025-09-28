package work.chncyl.service.user.enums;

import work.chncyl.base.global.enums.EnumMessage;

public enum EnumAccountStatus implements EnumMessage {
    DISABLE(0, "禁用"),
    ENABLE(1, "启用");

    final Integer code;
    final String msg;

    EnumAccountStatus(Integer code, String msg) {
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
