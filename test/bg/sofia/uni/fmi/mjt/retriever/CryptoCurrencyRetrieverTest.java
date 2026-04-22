package bg.sofia.uni.fmi.mjt.retriever;

import bg.sofia.uni.fmi.mjt.entity.Asset;
import bg.sofia.uni.fmi.mjt.entity.LocalDateTimeAdapter;
import bg.sofia.uni.fmi.mjt.exceptions.api.ApiKeyLimitExceededException;
import bg.sofia.uni.fmi.mjt.exceptions.api.BadRequestException;
import bg.sofia.uni.fmi.mjt.exceptions.api.DataUnavailableException;
import bg.sofia.uni.fmi.mjt.exceptions.api.InsufficientPermissions;
import bg.sofia.uni.fmi.mjt.exceptions.api.UnauthorizedKeyException;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CryptoCurrencyRetrieverTest {
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static final  ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    @Mock
    HttpClient mockedClient = mock();

    private CryptoCurrencyRetriever retriever;
    @BeforeEach
    void setUp() {
        retriever = new CryptoCurrencyRetriever(mockedClient, () -> "apiKey", EXECUTOR);
    }

    @Test
    void testCreateDefaultReturnsNonNullRetriever() {
        CryptoCurrencyRetriever retriever = CryptoCurrencyRetriever.createDefault();
        Assertions.assertNotNull(retriever, "Expected the retriever for default constructor to be not null");
    }

    @Test
    void testGetCryptoCurrencyAsyncThrowsExceptionWithStatusCodeBadRequest() {
        setUpStatusCodeOnCall(400);
        Exception ex = Assertions.assertThrows(CompletionException.class,
            () -> retriever.getCryptoCurrencyAsync("test", false).join()
        );

        Throwable cause = unwrap(ex);
        Assertions.assertInstanceOf(BadRequestException.class, cause,
            "Expected an exception to be thrown with this specific error code");
    }

    @Test
    void testGetCryptoCurrencyAsyncThrowsExceptionWithUnauthorizedKey() {
        setUpStatusCodeOnCall(401);
        Exception ex = Assertions.assertThrows(CompletionException.class,
            () -> retriever.getCryptoCurrencyAsync("test", false).join()
        );

        Throwable cause = unwrap(ex);
        Assertions.assertInstanceOf(UnauthorizedKeyException.class, cause,
            "Expected an exception to be thrown with this specific error code");
    }

    @Test
    void testGetCryptoCurrencyAsyncThrowsExceptionWithInsufficientPermissions() {
        setUpStatusCodeOnCall(403);
        Exception ex = Assertions.assertThrows(CompletionException.class,
            () -> retriever.getCryptoCurrencyAsync("test", false).join()
        );

        Throwable cause = unwrap(ex);
        Assertions.assertInstanceOf(InsufficientPermissions.class, cause,
            "Expected an exception to be thrown with this specific error code");
    }

    @Test
    void testGetCryptoCurrencyAsyncThrowsExceptionWithApiKeyLimitExceededException() {
        setUpStatusCodeOnCall(429);
        Exception ex = Assertions.assertThrows(CompletionException.class,
            () -> retriever.getCryptoCurrencyAsync("test", false).join()
        );

        Throwable cause = unwrap(ex);
        Assertions.assertInstanceOf(ApiKeyLimitExceededException.class, cause,
            "Expected an exception to be thrown with this specific error code");
    }

    @Test
    void testGetCryptoCurrencyAsyncThrowsExceptionWithinServerError() {
        setUpStatusCodeOnCall(550);
        Exception ex = Assertions.assertThrows(CompletionException.class,
            () -> retriever.getCryptoCurrencyAsync("test", false).join()
        );

        Throwable cause = unwrap(ex);
        Assertions.assertInstanceOf(DataUnavailableException.class, cause,
            "Expected an exception to be thrown with this specific error code");
    }

    @Test
    void testGetCryptoCurrencyAsyncReturnsAnArrayOfAssets() {
        Asset[] assets = setUpAsset();
        HttpResponse<String> mockResponse = mock();
        when(mockResponse.statusCode()).thenReturn(200);
        Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
        String json = gson.toJson(assets);
        when(mockResponse.body()).thenReturn(json);

        CompletableFuture<HttpResponse<String>> future =
            CompletableFuture.completedFuture(mockResponse);
        //след type erasure CompletableFuture<HttpResponse<String>> става просто CompletableFuture
        //Mockito приема raw типове -> raw cast, защото generics не съществуват runtime
        when(mockedClient.sendAsync(any(), any())).thenReturn((CompletableFuture) future);

        Assertions.assertArrayEquals(assets, retriever.getCryptoCurrencyAsync("any", true).join(),
            "Expected the retriever to parse and return the same asset array");
    }

    @Test
    void testShutdownExecutorsThrowsExceptionAfterClosedWithNewTasks() {
        CryptoCurrencyRetriever underTest = CryptoCurrencyRetriever.createDefault();
        underTest.shutdownExecutors();

        Assertions.assertThrows(RejectedExecutionException.class, () -> underTest.getCryptoCurrencyAsync("test", false).join(),
            "Expected an exception to be thrown because executors have been closed");

    }

    private Asset[] setUpAsset() {
        Asset a1 = new Asset("BTC", "Bitcoin", 1, null, null, null, null,
            null, null, null, null, null, null,
            null, null, null, null, null, null, null, null);

        Asset a2 = new Asset("BTC", "BitcoinV2", 1, null, null, null, null,
            null, null, null, null, null, null,
            null, null, null, null, null, null, null, null);
        return new Asset[] {a1, a2};
    }

    void setUpStatusCodeOnCall(int statusCode) {
        HttpResponse<String> mockResponse = mock();
        when(mockResponse.statusCode()).thenReturn(statusCode);
        when(mockResponse.body()).thenReturn("{" + LINE_SEPARATOR +
            "    \"error\": \"test\"" + LINE_SEPARATOR +
            LINE_SEPARATOR +
            "}");

        CompletableFuture<HttpResponse<String>> future =
            CompletableFuture.completedFuture(mockResponse);

        when(mockedClient.sendAsync(any(), any())).thenReturn((CompletableFuture) future);
    }

    public Throwable unwrap(Throwable t) {
        while (t.getCause() != null) {
            t = t.getCause();
        }
        return t;
    }
}
