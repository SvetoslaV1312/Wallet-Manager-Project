package bg.sofia.uni.fmi.mjt.exceptions.app.wallet;

import bg.sofia.uni.fmi.mjt.exceptions.app.AppExecutionException;

public class NegativeMoneyException extends AppExecutionException {
    public NegativeMoneyException(String message) {
        super(message);
    }

    public NegativeMoneyException(String message, Exception e) {
        super(message, e);
    }

}
