package bg.sofia.uni.fmi.mjt.commands.concretecommands;

import bg.sofia.uni.fmi.mjt.commands.Command;
import bg.sofia.uni.fmi.mjt.entity.User;
import bg.sofia.uni.fmi.mjt.exceptions.api.ApiExecutionException;
import bg.sofia.uni.fmi.mjt.exceptions.app.command.InvalidCommandFormat;
import bg.sofia.uni.fmi.mjt.repository.WalletManagerRepositoryDB;
import bg.sofia.uni.fmi.mjt.response.ServerResponse;
import bg.sofia.uni.fmi.mjt.exceptions.app.command.InvalidCommandArgumentCount;
import bg.sofia.uni.fmi.mjt.utility.ArgumentParser;

import java.util.List;
import java.util.Map;

public class ListOfferingsCommand extends Command {
    private static final int ARGUMENTS_COUNT = 0;

    public ListOfferingsCommand(List<String> arguments, User user) {
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
            InvalidCommandFormat {
        var argsMap = ArgumentParser.parseString(arguments);
        checkArgumentsCount(argsMap);

        String offerings = storage.listOfferings();
        return GSON.toJson(new ServerResponse(GOOD_STATUS,
            "All available offerings: " + offerings, user));
    }
}
