package bg.sofia.uni.fmi.mjt.exceptions.app.command;

import bg.sofia.uni.fmi.mjt.exceptions.app.AppExecutionException;

public class InvalidCommandArgumentCount extends AppExecutionException {
    public InvalidCommandArgumentCount(String message) {
        super(message);
    }

    public InvalidCommandArgumentCount(String message, Exception e) {
        super(message, e);
    }

}
