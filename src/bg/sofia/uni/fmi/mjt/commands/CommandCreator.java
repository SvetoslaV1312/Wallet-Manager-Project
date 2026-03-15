package bg.sofia.uni.fmi.mjt.commands;

import bg.sofia.uni.fmi.mjt.exceptions.app.command.UnknownCommand;

import java.util.Arrays;
import java.util.List;

public class CommandCreator {
    private static final int ARGS_START_TOKEN_IDX = 1;
    public static final String COMMAND_MESSAGE_FORMAT = "Command %s is unknown try $ help for command names.";

    private static List<String> getCommandArguments(String input) {
        return Arrays.stream(input.split(" ")).toList();
    }

    public static CommandInterface newCommand(String clientInput, String user) throws UnknownCommand {
        List<String> tokens = CommandCreator.getCommandArguments(clientInput);
        String commandName = tokens.getFirst();
        List<String> args = tokens.subList(ARGS_START_TOKEN_IDX, tokens.size());
        for (var current : CommandEnum.values()) {
            if (current.commandName().equals(commandName)) {
                return current.createCommand(args, user);
            }
        }
        throw new UnknownCommand(String.format(COMMAND_MESSAGE_FORMAT, commandName));
    }
}