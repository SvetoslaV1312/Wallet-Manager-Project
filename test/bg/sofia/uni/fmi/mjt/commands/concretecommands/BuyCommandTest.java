package bg.sofia.uni.fmi.mjt.commands.concretecommands;

import bg.sofia.uni.fmi.mjt.commands.Command;
import bg.sofia.uni.fmi.mjt.exceptions.app.command.InvalidCommandArgumentCount;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.NoUserLoggedIn;
import bg.sofia.uni.fmi.mjt.exceptions.app.wallet.IllegalAmountTypeException;
import bg.sofia.uni.fmi.mjt.repository.InMemoryWalletManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.mock;

public class BuyCommandTest {
    @Test
    void testExecuteThrowsNoUserLoggedInWithInvalidUserSession() {
        Command command = new BuyCommand(List.of(), null);
        Assertions.assertThrows(NoUserLoggedIn.class, () -> command.execute(null),
            "Expected an exception to be thrown if buy is invoked with no user");
    }

    @Test
    void testExecuteThrowsInvalidCommandArgumentCount() {
        Command command = new BuyCommand(List.of(), "test");
        Assertions.assertThrows(InvalidCommandArgumentCount.class, () -> command.execute(null),
            "Expected an exception to be thrown if buy is invoked with invalid arguments");
    }

    @Test
    void testExecuteThrowsIllegalAmountTypeException() {
        Command command = new BuyCommand(List.of("--offering=test", "--money=test"), "test");
        Assertions.assertThrows(IllegalAmountTypeException.class, () -> command.execute(null),
            "Expected an exception to be thrown if buy is invoked with string for amount");
    }

    @Test
    void testExecuteReturnsApropriateMessage() throws Exception {
        InMemoryWalletManager storage = mock();
        Command command = new BuyCommand(List.of("--offering=offering", "--money=10"), "test");
        String message =  command.execute(storage);
        Assertions.assertTrue(message.contains("Buy order executed"),
            "Expcted the returned message to contain valid information");

    }
}
