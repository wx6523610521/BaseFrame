package work.chncyl.base.global.security.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import work.chncyl.base.global.security.utils.TokenUtil;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
public class LoginedUserInfo extends JwtClaimDto {
    // 用户类型
    @ApiModelProperty(value = "用户类型 0 个人用户 1 企业用户")
    private Integer userType;
    // 角色
    @ApiModelProperty(value = "全部角色,多个角色用,分隔")
    private String roleIds;

    @ApiModelProperty(value = "密码")
    private String accountPwd;

    @ApiModelProperty(value = "头像")
    private String headImage;
    // 昵称
    @ApiModelProperty(value = "昵称")
    private String nickName;
    // 手机号
    @ApiModelProperty(value = "手机号")
    private String phoneNum;
    //最后登录时间
    @ApiModelProperty(value = "最后登录时间")
    private Date lastLoginTime;

    private Integer isChecked;

    @ApiModelProperty(value = "账号状态 0 禁用 1 正常 ")
    private Integer accountStatus;

    private List<RoleInfo> userRoles;

    private List<String> permissions;

    private List<ManageOrgan> manageOrgans;

    public String getRoleId() {
        String roleId = TokenUtil.getValueByKey("roleId");
        if (roleId == null) {
            roleId = super.getRoleId();
        }
        return roleId;
    }
}