package work.chncyl.base.global.syslog;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("sys_logs")
public class SysLog {
    @TableId(
            value = "id",
            type = IdType.AUTO
    )
    private Long id;
    /**
     * 操作时间
     */
    private Date createTime;
    /**
     * 操作人
     */
    private Integer userid;
    /**
     * 操作人
     */
    private String username;
    /**
     * 执行时间
     */
    private long costTime;

    /**
     * 执行类型
     */
    private String logType;

    /**
     * 日志记录
     */
    private String logContent;

    /**
     * 执行方法
     */
    private String method;

    /**
     * 请求参数
     */
    private String requestParam;

    /**
     * 请求ip
     */
    private String ip;
}
