package bg.sofia.uni.fmi.mjt.thread;

import bg.sofia.uni.fmi.mjt.repository.WalletManagerRepository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.file.Path;

import static bg.sofia.uni.fmi.mjt.utility.FileHandler.intializeWriter;

public class SaveDBThread implements  Runnable {
    private static final Path DB_FILE_PATH = Path.of("out", "data.txt");
    private static final Path CACHE_FILE_PATH = Path.of("out", "cache.txt");
    private final WalletManagerRepository repository;

    public SaveDBThread(WalletManagerRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run() {
        try (Writer usersWriter = intializeWriter(DB_FILE_PATH);
             Writer cacheWriter = intializeWriter(CACHE_FILE_PATH)) {
            repository.saveUsersToDataBase(usersWriter);
            repository.cachedCryptoCurrency().saveCache(cacheWriter);
        } catch (IOException e) {
            throw new UncheckedIOException("Server aborted because of a problem when saving information", e);
        }
    }
}
