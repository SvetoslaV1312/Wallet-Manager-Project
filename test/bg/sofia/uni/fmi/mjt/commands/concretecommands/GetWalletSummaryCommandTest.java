package bg.sofia.uni.fmi.mjt.commands.concretecommands;

import bg.sofia.uni.fmi.mjt.commands.Command;
import bg.sofia.uni.fmi.mjt.entity.User;
import bg.sofia.uni.fmi.mjt.exceptions.app.command.InvalidCommandArgumentCount;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.NoUserLoggedIn;
import bg.sofia.uni.fmi.mjt.repository.WalletManagerRepositoryDB;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetWalletSummaryCommandTest {
    private User user;
    @BeforeEach
    void setUp() throws Exception {
        user = new User("test", "password");
    }

    @Test
    void testExecuteThrowsNoUserLoggedInWhenUserIsNull() {
        Command command = new GetWalletSummaryCommand(List.of(), null);

        Assertions.assertThrows(
            NoUserLoggedIn.class,
            () -> command.execute(null),
            "Expected NoUserLoggedIn when no user session is present"
        );
    }

    @Test
    void testExecuteThrowsInvalidCommandArgumentCountWhenArgumentsProvided() {
        Command command = new GetWalletSummaryCommand(List.of("--extra=sth"), user);

        Assertions.assertThrows(
            InvalidCommandArgumentCount.class,
            () -> command.execute(null),
            "Expected InvalidCommandArgumentCount when arguments are provided"
        );
    }

    @Test
    void testExecuteReturnsCorrectJsonMessageOnSuccess() throws Exception {
        WalletManagerRepositoryDB storage = mock(WalletManagerRepositoryDB.class);

        when(storage.getWalletSummary(user))
            .thenReturn(" -> summary content");

        Command command = new GetWalletSummaryCommand(List.of(), user);

        String result = command.execute(storage);

        Assertions.assertTrue(
            result.contains("Summary of the wallet of username test"),
            "Expected JSON to contain the summary prefix"
        );
        Assertions.assertTrue(
            result.contains("summary content"),
            "Expected JSON to contain the summary returned by storage"
        );

    }
}
