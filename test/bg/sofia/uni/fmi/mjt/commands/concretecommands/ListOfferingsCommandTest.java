package bg.sofia.uni.fmi.mjt.commands.concretecommands;

import bg.sofia.uni.fmi.mjt.commands.Command;
import bg.sofia.uni.fmi.mjt.entity.User;
import bg.sofia.uni.fmi.mjt.exceptions.app.command.InvalidCommandArgumentCount;
import bg.sofia.uni.fmi.mjt.repository.WalletManagerRepositoryDB;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

public class ListOfferingsCommandTest {
    private User user;
    @BeforeEach
    void setUp() throws Exception {
        user = new User("test", "password");
    }


    @Test
    void testExecuteThrowsInvalidCommandArgumentCountWhenArgumentsProvided() {
        Command command = new ListOfferingsCommand(List.of("--extra=sth"), user);

        Assertions.assertThrows(
            InvalidCommandArgumentCount.class,
            () -> command.execute(null),
            "Expected InvalidCommandArgumentCount when arguments are provided but none are allowed"
        );
    }

    @Test
    void testExecuteReturnsCorrectJsonMessageOnSuccess() throws Exception {
        WalletManagerRepositoryDB storage = mock(WalletManagerRepositoryDB.class);
        when(storage.listOfferings())
                .thenReturn("[BTC, ETH]");

        Command command = new ListOfferingsCommand(List.of(), user);

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
