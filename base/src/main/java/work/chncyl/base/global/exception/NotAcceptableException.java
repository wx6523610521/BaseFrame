package work.chncyl.base.global.exception;

/**
 * 不可接受异常(无法根据客户端请求的内容特性完成请求
 */
public class NotAcceptableException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NotAcceptableException(String message) {
        super(message);
    }

    public NotAcceptableException(Throwable cause) {
        super(cause);
    }

    public NotAcceptableException(String message, Throwable cause) {
        super(message, cause);
    }
}
