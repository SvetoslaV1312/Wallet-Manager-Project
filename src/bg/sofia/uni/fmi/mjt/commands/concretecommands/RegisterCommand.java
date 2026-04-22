package bg.sofia.uni.fmi.mjt.commands.concretecommands;

import bg.sofia.uni.fmi.mjt.commands.Command;
import bg.sofia.uni.fmi.mjt.entity.User;
import bg.sofia.uni.fmi.mjt.exceptions.app.AppExecutionException;
import bg.sofia.uni.fmi.mjt.exceptions.app.command.InvalidCommandFormat;
import bg.sofia.uni.fmi.mjt.exceptions.app.repository.DataAcessException;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.IllegalNameException;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.IllegalPasswordException;
import bg.sofia.uni.fmi.mjt.repository.WalletManagerRepositoryDB;
import bg.sofia.uni.fmi.mjt.response.ServerResponse;
import bg.sofia.uni.fmi.mjt.exceptions.app.command.InvalidCommandArgumentCount;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.UserAlreadyExistsInDB;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.UserAlreadyLoggedIn;
import bg.sofia.uni.fmi.mjt.utility.ArgumentParser;

import java.util.List;
import java.util.Map;

public class RegisterCommand extends Command {
    private static final int ARGUMENTS_COUNT = 2;
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    public RegisterCommand(List<String> arguments, User user) {
        super(arguments, user);
    }

    @Override
    public void checkArgumentsCount(Map<String, String> argsMap)
            throws InvalidCommandArgumentCount, InvalidCommandFormat {
        if (argsMap.size() > ARGUMENTS_COUNT) {
            throw new InvalidCommandArgumentCount("Invalid argument count try command $ help for all commands");
        }
        if (!argsMap.containsKey(USERNAME) || !argsMap.containsKey(PASSWORD)) {
            throw new InvalidCommandFormat("The command must contain the mandatory fields");
        }
    }

    @Override
    public String execute(WalletManagerRepositoryDB storage)
            throws AppExecutionException,
            DataAcessException {
        checkIsUserLoggedIn();
        var argsMap = ArgumentParser.parseString(arguments);
        checkArgumentsCount(argsMap);
        storage.registerUser(argsMap.get(USERNAME), argsMap.get(PASSWORD));
        return GSON.toJson(new ServerResponse(GOOD_STATUS, "User registered", null));
    }
}
