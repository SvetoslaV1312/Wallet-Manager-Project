package bg.sofia.uni.fmi.mjt.alerts;

import java.math.BigDecimal;

public record CryptoPriceUpdateEvent(String crypto, BigDecimal price) implements Event {
}

