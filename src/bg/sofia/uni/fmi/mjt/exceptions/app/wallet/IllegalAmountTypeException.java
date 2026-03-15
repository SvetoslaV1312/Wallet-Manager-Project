package bg.sofia.uni.fmi.mjt.exceptions.app.wallet;

import bg.sofia.uni.fmi.mjt.exceptions.app.AppExecutionException;

public class IllegalAmountTypeException extends AppExecutionException {
    public IllegalAmountTypeException(String message) {
        super(message);
    }

    public IllegalAmountTypeException(String message, Exception e) {
        super(message, e);
    }

}
