package bg.sofia.uni.fmi.mjt.thread;

import bg.sofia.uni.fmi.mjt.commands.CommandCreator;
import bg.sofia.uni.fmi.mjt.commands.CommandExecutor;
import bg.sofia.uni.fmi.mjt.exceptions.app.AppExecutionException;
import bg.sofia.uni.fmi.mjt.exceptions.api.ApiExecutionException;
import bg.sofia.uni.fmi.mjt.repository.WalletManagerRepository;
import bg.sofia.uni.fmi.mjt.response.ServerResponse;
import com.google.gson.Gson;

import java.util.concurrent.Callable;

public class CommandWorkerThread implements Callable<String> {
    private final CommandExecutor commandExecutor;
    private static final Gson GSON = new Gson();
    private static final String STATUS_BAD = "BAD";
    private final String clientInput;
    private final String user;

    public CommandWorkerThread(String clientInput, String user, WalletManagerRepository storage) {
        this.clientInput = clientInput;
        this.user = user;
        commandExecutor = new CommandExecutor(storage);
    }

    @Override
    public String call() {
        String output;
        try {
            return  commandExecutor.execute(
                CommandCreator.newCommand(clientInput, user));
        } catch (AppExecutionException | ApiExecutionException e) {
            output = GSON.toJson(new ServerResponse(STATUS_BAD, e.getMessage(), user));
        }
        return output;
    }
}
