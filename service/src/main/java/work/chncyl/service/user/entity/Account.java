package work.chncyl.service.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Data;

/**
 * 账户表
 */
@ApiModel(description = "账户表")
@Data
@TableName(value = "sys_account")
public class Account {
    @TableId(value = "Id", type = IdType.AUTO)
    @ApiModelProperty(value="")
    private Integer id;

    /**
     * 登录名
     */
    @TableField(value = "AccountName")
    @ApiModelProperty(value="登录名")
    private String accountName;

    /**
     * 加密密码
     */
    @TableField(value = "AccountPwd")
    @ApiModelProperty(value="加密密码")
    private String accountPwd;

    /**
     * 关联用户Id
     */
    @TableField(value = "UserId")
    @ApiModelProperty(value="关联用户Id")
    private Integer userId;

    @TableField(value = "AccountType")
    @ApiModelProperty(value="类型")
    private Byte accountType;

    /**
     * 账号状态 0 禁用 1 正常
     */
    @TableField(value = "AccountStatus")
    @ApiModelProperty(value="账号状态 0 禁用 1 正常 ")
    private Byte accountStatus;

    /**
     * 最后登录时间
     */
    @TableField(value = "LastLoginTime")
    @ApiModelProperty(value="最后登录时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastLoginTime;

    /**
     * 头像
     */
    @TableField(value = "HeadImage")
    @ApiModelProperty(value="头像")
    private String headImage;

    /**
     * 昵称
     */
    @TableField(value = "NickName")
    @ApiModelProperty(value="昵称")
    private String nickName;

    @TableField(value = "WechatOpenId")
    @ApiModelProperty(value="")
    private String wechatOpenId;

    /**
     * 是否删除
     */
    @TableField(value = "IsDeleted")
    @ApiModelProperty(value="是否删除")
    private Boolean isDeleted;
}