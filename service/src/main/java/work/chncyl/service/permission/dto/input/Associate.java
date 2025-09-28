package work.chncyl.service.permission.dto.input;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@ApiModel(value = "权限关联")
@AllArgsConstructor
@NoArgsConstructor
public class Associate {
    @ApiModelProperty(value = "主体ID", required = true)
    private Integer principalId;
    @ApiModelProperty(value = "关联ID", required = true)
    private List<Integer> relevanceId;
}
