package bg.sofia.uni.fmi.mjt.entity;

public final class SqlQueries {
    public static final String SELECT_USER =
            "SELECT username, password, isLoggedIn FROM users WHERE username = ?";

    public static final String SELECT_WALLET =
            "SELECT username, balance FROM user_wallet WHERE username = ?";

    public static final String SELECT_LOGIN =
            "SELECT username, password, isLoggedIn FROM users WHERE username = ? FOR UPDATE";

    public static final String SELECT_ASSETS =
            "SELECT asset_name, amount FROM user_assets WHERE username = ?";

    public static final String UPDATE_BALANCE =
            "UPDATE user_wallet SET balance = ? WHERE username = ?";

    public static final String UPDATE_LOGGED_IN =
            "UPDATE users SET isLoggedIn = TRUE WHERE username = ?";
    public static final String REMOVE_ASSET =
            "DELETE FROM user_assets WHERE username = ? AND asset_name = ?";
    public static final String UPDATE_LOGGED_OUT =
            "UPDATE users SET isLoggedIn = FALSE WHERE username = ?";
    public static final String INSERT_WALLET =
            "INSERT INTO user_wallet (username, balance) VALUES (?, 0)";
    public static final String INSERT_USER =
            "Insert into users (username, password) values (?, ?)";
    public static final String INSERT_ASSET =
            """
            INSERT INTO user_assets (username, asset_name, amount)
            VALUES (?, ?, ?)
            ON DUPLICATE KEY UPDATE 
                amount = VALUES(amount)
            """;
    public static final String SELECT_ALERTS =
            "SELECT username, price_to_notify, has_triggered " +
                    "FROM user_notifications WHERE crypto = ?";

    public static final String UPDATE_TRIGGERED =
            "UPDATE user_notifications SET has_triggered = 1, date_triggered = CURRENT_TIMESTAMP " +
                    "WHERE username = ? AND crypto = ?";
    public static final String INSERT_PRICE_ALERT = "Insert into user_notifications " +
            "(username, crypto, price_to_notify) values (? , ? , ?)";
    public static final String SELECT_USER_ALERTS = "SELECT crypto, price_to_notify, date_triggered " +
            "from user_notifications" +
            "  where username = ? and has_triggered = true";
}
