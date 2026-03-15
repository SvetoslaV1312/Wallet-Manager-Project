package bg.sofia.uni.fmi.mjt.utility;

import bg.sofia.uni.fmi.mjt.exceptions.app.command.InvalidCommandFormat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArgumentParser {
    private static final int ARGS_FORMAT = 2;
    private static final String PARTS_REGEX = "=";
    private static final String REGEX = "--";
    private static final int KEY_IDX = 0;
    private static final int VALUE_IDX = 1;
    public static final String EMPTY_STRING = "";

    public static Map<String, String> parseString(List<String> arguments) throws InvalidCommandFormat {
        Map<String, String> toReturn = new HashMap<>();
        for (String arg : arguments) {
            String s = arg.replace(REGEX, EMPTY_STRING);
            String[] parts = s.split(PARTS_REGEX);
            if (parts.length != ARGS_FORMAT) {
                throw new InvalidCommandFormat("Expected the argument to be formatted as --field=<value>");
            }

            toReturn.put(parts[KEY_IDX].trim(), parts[VALUE_IDX].trim());
        }
        return toReturn;

    }
}
