package bg.sofia.uni.fmi.mjt.exceptions.app.wallet;

import bg.sofia.uni.fmi.mjt.exceptions.app.AppExecutionException;

public class WalletBalanceExceeded extends AppExecutionException {
    public WalletBalanceExceeded(String message) {
        super(message);
    }

    public WalletBalanceExceeded(String message, Exception e) {
        super(message, e);
    }

}
