package bg.sofia.uni.fmi.mjt.commands.concretecommands;

import bg.sofia.uni.fmi.mjt.commands.Command;
import bg.sofia.uni.fmi.mjt.exceptions.app.command.InvalidCommandArgumentCount;
import bg.sofia.uni.fmi.mjt.exceptions.app.command.InvalidCommandFormat;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.NoUserLoggedIn;
import bg.sofia.uni.fmi.mjt.repository.WalletManagerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

public class SellCommandTest {

    @Test
    void testExecuteThrowsNoUserLoggedInWhenUserIsNull() {
        Command command = new SellCommand(List.of("BTC"), null);

        Assertions.assertThrows(
            NoUserLoggedIn.class,
            () -> command.execute(null),
            "Expected NoUserLoggedIn when no user session is present"
        );
    }

    @Test
    void testExecuteThrowsInvalidCommandArgumentCountWhenNoArgumentsProvided() {
        Command command = new SellCommand(List.of(), "testUser");

        Assertions.assertThrows(
                InvalidCommandFormat.class,
            () -> command.execute(null),
            "Expected InvalidCommandArgumentCount when no arguments are provided"
        );
    }

    @Test
    void testExecuteThrowsInvalidCommandArgumentCountWhenTooManyArgumentsProvided() {
        Command command = new SellCommand(List.of("--offering=BTC", "--sth=EXTRA"), "testUser");

        Assertions.assertThrows(
            InvalidCommandArgumentCount.class,
            () -> command.execute(null),
            "Expected InvalidCommandArgumentCount when too many arguments are provided"
        );
    }

    @Test
    void testExecuteReturnsCorrectJsonMessageOnSuccess() throws Exception {
        WalletManagerRepository storage = mock(WalletManagerRepository.class);

        Command command = new SellCommand(List.of("--offering=BTC"), "testUser");

        String result = command.execute(storage);

        Assertions.assertTrue(
            result.contains("Sell order executed"),
            "Expected JSON to contain success message"
        );
        Assertions.assertTrue(
            result.contains("\"status\":\"OK\""),
            "Expected JSON to contain GOOD status"
        );
    }
}
