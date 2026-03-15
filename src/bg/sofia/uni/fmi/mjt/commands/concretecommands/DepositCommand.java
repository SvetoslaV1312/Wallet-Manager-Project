package bg.sofia.uni.fmi.mjt.commands.concretecommands;

import bg.sofia.uni.fmi.mjt.commands.Command;
import bg.sofia.uni.fmi.mjt.exceptions.app.command.InvalidCommandFormat;
import bg.sofia.uni.fmi.mjt.exceptions.app.wallet.IllegalAmountTypeException;
import bg.sofia.uni.fmi.mjt.exceptions.app.wallet.NegativeMoneyException;
import bg.sofia.uni.fmi.mjt.response.ServerResponse;
import bg.sofia.uni.fmi.mjt.exceptions.app.command.InvalidCommandArgumentCount;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.NoUserLoggedIn;
import bg.sofia.uni.fmi.mjt.repository.WalletManagerRepository;
import bg.sofia.uni.fmi.mjt.utility.ArgumentParser;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class DepositCommand extends Command {
    private static final int ARGUMENTS_COUNT = 1;
    private static final String MONEY = "money";

    public DepositCommand(List<String> arguments, String user) {
        super(arguments, user);
    }

    @Override
    public void checkArgumentsCount(Map<String, String> argsMap) throws
            InvalidCommandArgumentCount, InvalidCommandFormat {
        if (argsMap.size() < ARGUMENTS_COUNT) {
            throw new InvalidCommandArgumentCount("Invalid argument count try command $ help for all commands");
        }
        if (!argsMap.containsKey(MONEY)) {
            throw new InvalidCommandFormat("The command must contain the mandatory field: --" + MONEY);
        }
    }

    @Override
    public String execute(WalletManagerRepository storage)
            throws NoUserLoggedIn, InvalidCommandArgumentCount,
            NegativeMoneyException, IllegalAmountTypeException,
            InvalidCommandFormat {
        isValidSessionPresent();
        var argsMap = ArgumentParser.parseString(arguments);
        checkArgumentsCount(argsMap);

        BigDecimal amount = BigDecimal.valueOf(0.0);
        try {
            amount = BigDecimal.valueOf(Double.parseDouble(argsMap.get(MONEY)));
        } catch (NumberFormatException e) {
            throw new IllegalAmountTypeException(DOUBLE_TYPE, e);
        }
        BigDecimal afterDeposit = storage.depositMoney(amount, user);
        return GSON.toJson(new ServerResponse(GOOD_STATUS,
            "Money deposited current balance: " + afterDeposit, null));
    }
}
