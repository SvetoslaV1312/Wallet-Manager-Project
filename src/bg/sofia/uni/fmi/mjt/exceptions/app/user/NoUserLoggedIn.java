package bg.sofia.uni.fmi.mjt.exceptions.app.user;

import bg.sofia.uni.fmi.mjt.exceptions.app.AppExecutionException;

public class NoUserLoggedIn extends AppExecutionException {
    public NoUserLoggedIn(String message) {
        super(message);
    }

    public NoUserLoggedIn(String message, Exception e) {
        super(message, e);
    }

}
