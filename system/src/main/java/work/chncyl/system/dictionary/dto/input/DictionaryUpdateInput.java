package work.chncyl.system.dictionary.dto.input;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DictionaryUpdateInput {
    @ApiModelProperty()
    private Integer id;

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
