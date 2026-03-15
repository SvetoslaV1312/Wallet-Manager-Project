package bg.sofia.uni.fmi.mjt.exceptions.app.user;

import bg.sofia.uni.fmi.mjt.exceptions.app.AppExecutionException;

public class UserHasNotBeenRegistered extends AppExecutionException {
    public UserHasNotBeenRegistered(String message) {
        super(message);
    }

    public UserHasNotBeenRegistered(String message, Exception e) {
        super(message, e);
    }

}
