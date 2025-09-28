package work.chncyl.system.authentication.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("带验证码实体")
public class VerificationLoginModel {
    private String codeId;
    @ApiModelProperty("验证码")
    private String code;
}
