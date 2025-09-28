package work.chncyl.service.permission.dto.input;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PermissionAddInput {

    @ApiModelProperty(value = "权限名称")
    private String displayName;

    /**
     * 权限标记
     */
    @ApiModelProperty(value = "权限标记", required = true)
    private String permissionName;

    /**
     * 权限路径
     */
    @ApiModelProperty(value = "权限路径")
    private String url;

    /**
     * 权限级别
     */
    @ApiModelProperty(value = "权限级别")
    private Byte level;

    /**
     * 排序
     */
    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty(value = "权限类型")
    private String type;
}
