package bg.sofia.uni.fmi.mjt.commands.concretecommands;

import bg.sofia.uni.fmi.mjt.commands.Command;
import bg.sofia.uni.fmi.mjt.commands.CommandEnum;
import bg.sofia.uni.fmi.mjt.entity.User;
import bg.sofia.uni.fmi.mjt.exceptions.app.command.InvalidCommandFormat;
import bg.sofia.uni.fmi.mjt.repository.WalletManagerRepositoryDB;
import bg.sofia.uni.fmi.mjt.response.ServerResponse;
import bg.sofia.uni.fmi.mjt.exceptions.app.command.InvalidCommandArgumentCount;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.NoUserLoggedIn;
import bg.sofia.uni.fmi.mjt.utility.ArgumentParser;

import java.util.List;
import java.util.Map;

public class HelpCommand extends Command {
    private static final String COMMANDS = "List of commands: ";
    private static final int ARGS_COUNT = 0;

    public HelpCommand(List<String> arguments, User user) {
        super(arguments, user);
    }

    @Override
    public void checkArgumentsCount(Map<String, String> argsMap) throws
            InvalidCommandArgumentCount, InvalidCommandFormat {
        if (argsMap.size() != ARGS_COUNT) {
            throw new InvalidCommandArgumentCount("Invalid argument count try command $ help for all commands");
        }
    }

    @Override
    public String execute(WalletManagerRepositoryDB storage)
            throws NoUserLoggedIn, InvalidCommandArgumentCount, InvalidCommandFormat {
        var argsMap = ArgumentParser.parseString(arguments);
        checkArgumentsCount(argsMap);

        StringBuilder sb = new StringBuilder(COMMANDS + System.lineSeparator());
        for (var element : CommandEnum.values()) {
            sb.append(element.commandName()).append(System.lineSeparator());
        }
        return GSON.toJson(new ServerResponse(GOOD_STATUS,
            sb.toString(), user));
    }
}

