package bg.sofia.uni.fmi.mjt.exceptions.api;

public class ApiKeyLimitExceededException extends ApiExecutionException {
    public ApiKeyLimitExceededException(String message) {
        super(message);
    }

    public ApiKeyLimitExceededException(String message, Exception e) {
        super(message, e);
    }

}
