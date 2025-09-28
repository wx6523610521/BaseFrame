package work.chncyl.system.dictionary.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "sys_dictionary")
@Data
@TableName(value = "sys_dictionary")
public class Dictionary {
    @TableId(value = "Id", type = IdType.AUTO)
    @ApiModelProperty(value = "")
    private Integer id;

    /**
     * 字典类型
     */
    @TableField(value = "`Type`")
    @ApiModelProperty(value = "字典类型")
    private String type;

    /**
     * 类型名称
     */
    @TableField(value = "TypeName")
    @ApiModelProperty(value = "类型名称")
    private String typeName;

    /**
     * 码值
     */
    @TableField(value = "Code")
    @ApiModelProperty(value = "码值")
    private String code;

    /**
     * 显示名称
     */
    @TableField(value = "`Name`")
    @ApiModelProperty(value = "显示名称")
    private String name;

    /**
     * 上级id
     */
    @TableField(value = "UpId")
    @ApiModelProperty(value = "上级id")
    private Integer upId;

    /**
     * 级别
     */
    @TableField(value = "`Level`")
    @ApiModelProperty(value = "级别")
    private Byte level;
}