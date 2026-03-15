package bg.sofia.uni.fmi.mjt.exceptions.api;

public class CryptoNotFoundException extends ApiExecutionException {
    public CryptoNotFoundException(String message) {
        super(message);
    }

    public CryptoNotFoundException(String message, Exception e) {
        super(message, e);
    }

}
