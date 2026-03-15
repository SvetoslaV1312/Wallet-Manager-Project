package bg.sofia.uni.fmi.mjt.exceptions.app.user;

import bg.sofia.uni.fmi.mjt.exceptions.app.AppExecutionException;

public class UserAlreadyExistsInDB extends AppExecutionException {
    public UserAlreadyExistsInDB(String message) {
        super(message);
    }

    public UserAlreadyExistsInDB(String message, Exception e) {
        super(message, e);
    }

}
