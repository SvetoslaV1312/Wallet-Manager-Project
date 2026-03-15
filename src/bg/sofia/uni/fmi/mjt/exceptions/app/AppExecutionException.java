package bg.sofia.uni.fmi.mjt.exceptions.app;

public class AppExecutionException extends Exception {
    public AppExecutionException(String message) {
        super(message);
    }

    public AppExecutionException(String message, Exception e) {
        super(message, e);
    }

}
