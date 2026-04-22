package bg.sofia.uni.fmi.mjt.cache;

import bg.sofia.uni.fmi.mjt.alerts.EventDispatcher;
import bg.sofia.uni.fmi.mjt.entity.Asset;
import bg.sofia.uni.fmi.mjt.entity.ChainAddress;
import bg.sofia.uni.fmi.mjt.entity.Pair;
import bg.sofia.uni.fmi.mjt.exceptions.APIExceptionPropagate;
import bg.sofia.uni.fmi.mjt.exceptions.api.CryptoNotFoundException;
import bg.sofia.uni.fmi.mjt.retriever.CryptoCurrencyRetriever;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import java.io.*;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import static bg.sofia.uni.fmi.mjt.exceptions.APIExceptionPropagate.joinOrPropagate;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class CacheCryptoTest {
    @TempDir
    Path temp;

    private CacheCrypto cache;
    @Mock
    private CryptoCurrencyRetriever mockRetriever = mock();
    @Mock
    private EventDispatcher eventDispatcher = mock();

    @BeforeEach
    void setup() {
        Reader reader = new StringReader("");
        cache = new CacheCrypto(reader, mockRetriever, eventDispatcher);
    }

    @Test
    void testGetReturnsCachedValueWhenPresentInMemory() throws Exception {
        Asset asset = initializeAssetWithName("Test");
        Asset[] arr = {asset};

        CompletableFuture<Asset[]> future = CompletableFuture.completedFuture(arr);
        when(mockRetriever.getCryptoCurrencyAsync("BTC", false)).thenReturn(future);

        cache.get("BTC");

        Asset result = cache.get("BTC");

        assertEquals(asset, result, "Expected the results of the future when called again to equal exactly");
    }

    @Test
    void testGetFetchesFromRetrieverWhenNoCacheAvailable() throws Exception {
        Asset asset = initializeAssetWithName("Test");
        Asset[] arr = {asset};

        try (MockedStatic<APIExceptionPropagate> mocked = mockStatic(APIExceptionPropagate.class)) {

            when(mockRetriever.getCryptoCurrencyAsync("BTC", false)).thenReturn(null);
            mocked.when(() -> joinOrPropagate(null)).thenReturn(arr);

            Asset result = cache.get("BTC");

            assertEquals(asset, result, "Expected when not available in memory to get it from the retriver");
        }
    }

    @Test
    void testGetThrowsWhenRetrieverReturnsEmpty() {
        try (MockedStatic<APIExceptionPropagate> mocked = mockStatic(APIExceptionPropagate.class)) {

            mocked.when(() -> joinOrPropagate(any())).thenReturn(new Asset[0]);

            assertThrows(CryptoNotFoundException.class, () -> cache.get("BTC"), "When an empty asset is return expected exception");
        }
    }

    @Test
    void testPutMultipleStoresAssets() throws  Exception {
        Asset a1 = initializeAssetWithName("Test1");

        Asset a2 = initializeAssetWithName("Test2");

        cache.putMultiple(List.of(a1, a2));

        assertDoesNotThrow(() -> cache.get("Test1"), "Expected the asset to be stored in the cache");
        assertDoesNotThrow(() -> cache.get("Test2"), "Expected the asset to be stored in the cache");

        cache.get("Test1");
        verify(mockRetriever, never()).getCryptoCurrencyAsync("Test1", false);
    }

    @Test
    void testReadCacheLoadsValidData() throws Exception {
        Asset asset = initializeAssetWithName("Test");

        ConcurrentHashMap<String, Pair<Long, Asset>> map = new ConcurrentHashMap<>();
        map.put(asset.name(), new Pair<>(100000L, asset));

        cache.putMultiple(List.of(asset));
        var w = new StringWriter();
        cache.saveCache(w);
        var r = new StringReader(w.getBuffer().toString());
        CacheCrypto underTestCache = new CacheCrypto(r, mockRetriever, eventDispatcher);

        assertEquals("Test", underTestCache.get("Test").name(), "Expected after loading file" +
                " to retrieve it from memory");
    }

    @Test
    void testReadCacheThrowsUncheckedIOException() throws IOException {
        Reader failingReader = mock(Reader.class);

        doThrow(new IOException("Simulated IO error"))
                .when(failingReader)
                .read(any(char[].class), anyInt(), anyInt());

        assertDoesNotThrow(() -> new CacheCrypto(failingReader, mockRetriever, eventDispatcher),
                "Expected nothing to be thrown and an empty db to be initialized"
        );
    }

    @Test
    void testWriteUsersToFileV2ThrowsUncheckedIOException() throws IOException {
        Writer failingWriter = mock(Writer.class);

        doThrow(new IOException("Simulated IO error"))
                .when(failingWriter)
                .write(any(char[].class), anyInt(), anyInt());

        assertThrows(UncheckedIOException.class,
                () -> cache.saveCache(failingWriter),
                "Expected the Io exception to be wrapped"
        );
    }

    private Asset initializeAssetWithName(String name) {
        return new Asset("Test",
                name,
                1,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                1000L,
                1.0,
                1.0,
                1.0,
                1.0,
                null,
                1.0,
                1.0,
                1.0,
                new ChainAddress[0],
                "today",
                "tommorrow"
        );
    }



}
