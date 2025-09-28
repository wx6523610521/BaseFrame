package work.chncyl.system.authentication.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RegisterInput extends VerificationLoginModel{
    /**
     * 登录名
     */
    @ApiModelProperty(value = "登录名")
    private String accountName;

    /**
     * 加密密码
     */
    @ApiModelProperty(value = "加密密码")
    private String accountPwd;

    @ApiModelProperty(value = "获取公钥时使用的mark")
    private String mark;

    /**
     * 账户用户 0 个人用户 1 企业认证用户
     */
    @ApiModelProperty(value = "账户用户 0 个人用户 1 企业认证用户")
    private Byte accountType;

    /**
     * 头像
     */
    @ApiModelProperty(value = "头像")
    private String headimage;

    /**
     * 昵称
     */
    @ApiModelProperty(value = "昵称")
    private String nickname;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户姓名")
    private String userName;

    /**
     * 性别
     */
    @ApiModelProperty(value = "性别")
    private String sex;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号")
    private String phoneNum;

    /**
     * 电子邮箱
     */
    @ApiModelProperty(value = "电子邮箱")
    private String email;

    /**
     * 公司名称
     */
    @ApiModelProperty(value = "公司名称")
    private String companyName;

}
