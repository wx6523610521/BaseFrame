package work.chncyl.system.dictionary.dto.input;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DictionaryAddInput {
    @ApiModelProperty(value = "字典类型")
    private String type;

    /**
     * 类型名称
     */
    @ApiModelProperty(value = "类型显示名称")
    private String typeName;

    /**
     * 码值
     */
    @ApiModelProperty(value = "码值")
    private String code;

    /**
     * 显示名称
     */
    @ApiModelProperty(value = "显示名称")
    private String name;

    /**
     * 上级id
     */
    @ApiModelProperty(value = "上级id")
    private Integer upId;
}
