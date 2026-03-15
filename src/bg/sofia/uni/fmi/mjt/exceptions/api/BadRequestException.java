package bg.sofia.uni.fmi.mjt.exceptions.api;

public class BadRequestException extends ApiExecutionException {
    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Exception e) {
        super(message, e);
    }

}
