package bg.sofia.uni.fmi.mjt.commands.concretecommands;

import bg.sofia.uni.fmi.mjt.commands.Command;
import bg.sofia.uni.fmi.mjt.exceptions.app.command.InvalidCommandArgumentCount;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.NoUserLoggedIn;
import bg.sofia.uni.fmi.mjt.exceptions.app.wallet.IllegalAmountTypeException;
import bg.sofia.uni.fmi.mjt.repository.WalletManagerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;

public class DepositCommandTest {

    @Test
    void testExecuteThrowsNoUserLoggedInWhenUserIsNull() {
        Command command = new DepositCommand(List.of("10"), null);

        Assertions.assertThrows(
            NoUserLoggedIn.class,
            () -> command.execute(null),
            "Expected NoUserLoggedIn when deposit is invoked without a user session"
        );
    }

    @Test
    void testExecuteThrowsInvalidCommandArgumentCountWhenNoArgumentsProvided() {
        Command command = new DepositCommand(List.of(), "testUser");

        Assertions.assertThrows(
            InvalidCommandArgumentCount.class,
            () -> command.execute(null),
            "Expected InvalidCommandArgumentCount when no arguments are provided"
        );
    }

    @Test
    void testExecuteThrowsIllegalAmountTypeExceptionWhenAmountIsNotDouble() {
        Command command = new DepositCommand(List.of("--money=abc"), "testUser");

        Assertions.assertThrows(
            IllegalAmountTypeException.class,
            () -> command.execute(null),
            "Expected IllegalAmountTypeException when amount is string for amount"
        );
    }

    @Test
    void testExecuteReturnsCorrectJsonMessageOnSuccess() throws Exception {
        WalletManagerRepository storage = mock(WalletManagerRepository.class);

        when(storage.depositMoney(BigDecimal.valueOf(50.0), "testUser")).thenReturn(BigDecimal.valueOf(150.0));

        Command command = new DepositCommand(List.of("--money=50"), "testUser");

        String result = command.execute(storage);

        Assertions.assertTrue(
            result.contains("Money deposited current balance: 150.0"),
            "Expected JSON response to contain updated balance"
        );
        Assertions.assertTrue(
            result.contains("\"status\":\"OK\""),
            "Expected JSON response to contain OK status"
        );
    }
}
