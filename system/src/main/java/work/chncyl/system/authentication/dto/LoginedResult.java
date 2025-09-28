package work.chncyl.system.authentication.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class LoginedResult extends ClaimAuthticateResultModel{

    @ApiModelProperty(value = "头像")
    private String headImage;

    // 昵称
    @ApiModelProperty(value = "昵称")
    private String nickName;

    @ApiModelProperty(value = "手机号")
    private String phoneNum;

    @ApiModelProperty(value = "认证公司名称")
    private String companyName;

    @ApiModelProperty(value = "认证状态 0 未认证 1 认证通过 2 认证未通过")
    private Integer authenticationState;
}
