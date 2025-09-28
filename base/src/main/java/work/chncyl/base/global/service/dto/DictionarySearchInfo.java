package work.chncyl.base.global.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DictionarySearchInfo {
    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "字典类型")
    private String type;

    @ApiModelProperty(value = "码值")
    private String code;

    @ApiModelProperty(value = "显示名称")
    private String name;

    @ApiModelProperty(value = "上级id")
    private Integer upId;

}
