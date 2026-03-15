package bg.sofia.uni.fmi.mjt.commands.concretecommands;

import bg.sofia.uni.fmi.mjt.commands.Command;
import bg.sofia.uni.fmi.mjt.exceptions.app.command.InvalidCommandArgumentCount;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.NoUserLoggedIn;
import bg.sofia.uni.fmi.mjt.repository.WalletManagerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetWalletOverallSummaryCommandTest {

    @Test
    void testExecuteThrowsNoUserLoggedInWhenUserIsNull() {
        Command command = new GetWalletOverallSummaryCommand(List.of(), null);

        Assertions.assertThrows(
            NoUserLoggedIn.class,
            () -> command.execute(null),
            "Expected NoUserLoggedIn when no user session is present"
        );
    }

    @Test
    void testExecuteThrowsInvalidCommandArgumentCountWhenArgumentsProvided() {
        Command command = new GetWalletOverallSummaryCommand(List.of("--extra=srh"), "testUser");

        Assertions.assertThrows(
            InvalidCommandArgumentCount.class,
            () -> command.execute(null),
            "Expected InvalidCommandArgumentCount when arguments are provided"
        );
    }

    @Test
    void testExecuteReturnsCorrectJsonMessageOnSuccess() throws Exception {
        WalletManagerRepository storage = mock(WalletManagerRepository.class);

        when(storage.getWalletOverallSummary("testUser"))
            .thenReturn(" -> summary content");

        Command command = new GetWalletOverallSummaryCommand(List.of(), "testUser");

        String result = command.execute(storage);

        Assertions.assertTrue(
            result.contains("Summary of the wallet of username testUser"),
            "Expected JSON to contain the summary prefix"
        );
        Assertions.assertTrue(
            result.contains("summary content"),
            "Expected JSON to contain the summary returned by storage"
        );
    }
}
