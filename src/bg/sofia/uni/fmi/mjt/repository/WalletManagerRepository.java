package bg.sofia.uni.fmi.mjt.repository;

import bg.sofia.uni.fmi.mjt.cache.CacheCrypto;
import bg.sofia.uni.fmi.mjt.entity.User;
import bg.sofia.uni.fmi.mjt.exceptions.api.ApiExecutionException;
import bg.sofia.uni.fmi.mjt.exceptions.api.ApiKeyLimitExceededException;
import bg.sofia.uni.fmi.mjt.exceptions.api.BadRequestException;
import bg.sofia.uni.fmi.mjt.exceptions.api.CryptoNotFoundException;
import bg.sofia.uni.fmi.mjt.exceptions.api.DataUnavailableException;
import bg.sofia.uni.fmi.mjt.exceptions.api.InsufficientPermissions;
import bg.sofia.uni.fmi.mjt.exceptions.api.UnauthorizedKeyException;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.IllegalNameException;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.IllegalPasswordException;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.UserAlreadyLoggedIn;
import bg.sofia.uni.fmi.mjt.exceptions.app.wallet.CryptoNotFoundInWallet;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.UserAlreadyExistsInDB;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.UserHasNotBeenRegistered;
import bg.sofia.uni.fmi.mjt.exceptions.app.wallet.IllegalAmountTypeException;
import bg.sofia.uni.fmi.mjt.exceptions.app.wallet.NegativeMoneyException;
import bg.sofia.uni.fmi.mjt.exceptions.app.wallet.WalletBalanceExceeded;
import bg.sofia.uni.fmi.mjt.exceptions.app.command.WrongPasswordException;

import java.io.Writer;
import java.math.BigDecimal;

public interface WalletManagerRepository {
    void registerUser(String username, String password)
        throws UserAlreadyExistsInDB, IllegalPasswordException, IllegalNameException;

    User loginUser(String username, String password)
        throws UserHasNotBeenRegistered, WrongPasswordException, UserAlreadyLoggedIn;

    BigDecimal depositMoney(BigDecimal amount, String user) throws NegativeMoneyException;

    String listOffering(String offeringCode)
            throws ApiExecutionException;

    String listOfferings() throws DataUnavailableException, InsufficientPermissions, BadRequestException,
        UnauthorizedKeyException, ApiKeyLimitExceededException, CryptoNotFoundException;

    void buyOffering(String offeringCode, BigDecimal amount, String discount,  String user)
            throws WalletBalanceExceeded, ApiExecutionException,
            NegativeMoneyException, IllegalAmountTypeException;

    void sellOffering(String offeringCode, String user)
            throws CryptoNotFoundInWallet, ApiExecutionException;

    String getWalletSummary(String user);

    void logout(String user) throws UserHasNotBeenRegistered;

    String getWalletOverallSummary(String user)
            throws ApiExecutionException;

    void saveUsersToDataBase(Writer writer);

    CacheCrypto cachedCryptoCurrency();

}
