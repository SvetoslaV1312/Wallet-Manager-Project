package bg.sofia.uni.fmi.mjt.commands.concretecommands;

import bg.sofia.uni.fmi.mjt.commands.Command;
import bg.sofia.uni.fmi.mjt.commands.CommandEnum;
import bg.sofia.uni.fmi.mjt.entity.User;
import bg.sofia.uni.fmi.mjt.repository.WalletManagerRepositoryDB;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.mock;

public class HelpCommandTest {
    private User user;
    @BeforeEach
    void setUp() throws Exception {
        user = new User("test", "password");
    }

    @Test
    void testExecuteReturnsJsonResponse() throws Exception {
        Command command = new HelpCommand(List.of(), user);

        String result = command.execute(mock(WalletManagerRepositoryDB.class));

        Assertions.assertTrue(
            result.startsWith("{") && result.endsWith("}"),
            "Expected the help command to return a JSON object"
        );
    }

    @Test
    void testExecuteContainsAllCommandNames() throws Exception {
        Command command = new HelpCommand(List.of(), user);

        String result = command.execute(mock(WalletManagerRepositoryDB.class));

        for (var element : CommandEnum.values()) {
            Assertions.assertTrue(
                result.contains(element.commandName()),
                "Expected help output to contain command: " + element.commandName()
            );
        }
    }

    @Test
    void testExecuteContainsGoodStatus() throws Exception {
        Command command = new HelpCommand(List.of(), user);

        String result = command.execute(mock(WalletManagerRepositoryDB.class));

        Assertions.assertTrue(
            result.contains("\"status\":\"OK\""),
            "Expected help command to return GOOD status"
        );
    }

    @Test
    void testExecuteContainsHeaderText() throws Exception {
        Command command = new HelpCommand(List.of(), user);

        String result = command.execute(mock(WalletManagerRepositoryDB.class));

        Assertions.assertTrue(
            result.contains("List of commands"),
            "Expected help output to contain header text"
        );
    }
}
