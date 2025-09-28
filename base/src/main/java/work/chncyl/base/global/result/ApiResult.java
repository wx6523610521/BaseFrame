package work.chncyl.base.global.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Getter
@Setter
public class ApiResult<T> implements Serializable {
    /**
     * 成功标志
     */
    @ApiModelProperty("成功标志")
    private boolean status;

    /**
     * 返回处理消息
     */
    @ApiModelProperty("返回处理消息")
    private String message;

    /**
     * 返回代码
     */
    @ApiModelProperty("返回代码")
    private Integer code;

    /**
     * 返回数据对象 data
     */
    @ApiModelProperty("返回数据对象")
    private T data;

    /**
     * 时间戳
     */
    @ApiModelProperty("时间戳")
    private long timestamp;

    /**
     * 额外信息
     */
    @ApiModelProperty("额外信息")
    private String detail;

    public ApiResult(boolean status, Integer code, T data) {
        this(status, (status ? "success" : "error"), code, data);
    }

    public ApiResult(boolean status, Integer code, String message) {
        this(status, message, code, null);
    }

    public ApiResult(boolean status, String message, Integer code, T data) {
        this(status, message, code, data, System.currentTimeMillis());
    }

    public ApiResult(boolean status, String message, Integer code, T data, long timestamp) {
        this(status, message, code, data, timestamp, null);
    }

    public ApiResult(boolean status, String message, Integer code, T data, long timestamp, String detail) {
        this.status = status;
        this.message = message;
        this.code = code;
        this.data = data;
        this.timestamp = timestamp;
        this.detail = detail;
    }

    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(true, HttpStatus.OK.value(), data);
    }

    public static <T> ApiResult<T> error(Integer code, String message) {
        return new ApiResult<>(false, code, message);
    }

    public static <T> ApiResult<T> error500(String message) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
    }

    public static <T> ApiResult<T> error405(String message) {
        return error(HttpStatus.METHOD_NOT_ALLOWED.value(), message);
    }

    public static <T> ApiResult<T> error404(String message) {
        return error(HttpStatus.NOT_FOUND.value(), message);
    }

    public static <T> ApiResult<T> error401(String message) {
        return error(HttpStatus.UNAUTHORIZED.value(), message);
    }

    public static <T> ApiResult<T> error406(String message) {
        return error(HttpStatus.NOT_ACCEPTABLE.value(), message);
    }

    /**
     * 弱密码响应
     */
    public static <T> ApiResult<T> error412(String detail) {
        ApiResult<T> error = error(HttpStatus.PRECONDITION_FAILED.value(), "密码复杂度不符合要求");
        error.setDetail(detail);
        return error;
    }

}
