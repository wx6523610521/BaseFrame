package work.chncyl.system.dictionary.dto.input;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DictionaryTreeInput {
    @ApiModelProperty(value = "字典类型")
    private String type;

}
