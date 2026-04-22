package bg.sofia.uni.fmi.mjt.thread;

import bg.sofia.uni.fmi.mjt.cache.CacheCrypto;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.file.Path;

import static bg.sofia.uni.fmi.mjt.utility.FileHandler.intializeWriter;

public class SaveDBThread implements  Runnable {
    private static final Path CACHE_FILE_PATH = Path.of("out", "cache.txt");
    private final CacheCrypto cacheCrypto;

    public SaveDBThread(CacheCrypto cacheCrypto) {
        this.cacheCrypto = cacheCrypto;
    }

    @Override
    public void run() {
        try (Writer cacheWriter = intializeWriter(CACHE_FILE_PATH)) {
            cacheCrypto.saveCache(cacheWriter);
        } catch (IOException e) {
            throw new UncheckedIOException("Server aborted because of a problem when saving information", e);
        }
    }
}
