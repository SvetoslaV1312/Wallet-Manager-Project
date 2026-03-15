package bg.sofia.uni.fmi.mjt.exceptions.app.wallet;

import bg.sofia.uni.fmi.mjt.exceptions.app.AppExecutionException;

public class CryptoNotFoundInWallet extends AppExecutionException {
    public CryptoNotFoundInWallet(String message) {
        super(message);
    }

    public CryptoNotFoundInWallet(String message, Exception e) {
        super(message, e);
    }

}
