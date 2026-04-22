package bg.sofia.uni.fmi.mjt.utility;

import bg.sofia.uni.fmi.mjt.repository.WalletManagerRepositoryDB;
import bg.sofia.uni.fmi.mjt.server.WalletManagerServer;
import bg.sofia.uni.fmi.mjt.cache.CacheCrypto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UncaughtExceptionHandlerTest {
    @TempDir
    Path tempDir;

    private ByteArrayOutputStream out;
    private Path file;

    private final WalletManagerRepositoryDB repo = mock();
    private final CacheCrypto cache = mock();
    private final WalletManagerServer server = mock();
    @BeforeEach
    void setUp() {
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        file = tempDir.resolve("test.txt");

        //doNothing().when(repo).saveUsersToDataBase(any());
        when(repo.cachedCryptoCurrency()).thenReturn(cache);
        doNothing().when(cache).saveCache(any());
        doNothing().when(server).stop();
    }

    @Test
    void testSetThreadToHandleExceptionBeforeTerminationCrashMessageIsWrittenToConsole() throws InterruptedException {
        serverTermination(repo);
        String content = out.toString();
        assertTrue(content.contains("ROOT MESSAGE"), "Expected the user to see the error caused");
    }

    @Test
    void testSetThreadToHandleExceptionBeforeClientTerminationCrashMessageIsWrittenToConsole()
        throws InterruptedException {
        clientTermination();
        String content = out.toString();
        assertTrue(content.contains("ROOT MESSAGE"), "Expected the user to see the error caused");
    }

    @Test
    void testSetThreadToHandleExceptionBeforeTerminationCrashMessageIsWrittenToFile() throws Exception {
        serverTermination(repo);
        String content = Files.readString(file);
        assertTrue(content.contains("Message"), "Expected to have specific Message parameter for file save");
        assertTrue(content.contains("Timestamp"), "Expected to have specific Timestamp parameter for file save");
    }

    private void serverTermination(WalletManagerRepositoryDB repo) throws InterruptedException {
        UncaughtExceptionHandler.setThreadToHandleExceptionBeforeTermination(repo.cachedCryptoCurrency(), file, server);

        Thread t = new Thread(() -> {
            throw new RuntimeException("ROOT MESSAGE");
        });

        t.start();
        t.join();
    }

    private void clientTermination() throws InterruptedException {
        UncaughtExceptionHandler.setThreadToHandleExceptionBeforeClientTermination(file);

        Thread t = new Thread(() -> {
            throw new RuntimeException("ROOT MESSAGE");
        });

        t.start();
        t.join();
    }

}
