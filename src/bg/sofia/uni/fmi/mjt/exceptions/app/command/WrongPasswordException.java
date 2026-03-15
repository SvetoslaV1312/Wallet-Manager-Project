package bg.sofia.uni.fmi.mjt.exceptions.app.command;

import bg.sofia.uni.fmi.mjt.exceptions.app.AppExecutionException;

public class WrongPasswordException extends AppExecutionException {
    public WrongPasswordException(String message) {
        super(message);
    }

    public WrongPasswordException(String message, Exception e) {
        super(message, e);
    }

}
