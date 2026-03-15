package bg.sofia.uni.fmi.mjt.entity;

import bg.sofia.uni.fmi.mjt.exceptions.app.wallet.CryptoNotFoundInWallet;
import bg.sofia.uni.fmi.mjt.exceptions.app.wallet.NegativeMoneyException;
import bg.sofia.uni.fmi.mjt.exceptions.app.wallet.WalletBalanceExceeded;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class UserWallet implements Serializable {
    private static final String WALLET_SUMMARY = "UserWallet summary";
    private static final String BALANCE = "balance=";
    private static final String ASSETS_IN_USD = ", assets in USD=";
    private static final String OVERALL_PLUS = "Overall plus = ";
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final String USD = " $";
    private static final String CRYPTO = "Crypto: ";
    public static final String INDENT = " ";
    private BigDecimal balance;
    private BigDecimal invested;
    private final Map<String, BigDecimal> assets;

    public UserWallet() {
        assets = new HashMap<>();
        this.balance = BigDecimal.valueOf(0);
        this.invested = BigDecimal.valueOf(0);
    }

    public BigDecimal depositMoney(BigDecimal amount) throws NegativeMoneyException {
        checkAmount(amount);
        return balance = balance.add(amount);
    }

    public BigDecimal balance() {
        return balance;
    }

    public Map<String, BigDecimal> assets() {
        return assets;
    }

    public void buyOffering(String offeringCode, BigDecimal amount, BigDecimal assetPrice)
        throws  WalletBalanceExceeded, NegativeMoneyException {
        checkAmount(amount);
        if (balance.subtract(amount).compareTo(BigDecimal.valueOf(0)) < 0) {
            throw new WalletBalanceExceeded("Can't execute offering because" +
                " amount exceeds wallet balance");
        }
        balance = balance.subtract(amount);
        invested = invested.add(amount);
        BigDecimal currentlyInvested = assets.get(offeringCode)
                == null ? BigDecimal.valueOf(0) : assets.get(offeringCode);
        BigDecimal newlyInvested =  amount.divide(assetPrice, MathContext.DECIMAL64);

        assets.put(offeringCode, currentlyInvested.add(newlyInvested));
    }

    public void sellOffering(String offeringCode, BigDecimal assetPrice) throws CryptoNotFoundInWallet {
        if (assets.get(offeringCode) == null) {
            throw new CryptoNotFoundInWallet("Can't find this crypto in the user's wallet");
        }
        BigDecimal currentlyInvected = assets.get(offeringCode).multiply(assetPrice);
        balance = balance.add(currentlyInvected);
        invested = invested.subtract(currentlyInvected);
        assets.remove(offeringCode);
    }

    @Override
    public String toString() {
        return WALLET_SUMMARY + LINE_SEPARATOR +
                BALANCE + balance + LINE_SEPARATOR +
                ASSETS_IN_USD +
                assets.entrySet().stream()
                        .map(entry ->
                                CRYPTO + entry.getValue() +
                                        INDENT + entry.getKey() +
                                        USD + LINE_SEPARATOR)
                        .collect(Collectors.joining());
    }

    public String overallSummary(BigDecimal sum) {
        String s = this.toString();
        return s.concat(OVERALL_PLUS + (sum.subtract(invested)) + USD);
    }

    private void checkAmount(BigDecimal amount) throws NegativeMoneyException {
        if (amount.compareTo(BigDecimal.valueOf(0)) <= 0) {
            throw new NegativeMoneyException("Amount must be positive");
        }
    }
}
