package bg.sofia.uni.fmi.mjt.commands;

import bg.sofia.uni.fmi.mjt.exceptions.api.ApiExecutionException;
import bg.sofia.uni.fmi.mjt.exceptions.app.AppExecutionException;
import bg.sofia.uni.fmi.mjt.exceptions.app.repository.DataAcessException;
import bg.sofia.uni.fmi.mjt.repository.WalletManagerRepositoryDB;

public interface CommandInterface {
    String execute(WalletManagerRepositoryDB storage)
            throws ApiExecutionException, AppExecutionException, DataAcessException;
}
