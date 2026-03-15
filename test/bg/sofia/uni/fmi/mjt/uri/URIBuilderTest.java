package bg.sofia.uni.fmi.mjt.uri;

import bg.sofia.uni.fmi.mjt.uri.URIBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;

public class URIBuilderTest {
    private static URIBuilder uriBuilder;

    @BeforeEach
    void setUp() {
        uriBuilder = new URIBuilder();
    }

    @Test
    void testWithIdInitializesAfterBuild() {
        String id = "BTC";
        uriBuilder.withAssetId(id);
        Assertions.assertTrue(uriBuilder.build().toString().contains(id),
            "Expected the URI to contain the specified id");
    }

    @Test
    void testWithPageAfterBuild() {
        uriBuilder.withPage(true);
        Assertions.assertTrue(uriBuilder.build().toString().contains("BTC"),
            "Expected the URI to contain the specified id");
        Assertions.assertTrue(uriBuilder.build().toString().contains("ETH"),
            "Expected the URI to contain the specified id");
    }

    @Test
    void testBuildURIWithIDAppendsToURI()   {
        String id = "tech";
        URI uri = URIBuilder.buildURI(id , false);
        Assertions.assertTrue(uri.toString().contains("/" + id),
            "Expected not to append category parameter to the uri.");

    }

    @Test
    void testBuildURIWithPagesAppendsToURI()   {
        URI uri = URIBuilder.buildURI(null , true);
        Assertions.assertTrue(uri.toString().contains("BTC;"),
            "Expected not to append category parameter to the uri as selected important");
    }

}
