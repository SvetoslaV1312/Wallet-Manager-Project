package bg.sofia.uni.fmi.mjt.commands;

import bg.sofia.uni.fmi.mjt.exceptions.api.ApiExecutionException;
import bg.sofia.uni.fmi.mjt.exceptions.app.AppExecutionException;
import bg.sofia.uni.fmi.mjt.exceptions.app.repository.DataAcessException;

import bg.sofia.uni.fmi.mjt.repository.WalletManagerRepositoryDB;

public class CommandExecutor {
    private final WalletManagerRepositoryDB storage;

    public CommandExecutor(WalletManagerRepositoryDB storage) {
        this.storage = storage;
    }

    public String execute(CommandInterface cmd)
            throws AppExecutionException, ApiExecutionException, DataAcessException {
        return cmd.execute(storage);
    }
}
