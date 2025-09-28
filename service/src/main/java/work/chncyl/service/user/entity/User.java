package work.chncyl.service.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Data;

/**
 * 用户表
 */
@ApiModel(description="用户表")
@Data
@TableName(value = "sys_user")
public class User {
    @TableId(value = "Id", type = IdType.AUTO)
    @ApiModelProperty(value="")
    private Integer id;

    /**
     * 用户名
     */
    @TableField(value = "UserName")
    @ApiModelProperty(value="用户名")
    private String userName;

    /**
     * 性别
     */
    @TableField(value = "Sex")
    @ApiModelProperty(value="性别")
    private String sex;

    /**
     * 手机号
     */
    @TableField(value = "PhoneNum")
    @ApiModelProperty(value="手机号")
    private String phoneNum;

    /**
     * 电子邮箱
     */
    @TableField(value = "Email")
    @ApiModelProperty(value="电子邮箱")
    private String email;

    /**
     * 公司名称
     */
    @TableField(value = "CompanyName")
    @ApiModelProperty(value="公司名称")
    private String companyName;

    /**
     * 创建时间
     */
    @TableField(value = "CreateTime")
    @ApiModelProperty(value="创建时间")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "UpdateTime")
    @ApiModelProperty(value="更新时间")
    private Date updateTime;

    @TableField(value = "UpdateUser")
    @ApiModelProperty(value="")
    private Integer updateUser;

    /**
     * 是否删除
     */
    @TableField(value = "IsDeleted")
    @ApiModelProperty(value="是否删除")
    private Boolean isDeleted;
}