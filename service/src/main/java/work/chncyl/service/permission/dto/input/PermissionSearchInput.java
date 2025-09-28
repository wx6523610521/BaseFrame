package work.chncyl.service.permission.dto.input;

import work.chncyl.base.global.pojo.PagedInputPojo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PermissionSearchInput extends PagedInputPojo {
    @ApiModelProperty(value = "权限名称")
    private String displayName;

    /**
     * 权限标记
     */
    @ApiModelProperty(value = "权限标记")
    private String permissionName;

    @ApiModelProperty(value = "roleId")
    private Integer roleId;

    @ApiModelProperty(value = "状态(0 禁用 1 正常）")
    private Byte status;
}
