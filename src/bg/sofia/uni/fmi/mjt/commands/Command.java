package bg.sofia.uni.fmi.mjt.commands;

import bg.sofia.uni.fmi.mjt.exceptions.app.command.InvalidCommandArgumentCount;
import bg.sofia.uni.fmi.mjt.exceptions.app.command.InvalidCommandFormat;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.NoUserLoggedIn;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.UserAlreadyLoggedIn;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

public abstract class Command implements CommandInterface {
    protected static final String GOOD_STATUS = "OK";
    protected static final String DOUBLE_TYPE = "Numbers should only be of double type";
    protected final List<String> arguments;
    protected static final Gson GSON = new Gson();
    private static final String ALREADY_LOGGED_FORMAT = "You are logged in , cant register/login %s";

    protected final String user;

    public Command(List<String> arguments, String user) {
        this.arguments = arguments;
        this.user = user;
    }

    public abstract void checkArgumentsCount(Map<String, String> argsMap)
            throws InvalidCommandArgumentCount, InvalidCommandFormat;

    public void checkIsUserLoggedIn() throws UserAlreadyLoggedIn {
        if (user != null) {
            throw new UserAlreadyLoggedIn(
                String.format(ALREADY_LOGGED_FORMAT, arguments.getFirst()));
        }
    }

    public void isValidSessionPresent() throws NoUserLoggedIn {
        if (user == null) {
            throw new NoUserLoggedIn("Only logged in users can execute this command");
        }
    }
}
