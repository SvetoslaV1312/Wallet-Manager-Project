package bg.sofia.uni.fmi.mjt.alerts;

import bg.sofia.uni.fmi.mjt.entity.SqlQueries;
import bg.sofia.uni.fmi.mjt.exceptions.app.repository.DataAcessException;
import bg.sofia.uni.fmi.mjt.repository.DataSource;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@EventHandlerClass
public class PriceUpdateHandler {
    public static final String MESSAGE = "The %s you have been watching has reached your set price of %.2f";
    //private final AlertRepository alertRepo;
    private final DataSource dataSource;
    public  PriceUpdateHandler(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void onPriceUpdate(CryptoPriceUpdateEvent event) throws DataAcessException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(SqlQueries.SELECT_ALERTS);
             PreparedStatement updateStmt = conn.prepareStatement(SqlQueries.UPDATE_TRIGGERED)) {

            conn.setAutoCommit(false);

            selectStmt.setString(1, event.crypto());
            ResultSet rs = selectStmt.executeQuery();

            while (rs.next()) {
                String username = rs.getString("username");
                BigDecimal targetPrice = rs.getBigDecimal("price_to_notify");
                boolean hasTriggered = rs.getBoolean("has_triggered");

                if (!hasTriggered && event.price().compareTo(targetPrice) >= 0) {
                    updateStmt.setString(1, username);
                    updateStmt.setString(2, event.crypto());
                    updateStmt.executeUpdate();

                }
            }

            conn.commit();

        } catch (SQLException e) {
            throw new DataAcessException("Failed to process price update", e);
        }
    }

}
