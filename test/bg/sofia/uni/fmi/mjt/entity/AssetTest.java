package bg.sofia.uni.fmi.mjt.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class AssetTest {
    @Test
    void hashCodeReturnsSameHashForDifferentNamesSameId() {
        var assets = setUpAssetsForHashAndEquals();
        Assertions.assertEquals(assets.get(0).hashCode(), assets.get(1).hashCode(),
            "Expected to be compared by id and type only for identifiers.");

    }

    @Test
    void equalsReturnsTrueForDifferentNamesSameID() {
        var assets = setUpAssetsForHashAndEquals();
        Assertions.assertEquals(assets.get(0), assets.get(1),
            "Expected to equal by id and type for identifiers.");

    }

    private List<Asset> setUpAssetsForHashAndEquals() {
        Asset a1 = new Asset("BTC", "Bitcoin", 1, null, null, null, null,
            null, null, null, null, null, null,
            null, null, null, null, null, null, null, null);

        Asset a2 = new Asset("BTC", "BitcoinV2", 1, null, null, null, null,
            null, null, null, null, null, null,
            null, null, null, null, null, null, null, null);
        return List.of(a1, a2);
    }

}
