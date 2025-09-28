package work.chncyl.base.global.security.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class JwtClaimDto {
    @ApiModelProperty(value = "用户ID")
    private Integer userId;
    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty("当前激活的角色")
    private String roleId;
}