package bg.sofia.uni.fmi.mjt.exceptions.app.encryption;

public class CacheFileEncryptionException extends RuntimeException {
    public CacheFileEncryptionException(String message) {
        super(message);
    }

    public CacheFileEncryptionException(String message, Exception e) {
        super(message, e);
    }

}
