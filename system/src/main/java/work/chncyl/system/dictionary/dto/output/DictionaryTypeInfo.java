package work.chncyl.system.dictionary.dto.output;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DictionaryTypeInfo {
    @ApiModelProperty(value = "字典类型")
    private String type;

    @ApiModelProperty(value = "类型名称")
    private String typeName;
}
