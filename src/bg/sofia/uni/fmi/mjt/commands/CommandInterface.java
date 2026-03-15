package bg.sofia.uni.fmi.mjt.commands;

import bg.sofia.uni.fmi.mjt.exceptions.api.ApiExecutionException;
import bg.sofia.uni.fmi.mjt.exceptions.app.AppExecutionException;
import bg.sofia.uni.fmi.mjt.repository.WalletManagerRepository;

public interface CommandInterface {
    String execute(WalletManagerRepository storage)
            throws ApiExecutionException, AppExecutionException;
}
