package bg.sofia.uni.fmi.mjt.exceptions.app.user;

import bg.sofia.uni.fmi.mjt.exceptions.app.AppExecutionException;

public class IllegalNameException extends AppExecutionException {
    public IllegalNameException(String message) {
        super(message);
    }

    public IllegalNameException(String message, Exception e) {
        super(message, e);
    }
}
