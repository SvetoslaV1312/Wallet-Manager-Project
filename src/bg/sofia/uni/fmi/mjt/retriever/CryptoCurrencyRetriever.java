package bg.sofia.uni.fmi.mjt.retriever;

import bg.sofia.uni.fmi.mjt.apikey.ApiKeyProvider;
import bg.sofia.uni.fmi.mjt.apikey.ApiKeyProviderEnvironmentImpl;
import bg.sofia.uni.fmi.mjt.entity.Asset;
import bg.sofia.uni.fmi.mjt.entity.LocalDateTimeAdapter;
import bg.sofia.uni.fmi.mjt.exceptions.ExceptionFactory;
import bg.sofia.uni.fmi.mjt.uri.URIBuilder;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CryptoCurrencyRetriever {
    private static final Gson GSON = new GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
        .create();
    private static  final ExecutorService IO_EXECUTOR = Executors.newCachedThreadPool();
    private static final int DEFAULT_THREAD_POOL_SIZE = 10;
    private  final ExecutorService executorApp;
    private final HttpClient client;
    private final String apiKey;

    public CryptoCurrencyRetriever(HttpClient client,   ApiKeyProvider apiKeyProvider) {
        this.client = client;
        this.apiKey = apiKeyProvider.getApikey();
        executorApp = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);
    }

    public CryptoCurrencyRetriever(HttpClient client,   ApiKeyProvider apiKeyProvider, ExecutorService executor) {
        this.client = client;
        this.apiKey = apiKeyProvider.getApikey();
        executorApp = executor;
    }

    public static CryptoCurrencyRetriever createDefault() {
        HttpClient client = HttpClient.newBuilder()
            .executor(IO_EXECUTOR)
            .build();

        return new CryptoCurrencyRetriever(client, new ApiKeyProviderEnvironmentImpl());
    }

    public CompletableFuture<Asset[]> getCryptoCurrencyAsync(String cryptoCurrency, Boolean pages) {
        URI uri = URIBuilder.buildURI(cryptoCurrency, pages);
        HttpRequest request = HttpRequest.newBuilder(uri).header("X-CoinAPI-Key", apiKey).GET().build();

        return client
            .sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApplyAsync(response -> {
                Throwable error = ExceptionFactory.of(response.statusCode(), response.body());
                if (error != null) {
                    throw new CompletionException("Async operation failed", error);
                }
                return response.body();

            })
            .thenApplyAsync(json -> GSON.fromJson(json, Asset[].class), executorApp);

    }

    public void shutdownExecutors() {
        IO_EXECUTOR.shutdown();
        executorApp.shutdown();
    }

}
