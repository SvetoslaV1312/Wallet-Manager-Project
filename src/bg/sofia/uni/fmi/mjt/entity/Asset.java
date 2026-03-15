package bg.sofia.uni.fmi.mjt.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public record Asset(String asset_id, String name, Integer type_is_crypto, LocalDateTime data_quote_start,
                    LocalDateTime data_quote_end, LocalDateTime data_orderbook_start, LocalDateTime data_orderbook_end,
                    LocalDateTime data_trade_start, LocalDateTime data_trade_end, Long data_symbols_count,
                    Double volume_1hrs_usd,
                    Double volume_1day_usd, Double volume_1mth_usd, Double price_usd,
                    UUID id_icon, Double supply_current,
                    Double supply_total, Double supply_max, ChainAddress[] chain_addresses, String data_start,
                    String data_end) implements Serializable {

    private static final String EQUALS_SIGN = "=";
    private static final String ASSET_ID = "  asset_id";
    private static final String NAME = "  name";
    private static final String PRICE_USD = "  price_usd";
    private static final String SUPPLY_CURRENT = "  supply_current";
    private static final String SUPPLY_TOTAL = "  supply_total";
    private static final String SUPPLY_MAX = "  supply_max";
    private static final String CHAIN_ADDRESSES = "  chain_addresses=";
    private static final String END_DELIMITER = "}";
    private static final String ASSET_OPENER = "Asset{";

    @Override
    public String toString() {
        String ln = System.lineSeparator();
        StringBuilder sb = new StringBuilder(ASSET_OPENER + ln);

        append(sb, ASSET_ID, asset_id, ln);
        append(sb, NAME, name, ln);
        append(sb, PRICE_USD, price_usd, ln);
        append(sb, SUPPLY_CURRENT, supply_current, ln);
        append(sb, SUPPLY_TOTAL, supply_total, ln);
        append(sb, SUPPLY_MAX, supply_max, ln);

        if (chain_addresses != null) {
            sb.append(CHAIN_ADDRESSES)
                .append(Arrays.toString(chain_addresses))
                .append(ln);
        }

        sb.append(END_DELIMITER);
        return sb.toString();
    }

    private void append(StringBuilder sb, String name, Object value, String ln) {
        if (value != null) {
            sb.append(name).append(EQUALS_SIGN).append(value).append(ln);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Asset asset = (Asset) o;
        return asset_id().equals(asset.asset_id()) && Objects.equals(type_is_crypto(), asset.type_is_crypto());
    }

    @Override
    public int hashCode() {
        int result = asset_id().hashCode();
        result = 31 * result + Objects.hashCode(type_is_crypto());
        return result;
    }

}
