package bg.sofia.uni.fmi.mjt.exceptions.api;

public class ApiExecutionException extends Exception {
    public ApiExecutionException(String message) {
        super(message);
    }

    public ApiExecutionException(String message, Exception e) {
        super(message, e);
    }

}
