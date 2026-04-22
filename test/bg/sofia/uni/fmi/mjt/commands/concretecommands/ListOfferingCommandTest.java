package bg.sofia.uni.fmi.mjt.commands.concretecommands;

import bg.sofia.uni.fmi.mjt.commands.Command;
import bg.sofia.uni.fmi.mjt.entity.User;
import bg.sofia.uni.fmi.mjt.exceptions.app.command.InvalidCommandArgumentCount;
import bg.sofia.uni.fmi.mjt.exceptions.app.command.InvalidCommandFormat;
import bg.sofia.uni.fmi.mjt.repository.WalletManagerRepositoryDB;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

public class ListOfferingCommandTest {
    private User user;
    @BeforeEach
    void setUp() throws Exception {
        user = new User("test", "password");
    }

    @Test
    void testExecuteThrowsInvalidCommandFormatCountWhenArgumentsMissing() {
        Command command = new ListOfferingCommand(List.of(), user);

        Assertions.assertThrows(
                InvalidCommandFormat.class,
            () -> command.execute(null),
            "Expected InvalidCommandArgumentCount when no arguments are provided"
        );
    }

    @Test
    void testExecuteThrowsInvalidCommandArgumentCountWhenTooManyArguments() {
        Command command = new ListOfferingCommand(List.of("--offering=BTC", "--sth=EXTRA"), user);

        Assertions.assertThrows(
            InvalidCommandArgumentCount.class,
            () -> command.execute(null),
            "Expected InvalidCommandArgumentCount when too many arguments are provided"
        );
    }

    @Test
    void testExecuteReturnsCorrectJsonMessageOnSuccess() throws Exception {
        WalletManagerRepositoryDB storage = mock(WalletManagerRepositoryDB.class);

        when(storage.listOffering("BTC"))
            .thenReturn("BTC - Bitcoin");

        Command command = new ListOfferingCommand(List.of("--offering=BTC"), user);

        String result = command.execute(storage);
        Assertions.assertTrue(
            result.contains("Offering: BTC - Bitcoin"),
            "Expected JSON to contain the offering details"
        );
        Assertions.assertTrue(
            result.contains("\"status\":\"OK\""),
            "Expected JSON to contain Ok status"
        );
    }
}
