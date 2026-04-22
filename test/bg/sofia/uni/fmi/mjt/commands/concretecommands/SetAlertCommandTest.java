package bg.sofia.uni.fmi.mjt.commands.concretecommands;

import bg.sofia.uni.fmi.mjt.commands.Command;
import bg.sofia.uni.fmi.mjt.entity.User;
import bg.sofia.uni.fmi.mjt.exceptions.app.command.InvalidCommandArgumentCount;
import bg.sofia.uni.fmi.mjt.exceptions.app.command.InvalidCommandFormat;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.NoUserLoggedIn;
import bg.sofia.uni.fmi.mjt.exceptions.app.wallet.IllegalAmountTypeException;
import bg.sofia.uni.fmi.mjt.repository.WalletManagerRepositoryDB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SetAlertCommandTest {

    private User user;

    @BeforeEach
    void setUp() throws Exception {
        user = new User("john", "pass");
    }

    @Test
    void testExecuteThrowsNoUserLoggedIn() {
        Command cmd = new SetAlertCommand(List.of(), null);

        assertThrows(NoUserLoggedIn.class,
                () -> cmd.execute(null),
                "Expected exception when executing with no user session");
    }

    @Test
    void testExecuteThrowsInvalidArgumentCount() {
        Command cmd = new SetAlertCommand(List.of("--offering=BTC"), user);

        assertThrows(InvalidCommandArgumentCount.class,
                () -> cmd.execute(null),
                "Expected exception when missing required arguments");
    }

    @Test
    void testExecuteThrowsInvalidCommandFormat() {
        Command cmd = new SetAlertCommand(List.of("--price=100 --other_arg=some"), user);

        assertThrows(InvalidCommandFormat.class,
                () -> cmd.execute(null),
                "Expected exception when offering or price is missing");
    }

    @Test
    void testExecuteThrowsIllegalAmountTypeException() {
        Command cmd = new SetAlertCommand(List.of("--offering=BTC", "--price=abc"), user);

        assertThrows(IllegalAmountTypeException.class,
                () -> cmd.execute(null),
                "Expected exception when price is not a number");
    }

    @Test
    void testExecuteSuccessReturnsValidMessage() throws Exception {
        WalletManagerRepositoryDB storage = mock(WalletManagerRepositoryDB.class);

        Command cmd = new SetAlertCommand(
                List.of("--offering=BTC", "--price=100"),
                user
        );

        String result = cmd.execute(storage);

        verify(storage).setPriceAlert(user, "BTC", new BigDecimal("100.0"));

        assertTrue(result.contains("Set notification to the price"),
                "Expected success message to contain confirmation text");
    }
}
