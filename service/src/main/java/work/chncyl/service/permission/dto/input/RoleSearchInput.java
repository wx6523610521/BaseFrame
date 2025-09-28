package work.chncyl.service.permission.dto.input;

import work.chncyl.base.global.pojo.PagedInputPojo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RoleSearchInput extends PagedInputPojo {
    @ApiModelProperty(value = "角色名称")
    private String name;

    @ApiModelProperty(value = "角色标记")
    private String mark;

    @ApiModelProperty(value = "是否默认角色")
    private Boolean isDefault;

    @ApiModelProperty(value = "状态(0 禁用 1 正常）")
    private Byte status;
}
