package bg.sofia.uni.fmi.mjt.exceptions.app.user;

import bg.sofia.uni.fmi.mjt.exceptions.app.AppExecutionException;

public class IllegalPasswordException extends AppExecutionException {
    public IllegalPasswordException(String message) {
        super(message);
    }

    public IllegalPasswordException(String message, Exception e) {
        super(message, e);
    }

}
