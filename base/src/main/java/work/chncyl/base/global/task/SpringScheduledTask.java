package work.chncyl.base.global.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
public class SpringScheduledTask implements Serializable {
    private static final long serialVersionUID = 1L;

    /**id*/
    private String id;
    
    /**任务名称*/
    private String taskName;
    
    /**cron表达式*/
    private String cronExpression;
    
    /**任务类名*/
    private String taskClass;
    
    /**任务方法名*/
    private String taskMethod;
    
    /**任务参数（JSON格式）*/
    private String taskParameter;
    
    /**状态 0正常 -1停止*/
    private Integer status;
    
    /**描述*/
    private String description;
    
    /**创建人*/
    private String createBy;
    
    /**创建时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    
    /**修改人*/
    private String updateBy;
    
    /**修改时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    
    /**删除状态*/
    private Integer delFlag;
}