package bg.sofia.uni.fmi.mjt.exceptions.api;

public class DataUnavailableException extends ApiExecutionException {
    public DataUnavailableException(String message) {
        super(message);
    }

    public DataUnavailableException(String message, Exception e) {
        super(message, e);
    }

}
