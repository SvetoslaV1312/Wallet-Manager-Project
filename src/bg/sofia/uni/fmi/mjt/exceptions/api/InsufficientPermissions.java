package bg.sofia.uni.fmi.mjt.exceptions.api;

public class InsufficientPermissions extends ApiExecutionException {
    public InsufficientPermissions(String message) {
        super(message);
    }

    public InsufficientPermissions(String message, Exception e) {
        super(message, e);
    }

}
