package bg.sofia.uni.fmi.mjt.entity;

import bg.sofia.uni.fmi.mjt.exceptions.app.user.IllegalNameException;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.IllegalPasswordException;
import bg.sofia.uni.fmi.mjt.exceptions.app.wallet.NegativeMoneyException;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;
    @BeforeEach
    void setup() throws IllegalPasswordException, IllegalNameException {
        user = new User("sesi", "pass");

    }

    @Test
    void testConstructorAndUsername() throws IllegalPasswordException, IllegalNameException {
        Assertions.assertEquals("sesi", user.username(),
            "Expected the constructor to match usernames");
    }

    @Test
    void testDepositMoneyDelegates() {

        assertThrows(NegativeMoneyException.class, () -> user.depositMoney(BigDecimal.valueOf(-5)),
            "Expected the wallet to throw an exception");
    }

    @Test
    void testBuyOfferingDelegates() throws Exception {
        user.depositMoney(BigDecimal.valueOf(100));

        assertDoesNotThrow(() ->user.buyOffering("BTC", BigDecimal.valueOf(50), BigDecimal.valueOf(50000.0)),
            "When executing in a setup environment no exceptions thrown");
    }

    @Test
    void testSellOfferingDelegates() throws Exception {
        user.depositMoney(BigDecimal.valueOf(1000));

        user.buyOffering("BTC", BigDecimal.valueOf(1000), BigDecimal.valueOf(50000.0));

        assertDoesNotThrow(() ->user.sellOffering("BTC", BigDecimal.valueOf(50000.0)),
            "When executing in a setup environment no exceptions thrown");
    }

    @Test
    void testWalletSummary() {
        String summary = user.walletSummary();

        assertTrue(summary.contains("balance="), "Expected the actual string to have the balance field");
    }

    @Test
    void testConstructorThrowsExceptionWithNullName()  {
        assertThrows(IllegalNameException.class, () -> new User(null, "sth"),
            "Expected an exception to be thrown when name is null");
    }

    @Test
    void testConstructorThrowsExceptionWithNullPassword()  {
        assertThrows(IllegalPasswordException.class, () -> new User("test", null),
            "Expected an exception to be thrown when password is null");
    }

    @Test
    void testWalletOverallSummary() throws Exception {
        user.depositMoney(BigDecimal.valueOf(1000));

        user.buyOffering("BTC", BigDecimal.valueOf(1000), BigDecimal.valueOf(50000.0));

        String summary = user.walletOverallSummary(BigDecimal.valueOf(0.0));

        assertTrue(summary.contains("plus ="), "Expected the actual string to have the overall plus field");
    }

    @Test
    void testEqualsAndHashCode() throws IllegalPasswordException, IllegalNameException {
        User u1 = new User("sesi", "pass");
        User u2 = new User("sesi", "pass");
        User u3 = new User("other", "pass");

        assertEquals(u1, u2, "Expected to match as usernames are same");
        assertEquals(u1.hashCode(), u2.hashCode(), "Expected to match as usernames are same");
        assertNotEquals(u1, u3, "Expected to not match as usernames are different");
    }
}
