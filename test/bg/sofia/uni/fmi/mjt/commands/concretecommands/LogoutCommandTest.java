package bg.sofia.uni.fmi.mjt.commands.concretecommands;

import bg.sofia.uni.fmi.mjt.commands.Command;
import bg.sofia.uni.fmi.mjt.commands.CommandEnum;
import bg.sofia.uni.fmi.mjt.entity.User;
import bg.sofia.uni.fmi.mjt.exceptions.app.command.InvalidCommandArgumentCount;
import bg.sofia.uni.fmi.mjt.exceptions.app.command.InvalidCommandFormat;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.NoUserLoggedIn;
import bg.sofia.uni.fmi.mjt.repository.WalletManagerRepositoryDB;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

import static org.mockito.Mockito.mock;

public class LogoutCommandTest {
    private User user;
    @BeforeEach
    void setUp() throws Exception {
        user = new User("test", "password");
    }

    @Mock
    WalletManagerRepositoryDB repo = mock();

    @Test
    void testExecuteReturnsJsonResponse() throws Exception {
        Command command = new LogoutCommand(List.of(), user);

        String result = command.execute(mock(WalletManagerRepositoryDB.class));

        Assertions.assertTrue(
                result.startsWith("{") && result.endsWith("}"),
                "Expected the help command to return a JSON object"
        );
    }

    @Test
    void testExecuteContainsGoodStatus() throws Exception {
        Command command = new LogoutCommand(List.of(), user);

        String result = command.execute(mock(WalletManagerRepositoryDB.class));

        Assertions.assertTrue(
                result.contains("\"status\":\"OK\""),
                "Expected logout command to return GOOD status"
        );
    }

    @Test
    void testLogoutThrowsInvalidCommandArgumentCount() {
        Command command = new LogoutCommand(List.of("--arg=test"), user);
        Assertions.assertThrows(InvalidCommandArgumentCount.class,
                () -> command.execute(repo),
                "Expected logout command to throw an exception"
        );
    }

    @Test
    void testLogoutThrowsInvalidCommandFormat() {
        Command command = new LogoutCommand(List.of("arg test"), user);
        Assertions.assertThrows(InvalidCommandFormat.class,
                () -> command.execute(repo),
                "Expected logout command to throw an exception"
        );
    }

    @Test
    void testLogoutThrowsNoUserLoggedIn() {
        Command command = new LogoutCommand(List.of("test"), null);
        Assertions.assertThrows(NoUserLoggedIn.class,
                () -> command.execute(repo),
                "Expected logout command to throw an exception"
        );
    }
}
