package work.chncyl.base.global.security.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RoleInfo {
    @ApiModelProperty(value = "")
    private Integer id;

    /**
     * 角色名称
     */
    @ApiModelProperty(value = "角色名称")
    private String name;

    /**
     * 角色标记
     */
    @ApiModelProperty(value = "角色标记")
    private String mark;
}
