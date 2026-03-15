package bg.sofia.uni.fmi.mjt.commands;

import bg.sofia.uni.fmi.mjt.exceptions.api.*;
import bg.sofia.uni.fmi.mjt.exceptions.app.AppExecutionException;
import bg.sofia.uni.fmi.mjt.exceptions.app.command.InvalidCommandArgumentCount;
import bg.sofia.uni.fmi.mjt.exceptions.app.command.InvalidCommandFormat;
import bg.sofia.uni.fmi.mjt.exceptions.app.command.WrongPasswordException;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.IllegalNameException;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.IllegalPasswordException;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.NoUserLoggedIn;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.UserAlreadyExistsInDB;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.UserAlreadyLoggedIn;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.UserHasNotBeenRegistered;
import bg.sofia.uni.fmi.mjt.exceptions.app.wallet.CryptoNotFoundInWallet;
import bg.sofia.uni.fmi.mjt.exceptions.app.wallet.IllegalAmountTypeException;
import bg.sofia.uni.fmi.mjt.exceptions.app.wallet.NegativeMoneyException;
import bg.sofia.uni.fmi.mjt.exceptions.app.wallet.WalletBalanceExceeded;
import bg.sofia.uni.fmi.mjt.repository.WalletManagerRepository;

public class CommandExecutor {
    private final WalletManagerRepository storage;

    public CommandExecutor(WalletManagerRepository storage) {
        this.storage = storage;
    }

    public String execute(CommandInterface cmd)
            throws AppExecutionException, ApiExecutionException {
        return cmd.execute(storage);
    }
}
