package bg.sofia.uni.fmi.mjt.cache;

import bg.sofia.uni.fmi.mjt.entity.LocalDateTimeAdapter;
import bg.sofia.uni.fmi.mjt.exceptions.api.ApiExecutionException;
import bg.sofia.uni.fmi.mjt.exceptions.api.ApiKeyLimitExceededException;
import bg.sofia.uni.fmi.mjt.exceptions.api.BadRequestException;
import bg.sofia.uni.fmi.mjt.exceptions.api.CryptoNotFoundException;
import bg.sofia.uni.fmi.mjt.exceptions.api.DataUnavailableException;
import bg.sofia.uni.fmi.mjt.exceptions.api.InsufficientPermissions;
import bg.sofia.uni.fmi.mjt.exceptions.api.UnauthorizedKeyException;
import bg.sofia.uni.fmi.mjt.retriever.CryptoCurrencyRetriever;
import bg.sofia.uni.fmi.mjt.entity.Asset;
import bg.sofia.uni.fmi.mjt.entity.CachedEntity;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static bg.sofia.uni.fmi.mjt.exceptions.APIExceptionPropagate.joinOrPropagate;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CacheCrypto {
    private static final int MAX_SIZE = 20;
    private static final int VALID_PERIOD = 108_000_000;
    private static final int MILLIS_TO_SECONDS = 1000;
    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    private static final float LOAD_FACTOR = 0.75f;
    private static final boolean ACCESS_ORDER = true;
    private static final int BUFFER_SIZE = 512;
    private Map<String, CachedEntity> cache;

    private final ReentrantReadWriteLock lock;

    private final CryptoCurrencyRetriever cryptoCurrencyRetriever;

    public CacheCrypto(Reader loadFrom, CryptoCurrencyRetriever cryptoCurrencyRetriever) {
        this.cache = new LinkedHashMap<>(MAX_SIZE, LOAD_FACTOR, ACCESS_ORDER);
        this.lock = new ReentrantReadWriteLock();
        this.cryptoCurrencyRetriever = cryptoCurrencyRetriever;
        readCacheFromFile(loadFrom);
    }

    public Asset get(String cryptoCurrency)
        throws ApiExecutionException {
        long now = System.currentTimeMillis() / MILLIS_TO_SECONDS;
        lock.readLock().lock();
        try {
            CachedEntity entry = cache.get(cryptoCurrency);
            if (entry != null && now - entry.timeInMilis() <= VALID_PERIOD) {
                return entry.asset();
            }
        } finally {
            lock.readLock().unlock();
        }
        Asset result = getAsset(cryptoCurrency);
        lock.writeLock().lock();
        try {
            cache.put(cryptoCurrency, new CachedEntity(now, result));
            evictIfNeeded();
        } finally {
            lock.writeLock().unlock();
        }
        return result;
    }

    public Asset[] getAllFrequentAssets()
            throws DataUnavailableException, InsufficientPermissions, BadRequestException,
            UnauthorizedKeyException, ApiKeyLimitExceededException {
        return joinOrPropagate(cryptoCurrencyRetriever.getCryptoCurrencyAsync(null, ACCESS_ORDER));
    }

    public void putMultiple(List<Asset> assets) {
        long now = System.currentTimeMillis() / MILLIS_TO_SECONDS;

        lock.writeLock().lock();
        try {
            for (Asset asset : assets) {
                if (asset.price_usd() == 0.0) continue;
                cache.put(asset.name(), new CachedEntity(now, asset));
                evictIfNeeded();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void saveCache(Writer writer) {
        cryptoCurrencyRetriever.shutdownExecutors();
        writeCacheToFile(writer);
    }

    private Asset getAsset(String cryptoCurrency)
        throws ApiKeyLimitExceededException, DataUnavailableException, BadRequestException, InsufficientPermissions,
        UnauthorizedKeyException, CryptoNotFoundException {
        Asset[] array = joinOrPropagate(
            cryptoCurrencyRetriever.getCryptoCurrencyAsync(cryptoCurrency, false));
        if (array == null || array.length == 0) {
            throw new CryptoNotFoundException("No asset found for: " + cryptoCurrency);
        }
        return array[0];
    }

    public void readCacheFromFile(Reader reader) {
        var bufferedReader = new BufferedReader(reader, BUFFER_SIZE);
        lock.writeLock().lock();
        try {
            String json = readAll(bufferedReader);
            Type type = new TypeToken<ConcurrentHashMap<String, CachedEntity>>() {
            }.getType();
            Map<String, CachedEntity> readCache = GSON.fromJson(json, type);

            if (readCache == null) {
                cache = new ConcurrentHashMap<>();
            } else {
                cache = new ConcurrentHashMap<>(readCache);
            }

        } catch (IOException e) {
            cache = new ConcurrentHashMap<>();
        } finally {
            lock.writeLock().unlock();
        }
    }

    private String readAll(BufferedReader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        return sb.toString();
    }

    private void writeCacheToFile(Writer writer) {
        var bufferedWriter = new BufferedWriter(writer, BUFFER_SIZE);
        try {
            bufferedWriter.write(GSON.toJson(cache));
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new UncheckedIOException("A problem occurred while writing to a file", e);
        }
    }

    private void evictIfNeeded() {
        if (cache.size() > MAX_SIZE) {
            Iterator<String> it = cache.keySet().iterator();
            it.next();
            it.remove();
        }
    }
}

