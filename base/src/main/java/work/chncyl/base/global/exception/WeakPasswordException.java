package work.chncyl.base.global.exception;

public class WeakPasswordException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public WeakPasswordException(String message) {
        super(message);
    }

    public WeakPasswordException(Throwable cause) {
        super(cause);
    }

    public WeakPasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}
