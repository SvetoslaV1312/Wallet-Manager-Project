package bg.sofia.uni.fmi.mjt.exceptions.app.repository;

public class DataAcessException extends Exception {
    public DataAcessException(String message) {
        super(message);
    }

    public DataAcessException(String message, Exception e) {
        super(message, e);
    }

}
