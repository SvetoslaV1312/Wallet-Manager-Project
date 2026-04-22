package bg.sofia.uni.fmi.mjt.commands.concretecommands;

import bg.sofia.uni.fmi.mjt.commands.Command;
import bg.sofia.uni.fmi.mjt.entity.User;
import bg.sofia.uni.fmi.mjt.exceptions.api.ApiExecutionException;
import bg.sofia.uni.fmi.mjt.exceptions.app.AppExecutionException;
import bg.sofia.uni.fmi.mjt.exceptions.app.command.InvalidCommandArgumentCount;
import bg.sofia.uni.fmi.mjt.exceptions.app.command.InvalidCommandFormat;
import bg.sofia.uni.fmi.mjt.exceptions.app.repository.DataAcessException;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.NoUserLoggedIn;
import bg.sofia.uni.fmi.mjt.exceptions.app.wallet.IllegalAmountTypeException;
import bg.sofia.uni.fmi.mjt.repository.WalletManagerRepositoryDB;
import bg.sofia.uni.fmi.mjt.response.ServerResponse;
import bg.sofia.uni.fmi.mjt.utility.ArgumentParser;

import java.util.List;
import java.util.Map;

public class CheckAlertsCommand extends Command {
    private static final int ARGUMENTS_COUNT = 0;

    public CheckAlertsCommand(List<String> arguments, User user) {
        super(arguments, user);
    }

    @Override
    public void checkArgumentsCount(Map<String, String> argsMap) throws
            InvalidCommandArgumentCount, InvalidCommandFormat {
        if (argsMap.size() != ARGUMENTS_COUNT) {
            throw new InvalidCommandArgumentCount("Invalid argument count try command $ help for all commands");
        }
    }

    @Override
    public String execute(WalletManagerRepositoryDB storage)
            throws InvalidCommandArgumentCount, ApiExecutionException,
            InvalidCommandFormat, DataAcessException, NoUserLoggedIn {
        isValidSessionPresent();
        var argsMap = ArgumentParser.parseString(arguments);
        checkArgumentsCount(argsMap);

        String notifications = storage.checkAlerts(user);
        return GSON.toJson(new ServerResponse(GOOD_STATUS,
                "All notifications: " + notifications, user));
    }
}
