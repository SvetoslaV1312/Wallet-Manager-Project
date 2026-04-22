package bg.sofia.uni.fmi.mjt.uri;

import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Path;

public class URIBuilder {

    private static final String BASE_URL = "https://api-realtime.exrates.coinapi.io/v1/assets";
    private static final String DELIMITER = "/";

    private final Path availableCrypto = Path.of("out", "popular.txt");
    private String assetId;
    private Boolean page;

    public URIBuilder withAssetId(String keyword) {
        this.assetId = keyword;
        return this;
    }

    public URIBuilder withPage(Boolean page) {
        this.page = page;
        return this;
    }

    public URI build() {
        StringBuilder sb = new StringBuilder(BASE_URL);
        if (assetId != null) {
            sb.append(DELIMITER).append(assetId);
        }

        if (page != null) {
            try (FileReader reader = new FileReader(availableCrypto.toString());
            ) {
                String cryptoToLoad = reader.readAllAsString();
                sb.append(DELIMITER).append(cryptoToLoad);
            } catch (IOException e) {
                throw new UncheckedIOException("Problem when loading the cryptos you searched", e);
            }
        }

        return URI.create(sb.toString());
    }

    public static URI buildURI(String assetId, Boolean page) {
        URIBuilder builder = new URIBuilder();
        if (assetId != null) {
            builder.withAssetId(assetId);
        }
        if (page) {
            builder.withPage(true);
        }

        return builder.build();
    }

}
