package work.chncyl.base.global.security.dto;

import lombok.Data;

import java.util.List;

@Data
public class PermissionDto {
    private Integer roleId;

    private List<String> permission;
}
