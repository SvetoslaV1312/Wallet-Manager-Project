package bg.sofia.uni.fmi.mjt.commands.concretecommands;

import bg.sofia.uni.fmi.mjt.commands.Command;
import bg.sofia.uni.fmi.mjt.exceptions.app.command.InvalidCommandArgumentCount;
import bg.sofia.uni.fmi.mjt.exceptions.app.command.InvalidCommandFormat;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.UserAlreadyLoggedIn;
import bg.sofia.uni.fmi.mjt.repository.WalletManagerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

public class RegisterCommandTest {

    @Test
    void testExecuteThrowsUserAlreadyLoggedInWhenSessionExists() {
        Command command = new RegisterCommand(List.of("user", "pass"), "alreadyLoggedInUser");

        Assertions.assertThrows(
            UserAlreadyLoggedIn.class,
            () -> command.execute(null),
            "Expected UserAlreadyLoggedIn when a session already exists"
        );
    }

    @Test
    void testExecuteThrowsInvalidCommandFormatWhenTooFewArguments() {
        Command command = new RegisterCommand(List.of("--username=onlyUsername"), null);

        Assertions.assertThrows(
                InvalidCommandFormat.class,
            () -> command.execute(null),
            "Expected InvalidCommandArgumentCount when fewer arguments are provided"
        );
    }

    @Test
    void testExecuteThrowsInvalidCommandArgumentCountWhenTooManyArguments() {
        Command command = new RegisterCommand(List.of("--username=u", "--password=p", "--extra=test"), null);

        Assertions.assertThrows(
            InvalidCommandArgumentCount.class,
            () -> command.execute(null),
            "Expected InvalidCommandArgumentCount when more arguments are provided"
        );
    }

    @Test
    void testExecuteReturnsCorrectJsonMessageOnSuccess() throws Exception {
        WalletManagerRepository storage = mock(WalletManagerRepository.class);

        Command command = new RegisterCommand(List.of("--username=user", "--password=pass"), null);

        String result = command.execute(storage);

        Assertions.assertTrue(
            result.contains("User registered"),
            "Expected JSON to contain success message"
        );
        Assertions.assertTrue(
            result.contains("\"status\":\"OK\""),
            "Expected JSON to contain GOOD status"
        );
    }
}
