package bg.sofia.uni.fmi.mjt.commands.concretecommands;

import bg.sofia.uni.fmi.mjt.commands.Command;
import bg.sofia.uni.fmi.mjt.entity.User;
import bg.sofia.uni.fmi.mjt.exceptions.app.command.InvalidCommandArgumentCount;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.NoUserLoggedIn;
import bg.sofia.uni.fmi.mjt.repository.WalletManagerRepositoryDB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CheckAlertsCommandTest {

    private User user;

    @BeforeEach
    void setUp() throws Exception {
        user = new User("john", "pass");
    }

    @Test
    void testExecuteThrowsNoUserLoggedIn() {
        Command cmd = new CheckAlertsCommand(List.of(), null);
        WalletManagerRepositoryDB mock = mock();

        assertThrows(NoUserLoggedIn.class,
                () -> cmd.execute(mock),
                "Expected NoUserLoggedIn when executing with null user");
    }

    @Test
    void testExecuteThrowsInvalidArgumentCountWhenArgumentsProvided() {
        Command cmd = new CheckAlertsCommand(List.of("--extra=123"), user);

        assertThrows(InvalidCommandArgumentCount.class,
                () -> cmd.execute(null),
                "Expected InvalidCommandArgumentCount when arguments are provided");
    }

    @Test
    void testExecuteReturnsCorrectJsonMessageOnSuccess() throws Exception {
        WalletManagerRepositoryDB storage = mock(WalletManagerRepositoryDB.class);

        when(storage.checkAlerts(user)).thenReturn("BTC alert triggered");

        Command cmd = new CheckAlertsCommand(List.of(), user);

        String result = cmd.execute(storage);

        verify(storage).checkAlerts(user);

        assertTrue(result.contains("All notifications: BTC alert triggered"),
                "Expected JSON to contain the notifications text");
        assertTrue(result.contains("\"status\":\"OK\""),
                "Expected JSON to contain OK status");
    }
}
