package bg.sofia.uni.fmi.mjt.commands.concretecommands;

import bg.sofia.uni.fmi.mjt.commands.Command;
import bg.sofia.uni.fmi.mjt.entity.User;
import bg.sofia.uni.fmi.mjt.exceptions.app.command.InvalidCommandArgumentCount;
import bg.sofia.uni.fmi.mjt.exceptions.app.command.InvalidCommandFormat;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.NoUserLoggedIn;
import bg.sofia.uni.fmi.mjt.repository.WalletManagerRepositoryDB;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

public class SellCommandTest {

    private User user;
    @BeforeEach
    void setUp() throws Exception {
        user = new User("test", "password");
    }

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
        Command command = new SellCommand(List.of(), user);

        Assertions.assertThrows(
                InvalidCommandFormat.class,
            () -> command.execute(null),
            "Expected InvalidCommandArgumentCount when no arguments are provided"
        );
    }

    @Test
    void testExecuteThrowsInvalidCommandArgumentCountWhenTooManyArgumentsProvided() {
        Command command = new SellCommand(List.of("--offering=BTC", "--sth=EXTRA"), user);

        Assertions.assertThrows(
            InvalidCommandArgumentCount.class,
            () -> command.execute(null),
            "Expected InvalidCommandArgumentCount when too many arguments are provided"
        );
    }

    @Test
    void testExecuteReturnsCorrectJsonMessageOnSuccess() throws Exception {
        WalletManagerRepositoryDB storage = mock(WalletManagerRepositoryDB.class);

        Command command = new SellCommand(List.of("--offering=BTC"), user);

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
