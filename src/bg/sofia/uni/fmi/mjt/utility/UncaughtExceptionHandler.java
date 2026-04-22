package bg.sofia.uni.fmi.mjt.utility;

import bg.sofia.uni.fmi.mjt.cache.CacheCrypto;
import bg.sofia.uni.fmi.mjt.server.WalletManagerServer;
import bg.sofia.uni.fmi.mjt.thread.SaveDBThread;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class UncaughtExceptionHandler {

    private static final String MESSAGE = "Message: ";
    private static final String TIMESTAMP = "Timestamp: ";
    private static final String EXCEPTION = "Exception: ";
    private static final String STACKTRACE = "Stacktrace:";

    public static void setThreadToHandleExceptionBeforeTermination(CacheCrypto cacheCrypto,
                                                                   Path crashFile, WalletManagerServer server) {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            defaultSaveToFile(crashFile, throwable);
            Thread savingThread = new Thread(new SaveDBThread(cacheCrypto));
            savingThread.setDaemon(false);
            savingThread.start();
            server.stop();
        });
    }

    public static void setThreadToHandleExceptionBeforeClientTermination(Path crashFile) {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) ->
            defaultSaveToFile(crashFile, throwable));
    }

    private static void defaultSaveToFile(Path crashFile, Throwable throwable) {
        saveCrashReport(throwable, crashFile);
        System.out.println(throwable.getMessage());

    }

    private static void saveCrashReport(Throwable t, Path crashFile) {
        FileHandler.createDirectoryIfAbsent(crashFile);
        FileHandler.createFileIfAbsent(crashFile);
        try (var writer = Files.newBufferedWriter(
            crashFile,
            StandardOpenOption.APPEND)) {

            Throwable root = t;
            do {
                writer.append(MESSAGE).append(root.getMessage()).append(System.lineSeparator());
                root = root.getCause();
            } while (root != null);
            writer.append(TIMESTAMP + java.time.Instant.now() + System.lineSeparator());
            writer.append(EXCEPTION + t.getClass().getName() + System.lineSeparator());
            writer.append(STACKTRACE + System.lineSeparator());
            for (var element : t.getStackTrace()) {
                writer.write("    at " + element + System.lineSeparator());
            }

            writer.append(System.lineSeparator());
        } catch (IOException e) {
            throw new UncheckedIOException("Critical error" +
                " when trying to save error to file.", e);
        }
    }

}
