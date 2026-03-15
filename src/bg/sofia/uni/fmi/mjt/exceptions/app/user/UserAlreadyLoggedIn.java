package bg.sofia.uni.fmi.mjt.exceptions.app.user;

import bg.sofia.uni.fmi.mjt.exceptions.app.AppExecutionException;

public class UserAlreadyLoggedIn extends AppExecutionException {
    public UserAlreadyLoggedIn(String message) {
        super(message);
    }

    public UserAlreadyLoggedIn(String message, Exception e) {
        super(message, e);
    }

}
