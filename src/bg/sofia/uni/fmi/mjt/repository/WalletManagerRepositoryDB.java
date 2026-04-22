package bg.sofia.uni.fmi.mjt.repository;

import bg.sofia.uni.fmi.mjt.cache.CacheCrypto;
import bg.sofia.uni.fmi.mjt.entity.User;
import bg.sofia.uni.fmi.mjt.exceptions.api.ApiExecutionException;
import bg.sofia.uni.fmi.mjt.exceptions.api.DataUnavailableException;
import bg.sofia.uni.fmi.mjt.exceptions.app.AppExecutionException;
import bg.sofia.uni.fmi.mjt.exceptions.app.repository.DataAcessException;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.IllegalNameException;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.IllegalPasswordException;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.UserAlreadyExistsInDB;

import java.math.BigDecimal;

public interface WalletManagerRepositoryDB {
    void registerUser(String username, String password)
            throws AppExecutionException, DataAcessException;

    User loginUser(String username, String password)
            throws AppExecutionException, DataAcessException;

    BigDecimal depositMoney(BigDecimal amount, User user) throws AppExecutionException, DataAcessException;

    String listOffering(String offeringCode)
            throws ApiExecutionException;

    String listOfferings() throws ApiExecutionException;

    void buyOffering(String offeringCode, BigDecimal amount, String discount,  User user)
            throws AppExecutionException, ApiExecutionException, DataAcessException;

    void sellOffering(String offeringCode, User user)
            throws AppExecutionException,  ApiExecutionException, DataAcessException;

    String getWalletSummary(User user) throws AppExecutionException, DataAcessException;

    void logout(User user) throws AppExecutionException, DataAcessException;

    String getWalletOverallSummary(User user)
            throws ApiExecutionException, AppExecutionException, DataAcessException;

    void setPriceAlert(User user, String offeringCode, BigDecimal price) throws DataAcessException;

    String checkAlerts(User user) throws DataAcessException;

    CacheCrypto cachedCryptoCurrency();

}
