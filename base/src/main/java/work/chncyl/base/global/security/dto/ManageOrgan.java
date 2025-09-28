package work.chncyl.base.global.security.dto;

import lombok.Data;

@Data
public class ManageOrgan {
    private String roleId;

    private int roleLevel;

    private String roleName;

    private String organId;

    private String organName;

    private Boolean isSchool;
}
