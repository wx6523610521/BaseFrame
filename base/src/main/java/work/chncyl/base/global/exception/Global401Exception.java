package work.chncyl.base.global.exception;

public class Global401Exception extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public Global401Exception(String message) {
        super(message);
    }

    public Global401Exception(Throwable cause) {
        super(cause);
    }

    public Global401Exception(String message, Throwable cause) {
        super(message, cause);
    }
}
