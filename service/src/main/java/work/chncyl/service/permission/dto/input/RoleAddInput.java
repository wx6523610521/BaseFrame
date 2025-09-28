package work.chncyl.service.permission.dto.input;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class RoleAddInput {
    @ApiModelProperty(value = "角色名称")
    private String name;

    /**
     * 角色标记
     */
    @ApiModelProperty(value = "角色标记", required = true)
    private String mark;

    /**
     * 是否默认角色
     */
    @ApiModelProperty(value = "是否默认角色")
    private Boolean isDefault;

    /**
     * 状态(0 禁用 1 正常）
     */
    @ApiModelProperty(value = "状态(0 禁用 1 正常）")
    private Byte status;

    /**
     * 排序号
     */
    @ApiModelProperty(value = "排序号")
    private Integer sort;

    private List<Integer> permissions;
}
