package bg.sofia.uni.fmi.mjt.exceptions.app.command;

import bg.sofia.uni.fmi.mjt.exceptions.app.AppExecutionException;

public class InvalidCommandFormat extends AppExecutionException {
    public InvalidCommandFormat(String message) {
        super(message);
    }

    public InvalidCommandFormat(String message, Exception e) {
        super(message, e);
    }

}
