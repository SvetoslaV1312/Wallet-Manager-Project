package bg.sofia.uni.fmi.mjt.entity;

import bg.sofia.uni.fmi.mjt.exceptions.app.user.IllegalNameException;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.IllegalPasswordException;
import bg.sofia.uni.fmi.mjt.exceptions.app.wallet.CryptoNotFoundInWallet;
import bg.sofia.uni.fmi.mjt.exceptions.app.wallet.NegativeMoneyException;
import bg.sofia.uni.fmi.mjt.exceptions.app.wallet.WalletBalanceExceeded;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

import static bg.sofia.uni.fmi.mjt.encryption.PasswordEncryptionV2.hash;
import static bg.sofia.uni.fmi.mjt.encryption.PasswordEncryptionV2.verify;

public class User implements Serializable {
    private final String username;
    private final String password;
    private final UserWallet userWallet;
    private transient boolean isUserLoggedIn = false;
    public User(String username, String password)
        throws IllegalPasswordException, IllegalNameException {
        checkPassword(password);
        checkUserName(username);
        this.password = hash(password);
        this.username = username;
        this.userWallet = new UserWallet();
    }

    public String username() {
        return username;
    }

    public boolean passwordMatch(String otherPassword) {
        return verify(otherPassword, this.password);
    }

    public synchronized boolean isLoggedIn() {
        return isUserLoggedIn;
    }

    public synchronized void setLoggedIn(boolean loggedIn) {
        this.isUserLoggedIn = loggedIn;
    }

    public BigDecimal depositMoney(BigDecimal amount) throws NegativeMoneyException {
        return userWallet.depositMoney(amount);
    }

    public void buyOffering(String offeringCode, BigDecimal amount, BigDecimal assetPrice)
        throws WalletBalanceExceeded, NegativeMoneyException {
        userWallet.buyOffering(offeringCode, amount, assetPrice);
    }

    public void sellOffering(String offeringCode, BigDecimal assetPrice)
        throws CryptoNotFoundInWallet {
        userWallet.sellOffering(offeringCode, assetPrice);
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
