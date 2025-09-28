package work.chncyl.service.user.enums;

import work.chncyl.base.global.enums.EnumMessage;

public enum EnumAccountType implements EnumMessage {
    INDIVIDUAL_USER(0, "个人用户"),

    ENTERPRISE_USER(1, "企业用户");

    final Integer code;
    final String msg;

    EnumAccountType(Integer code, String msg) {
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
