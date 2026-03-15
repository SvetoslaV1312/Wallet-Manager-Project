package bg.sofia.uni.fmi.mjt.exceptions.api;

public class UnauthorizedKeyException extends ApiExecutionException {
    public UnauthorizedKeyException(String message) {
        super(message);
    }

    public UnauthorizedKeyException(String message, Exception e) {
        super(message, e);
    }

}
