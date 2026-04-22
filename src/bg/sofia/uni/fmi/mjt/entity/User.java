package bg.sofia.uni.fmi.mjt.entity;

import bg.sofia.uni.fmi.mjt.exceptions.app.user.IllegalNameException;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.IllegalPasswordException;
import bg.sofia.uni.fmi.mjt.exceptions.app.wallet.CryptoNotFoundInWallet;
import bg.sofia.uni.fmi.mjt.exceptions.app.wallet.NegativeMoneyException;
import bg.sofia.uni.fmi.mjt.exceptions.app.wallet.WalletBalanceExceeded;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

import static bg.sofia.uni.fmi.mjt.encryption.PasswordEncryptionV2.hash;

public class User implements Serializable {
    private final String username;
    private final String password;
    private final UserWallet userWallet;
    private transient boolean isUserLoggedIn = false;
    private final Map<String, Pair<Date, String>> notifications;

    public User(String username, String password)
        throws IllegalPasswordException, IllegalNameException {
        checkPassword(password);
        checkUserName(username);
        this.password = hash(password);
        this.username = username;
        this.userWallet = new UserWallet();
        this.notifications = new HashMap<>();
    }

    public String username() {
        return username;
    }

    public UserWallet getUserWallet() {
        return userWallet;
    }

    public synchronized boolean isLoggedIn() {
        return isUserLoggedIn;
    }

    public void receiveNotification(String crypto, String message) {
        notifications.put(crypto, new Pair<>(new Date(), message));
    }

    public String notifications() {
        return notifications.toString();
    }

    public synchronized void setLoggedIn(boolean loggedIn) {
        this.isUserLoggedIn = loggedIn;
    }

    public BigDecimal depositMoney(BigDecimal amount) throws NegativeMoneyException {
        return userWallet.depositMoney(amount);
    }

    public BigDecimal buyOffering(String offeringCode, BigDecimal amount, BigDecimal assetPrice)
        throws WalletBalanceExceeded, NegativeMoneyException {
        return userWallet.buyOffering(offeringCode, amount, assetPrice);
    }

    public BigDecimal sellOffering(String offeringCode, BigDecimal assetPrice)
        throws CryptoNotFoundInWallet {
        return userWallet.sellOffering(offeringCode, assetPrice);
    }

    public String walletSummary() {
        return userWallet.toString();
    }

    public Map<String, BigDecimal> assets() {
        return Collections.unmodifiableMap(userWallet.assets());
    }

    public String walletOverallSummary(BigDecimal sum) {
        return userWallet.overallSummary(sum);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;
        return username.equals(user.username());
    }

    @Override
    public int hashCode() {
        return username().hashCode();
    }

    private void checkUserName(String username) throws IllegalNameException {
        if (username == null || username.isBlank()) {
            throw new IllegalNameException("Username cant be null nor blank");
        }
    }

    private void checkPassword(String password) throws IllegalPasswordException {
        if (password == null || password.isBlank()) {
            throw new IllegalPasswordException("Password cant be null nor blank");
        }
    }

}
