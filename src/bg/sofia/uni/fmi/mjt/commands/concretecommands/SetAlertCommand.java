package bg.sofia.uni.fmi.mjt.commands.concretecommands;

import bg.sofia.uni.fmi.mjt.commands.Command;
import bg.sofia.uni.fmi.mjt.entity.User;
import bg.sofia.uni.fmi.mjt.exceptions.api.ApiExecutionException;
import bg.sofia.uni.fmi.mjt.exceptions.app.AppExecutionException;
import bg.sofia.uni.fmi.mjt.exceptions.app.command.InvalidCommandArgumentCount;
import bg.sofia.uni.fmi.mjt.exceptions.app.command.InvalidCommandFormat;
import bg.sofia.uni.fmi.mjt.exceptions.app.repository.DataAcessException;
import bg.sofia.uni.fmi.mjt.exceptions.app.wallet.IllegalAmountTypeException;
import bg.sofia.uni.fmi.mjt.repository.WalletManagerRepositoryDB;
import bg.sofia.uni.fmi.mjt.response.ServerResponse;
import bg.sofia.uni.fmi.mjt.utility.ArgumentParser;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class SetAlertCommand extends Command {
    private static final int ARGUMENTS_COUNT = 2;
    private static final String OFFERING = "offering";
    private static final String PRICE = "price";

    public SetAlertCommand(List<String> arguments, User user) {
        super(arguments, user);
    }

    @Override
    public void checkArgumentsCount(Map<String, String> argsMap)
            throws InvalidCommandArgumentCount, InvalidCommandFormat {
        if (argsMap.size() < ARGUMENTS_COUNT) {
            throw new InvalidCommandArgumentCount("Invalid argument count try command $ help for all commands");
        }
        if (!argsMap.containsKey(OFFERING) || !argsMap.containsKey(PRICE)) {
            throw new InvalidCommandFormat("The command must contain the mandatory fields" +
                    " offering and price");
        }
    }

    @Override
    public String execute(WalletManagerRepositoryDB storage)
            throws AppExecutionException, ApiExecutionException,
            DataAcessException {
        isValidSessionPresent();
        var argsMap = ArgumentParser.parseString(arguments);
        checkArgumentsCount(argsMap);

        BigDecimal amount;
        try {
            amount = BigDecimal.valueOf(Double.parseDouble(argsMap.get(PRICE)));
        } catch (NumberFormatException e) {
            throw new IllegalAmountTypeException(DOUBLE_TYPE, e);
        }
        storage.setPriceAlert(user, argsMap.get(OFFERING), amount);
        return GSON.toJson(new ServerResponse(GOOD_STATUS, "Set notification to the price", user));

    }
}
