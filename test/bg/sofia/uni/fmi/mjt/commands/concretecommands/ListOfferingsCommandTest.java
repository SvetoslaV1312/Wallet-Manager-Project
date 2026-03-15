package bg.sofia.uni.fmi.mjt.commands.concretecommands;

import bg.sofia.uni.fmi.mjt.commands.Command;
import bg.sofia.uni.fmi.mjt.exceptions.app.command.InvalidCommandArgumentCount;
import bg.sofia.uni.fmi.mjt.repository.WalletManagerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

public class ListOfferingsCommandTest {

    @Test
    void testExecuteThrowsInvalidCommandArgumentCountWhenArgumentsProvided() {
        Command command = new ListOfferingsCommand(List.of("--extra=sth"), "testUser");

        Assertions.assertThrows(
            InvalidCommandArgumentCount.class,
            () -> command.execute(null),
            "Expected InvalidCommandArgumentCount when arguments are provided but none are allowed"
        );
    }

    @Test
    void testExecuteReturnsCorrectJsonMessageOnSuccess() throws Exception {
        WalletManagerRepository storage = mock(WalletManagerRepository.class);
        when(storage.listOfferings())
                .thenReturn("[BTC, ETH]");

        Command command = new ListOfferingsCommand(List.of(), "testUser");

        String result = command.execute(storage);
        Assertions.assertTrue(
                result.contains("All available offerings: [BTC, ETH]"),
                "Expected JSON to contain the offerings list");
        Assertions.assertTrue(
                result.contains("\"status\":\"OK\""),
                "Expected JSON to contain OK status"
        );
    }
}
