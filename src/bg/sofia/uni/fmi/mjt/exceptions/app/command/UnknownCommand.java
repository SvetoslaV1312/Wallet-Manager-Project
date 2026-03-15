package bg.sofia.uni.fmi.mjt.exceptions.app.command;

import bg.sofia.uni.fmi.mjt.exceptions.app.AppExecutionException;

public class UnknownCommand extends AppExecutionException {
    public UnknownCommand(String message) {
        super(message);
    }

    public UnknownCommand(String message, Exception e) {
        super(message, e);
    }

}
