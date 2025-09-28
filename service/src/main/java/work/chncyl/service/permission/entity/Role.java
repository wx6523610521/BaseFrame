package work.chncyl.service.permission.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Data;

/**
 * 角色
 */
@ApiModel(description = "角色")
@Data
@TableName(value = "sys_role")
public class Role {
    @TableId(value = "Id", type = IdType.AUTO)
    @ApiModelProperty(value = "")
    private Integer id;

    /**
     * 角色名称
     */
    @TableField(value = "`Name`")
    @ApiModelProperty(value = "角色名称")
    private String name;

    /**
     * 角色标记
     */
    @TableField(value = "Mark")
    @ApiModelProperty(value = "角色标记")
    private String mark;

    /**
     * 是否默认角色
     */
    @TableField(value = "IsDefault")
    @ApiModelProperty(value = "是否默认角色")
    private Boolean isDefault;

    /**
     * 状态(0 禁用 1 正常）
     */
    @TableField(value = "`Status`")
    @ApiModelProperty(value = "状态(0 禁用 1 正常）")
    private Byte status;

    /**
     * 排序号
     */
    @TableField(value = "Sort")
    @ApiModelProperty(value = "排序号")
    private Integer sort;

    /**
     * 创建时间
     */
    @TableField(value = "CreateTime")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 创建人ID
     */
    @TableField(value = "Creator")
    @ApiModelProperty(value = "创建人ID")
    private Integer creator;

    /**
     * 最后修改时间
     */
    @TableField(value = "UpdateTime")
    @ApiModelProperty(value = "最后修改时间")
    private Date updateTime;

    /**
     * 最后修改人ID
     */
    @TableField(value = "UpdateUser")
    @ApiModelProperty(value = "最后修改人ID")
    private Integer updateUser;

    /**
     * 是否删除
     */
    @TableField(value = "IsDeleted")
    @ApiModelProperty(value = "是否删除")
    private Boolean isDeleted;
}