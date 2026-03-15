package bg.sofia.uni.fmi.mjt.entity;

import bg.sofia.uni.fmi.mjt.exceptions.api.ApiKeyLimitExceededException;
import bg.sofia.uni.fmi.mjt.exceptions.api.BadRequestException;
import bg.sofia.uni.fmi.mjt.exceptions.api.CryptoNotFoundException;
import bg.sofia.uni.fmi.mjt.exceptions.api.DataUnavailableException;
import bg.sofia.uni.fmi.mjt.exceptions.api.InsufficientPermissions;
import bg.sofia.uni.fmi.mjt.exceptions.api.UnauthorizedKeyException;
import bg.sofia.uni.fmi.mjt.exceptions.app.wallet.CryptoNotFoundInWallet;
import bg.sofia.uni.fmi.mjt.exceptions.app.wallet.NegativeMoneyException;
import bg.sofia.uni.fmi.mjt.exceptions.app.wallet.WalletBalanceExceeded;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.math.BigDecimal;

class UserWalletTest {
    @Mock
    private UserWallet userWallet;

    @BeforeEach
    void setUp() {
        userWallet = new UserWallet();
    }

    @Test
    void testDepositMoneyThrowsExceptionWithNegativeAmount() {
        Assertions.assertThrows(NegativeMoneyException.class, () -> userWallet.depositMoney(BigDecimal.valueOf(-10)),
            "Expected the method to throw NegativeAmountException.");
    }

    @Test
    void testDepositMoneyChangesBalance() throws NegativeMoneyException {
        userWallet.depositMoney(BigDecimal.valueOf(100));
        Assertions.assertEquals(BigDecimal.valueOf(100), userWallet.balance(), "Expected the balance to equal the" +
            " deposit on a fresh account");
    }

    @Test
    void testBuyOfferingNegativeAmount() {
        Assertions.assertThrows(NegativeMoneyException.class,
            () -> userWallet.buyOffering("BTC", BigDecimal.valueOf(-10),BigDecimal.valueOf(0)),
            "Expected the method to throw NegativeAmountException.");
    }

    @Test
    void testBuyOfferingAmountExceedsBalance() throws NegativeMoneyException {
        userWallet.depositMoney(BigDecimal.valueOf(100));
        Assertions.assertThrows(WalletBalanceExceeded.class,
            () -> userWallet.buyOffering("BTC", BigDecimal.valueOf(5000), BigDecimal.valueOf(0)),
            "Expected the method to throw WalletBalanceExceeded when balance too small.");
    }

    @Test
    void testBuyOfferingAddsToAssetsAndSubtractsBalance()
        throws NegativeMoneyException, WalletBalanceExceeded {

        userWallet.depositMoney(BigDecimal.valueOf(100000.0));
        BigDecimal initialBalance = userWallet.balance();

        userWallet.buyOffering("BTC", BigDecimal.valueOf(1000), BigDecimal.valueOf(50000.0));

        Assertions.assertEquals(initialBalance.subtract(BigDecimal.valueOf(1000.0)), userWallet.balance(),
            "Expected the balance to be minus the price of how much is bought");

        Assertions.assertEquals(BigDecimal.valueOf(0.02), userWallet.assets().get("BTC"),
            "Expected the asset to be right amount");
    }

    @Test
    void testSellOfferingCryptoNotFound() {
        Assertions.assertThrows(CryptoNotFoundInWallet.class,
            () -> userWallet.sellOffering("BTC", BigDecimal.valueOf(0)),
            "Expected exception when trying to sell a currency which is not acquired");
    }

    @Test
    void testSellOfferingSuccess() throws Exception {
        userWallet.depositMoney(BigDecimal.valueOf(10000));

        userWallet.buyOffering("BTC", BigDecimal.valueOf(1000), BigDecimal.valueOf(50000.0));
        userWallet.sellOffering("BTC", BigDecimal.valueOf(50000.0));

        Assertions.assertFalse(userWallet.assets().containsKey("BTC"),
            "Expected the wallet to not have btc after it has been sold");
    }

    @Test
    void testOverallSummary() throws Exception {
        setUpBuyOrders();

        String summary = userWallet.overallSummary(BigDecimal.valueOf(0));
        Assertions.assertTrue(summary.contains("Overall plus = "), "Expected the string to contain right amount of btc");

    }

    @Test
    void testToString() throws Exception {
        setUpBuyOrders();

        String summary = userWallet.toString();

        Assertions.assertTrue(summary.contains("0.02 BTC $"), "Expected the summary to contain right amount");
        Assertions.assertTrue(summary.contains("0.25 ETH $"), "Expected the summary to contain right amount");
    }

    private void setUpBuyOrders()
        throws NegativeMoneyException, WalletBalanceExceeded {
        userWallet.depositMoney(BigDecimal.valueOf(10000));

        userWallet.buyOffering("BTC", BigDecimal.valueOf(1000), BigDecimal.valueOf(50000.0));
        userWallet.buyOffering("ETH", BigDecimal.valueOf(500), BigDecimal.valueOf(2000.0));
    }

}
