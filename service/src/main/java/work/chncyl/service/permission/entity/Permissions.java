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
 * 权限
 */
@ApiModel(description = "权限")
@Data
@TableName(value = "sys_permissions")
public class Permissions {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "")
    private Integer id;

    /**
     * 权限名称
     */
    @TableField(value = "DisplayName")
    @ApiModelProperty(value = "权限名称")
    private String displayName;

    /**
     * 权限标记
     */
    @TableField(value = "PermissionName")
    @ApiModelProperty(value = "权限标记")
    private String permissionName;

    /**
     * 权限路径
     */
    @TableField(value = "url")
    @ApiModelProperty(value = "权限路径")
    private String url;

    /**
     * 权限级别
     */
    @TableField(value = "`Level`")
    @ApiModelProperty(value = "权限级别")
    private Byte level;

    /**
     * 排序
     */
    @TableField(value = "Sort")
    @ApiModelProperty(value = "排序")
    private Integer sort;

    /**
     * 权限类型
     */
    @TableField(value = "`Type`")
    @ApiModelProperty(value = "权限类型")
    private String type;

    /**
     * 状态(0 禁用 1 正常）
     */
    @TableField(value = "`Status`")
    @ApiModelProperty(value = "状态(0 禁用 1 正常）")
    private Byte status;

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
     * 修改人id
     */
    @TableField(value = "UpdateUser")
    @ApiModelProperty(value = "修改人id")
    private Integer updateUser;

    /**
     * 是否删除
     */
    @TableField(value = "IsDeleted")
    @ApiModelProperty(value = "是否删除")
    private Boolean isDeleted;
}