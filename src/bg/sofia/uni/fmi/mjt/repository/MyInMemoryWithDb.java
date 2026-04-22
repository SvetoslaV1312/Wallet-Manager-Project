package bg.sofia.uni.fmi.mjt.repository;

import bg.sofia.uni.fmi.mjt.cache.CacheCrypto;
import bg.sofia.uni.fmi.mjt.entity.SqlQueries;
import bg.sofia.uni.fmi.mjt.entity.User;
import bg.sofia.uni.fmi.mjt.exceptions.api.ApiExecutionException;
import bg.sofia.uni.fmi.mjt.exceptions.api.CryptoNotFoundException;
import bg.sofia.uni.fmi.mjt.exceptions.app.AppExecutionException;
import bg.sofia.uni.fmi.mjt.exceptions.app.command.WrongPasswordException;
import bg.sofia.uni.fmi.mjt.exceptions.app.repository.DataAcessException;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.IllegalNameException;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.IllegalPasswordException;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.UserAlreadyExistsInDB;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.UserAlreadyLoggedIn;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.UserHasNotBeenRegistered;
import bg.sofia.uni.fmi.mjt.exceptions.app.wallet.IllegalAmountTypeException;
import bg.sofia.uni.fmi.mjt.exceptions.app.wallet.NegativeMoneyException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLSyntaxErrorException;
import java.sql.SQLTimeoutException;
import java.sql.SQLTransientException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static bg.sofia.uni.fmi.mjt.encryption.PasswordEncryptionV2.hash;
import static bg.sofia.uni.fmi.mjt.encryption.PasswordEncryptionV2.verify;

public class MyInMemoryWithDb implements WalletManagerRepositoryDB {
    private final CacheCrypto cachedCryptoCurrency;
    private final DataSource dataSource;

    public MyInMemoryWithDb(CacheCrypto cachedCryptoCurrency, DataSource dataSource) {
        this.cachedCryptoCurrency = cachedCryptoCurrency;
        this.dataSource = dataSource;
    }

    @Override
    public void registerUser(String username, String password)
            throws AppExecutionException, DataAcessException {
        if (username == null || username.isBlank()) {
            throw new IllegalNameException("Username cannot be empty");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalPasswordException("Password cannot be empty");
        }
        try (Connection conn = dataSource.getConnection();
             PreparedStatement selectStatement = conn.prepareStatement(SqlQueries.SELECT_USER)) {
            conn.setAutoCommit(false);
            selectStatement.setString(1, username);
            ResultSet rs = selectStatement.executeQuery();
            rs.close();
            createUser(username, hash(password), conn);
            createUserWallet(username, conn);
            conn.commit();

        } catch (SQLIntegrityConstraintViolationException e) {
            throw new UserAlreadyExistsInDB("Another user has this name", e);
        } catch (SQLException e) {
            throw translateSqlException(e);
        }
    }

    @Override
    public User loginUser(String username, String password)
            throws AppExecutionException, DataAcessException {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pst = conn.prepareStatement(SqlQueries.SELECT_LOGIN)) {
                pst.setString(1, username);

                try (ResultSet rs = pst.executeQuery()) {
                    if (!rs.next()) {
                        throw new UserHasNotBeenRegistered("User not found");
                    }
                    boolean loggedIn = rs.getBoolean("isLoggedIn");
                    if (loggedIn) {
                        throw new UserAlreadyLoggedIn("User already logged in");
                    }

                    String realPassword = rs.getString("password");

                    if (!verify(password, realPassword)) {
                        throw new WrongPasswordException("Wrong password");
                    }
                    markAsLoggedIn(username, conn);
                    conn.commit();
                    return getUser(username, conn);
                }
            }
        } catch (SQLException e) {
            throw translateSqlException(e);
        }
    }

    @Override
    public BigDecimal depositMoney(BigDecimal amount, User user) throws AppExecutionException, DataAcessException {
        try (Connection conn = dataSource.getConnection()) {
            BigDecimal result = user.depositMoney(amount);
            updateBalance(user.username(), result, conn);
            return result;
        } catch (SQLException e) {
            throw translateSqlException(e);
        }

    }

    @Override
    public String listOffering(String offeringCode) throws ApiExecutionException {
        var result = cachedCryptoCurrency.get(offeringCode);
        return result.toString();
    }

    @Override
    public String listOfferings()
            throws ApiExecutionException {
        var assets = cachedCryptoCurrency.getAllFrequentAssets();
        if (assets == null) {
            throw new CryptoNotFoundException("When retrieving the crypto it was null");
        }
        cachedCryptoCurrency.putMultiple((Arrays.stream(assets).toList()));
        return Arrays.toString(assets);
    }

    @Override
    public void buyOffering(String offeringCode, BigDecimal amount, String discount, User user)
            throws AppExecutionException, ApiExecutionException, DataAcessException {
        BigDecimal discountAmount = getBigDecimal(discount);
        Connection conn = null;

        try {
            BigDecimal value = user.buyOffering(offeringCode, amount.add(discountAmount),
                    BigDecimal.valueOf(cachedCryptoCurrency.get(offeringCode).price_usd()));
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            updateBalance(user.username(), user.getUserWallet().balance(), conn);
            updateAsset(user.username(), offeringCode, value,
                    BigDecimal.valueOf(cachedCryptoCurrency.get(offeringCode).price_usd()), conn);
            conn.commit();
            conn.close();

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    // log rollback failure
                }
            }
            throw translateSqlException(e);
        }
    }

    @Override
    public void sellOffering(String offeringCode, User user)
            throws AppExecutionException, ApiExecutionException, DataAcessException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            BigDecimal newBalance = user.sellOffering(offeringCode, BigDecimal
                    .valueOf(cachedCryptoCurrency.get(offeringCode).price_usd())
            );
            removeAsset(user.username(), offeringCode, conn);
            updateBalance(user.username(), newBalance, conn);
            conn.commit();
            conn.close();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    // optional: log rollback failure
                }
            }
            throw translateSqlException(e);
        }
    }

    @Override
    public String getWalletSummary(User user) {
        return user.walletSummary();
    }

    @Override
    public void logout(User user) throws AppExecutionException, DataAcessException {
        try (Connection conn = dataSource.getConnection()) {

            try (PreparedStatement pst = conn.prepareStatement(SqlQueries.SELECT_USER)) {
                pst.setString(1, user.username());

                try (ResultSet rs = pst.executeQuery()) {
                    if (!rs.next()) {
                        throw new UserHasNotBeenRegistered("The user you are trying" +
                                " to access does not have an account");
                    }
                }
            }
            updateLoggedOut(user.username(), conn);
        } catch (SQLException e) {
            throw translateSqlException(e);
        }

    }

    @Override
    public String getWalletOverallSummary(User user) throws ApiExecutionException {
        var assets = user.assets();
        BigDecimal sum = BigDecimal.valueOf(0);
        for (var entry : assets.entrySet()) {
            String crypto = entry.getKey();
            BigDecimal amount = entry.getValue();
            sum = sum.add(amount.multiply(BigDecimal.valueOf(cachedCryptoCurrency.get(crypto).price_usd())));
        }
        return user.walletOverallSummary(sum);
    }

    @Override
    public void setPriceAlert(User user, String offeringCode, BigDecimal price) throws DataAcessException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pst = conn.prepareStatement(SqlQueries.INSERT_PRICE_ALERT)) {
            int index = 1;
            pst.setString(index++, user.username());
            pst.setString(index++, offeringCode);
            pst.setBigDecimal(index, price);
            pst.executeUpdate();
        } catch (SQLException e) {
            throw translateSqlException(e);
        }
    }

    @Override
    public String checkAlerts(User user) throws DataAcessException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pst = conn.prepareStatement(SqlQueries.SELECT_USER_ALERTS)) {
            int index = 1;
            pst.setString(index, user.username());
            ResultSet rs = pst.executeQuery();
            return getAllAlerts(rs);
        } catch (SQLException e) {
            throw translateSqlException(e);
        }
    }

    private static String getAllAlerts(ResultSet rs) throws SQLException {
        StringBuilder sb = new StringBuilder();
        while (rs.next()) {
            String crypto = rs.getString("crypto");
            BigDecimal price = rs.getBigDecimal("price_to_notify");
            Date date = rs.getDate("date_triggered");
            sb.append(String.format("A price alert for %s has occurred on %s at price: %.2f",
                    crypto, date, price));
        }
        if (sb.isEmpty()) {
            return "You have no triggered alerts.";
        }
        return sb.toString();
    }

    @Override
    public CacheCrypto cachedCryptoCurrency() {
        return cachedCryptoCurrency;
    }

    private static void createUserWallet(String username, Connection conn) throws SQLException {
        try (PreparedStatement insertStatement = conn.prepareStatement(SqlQueries.INSERT_WALLET)) {
            int index = 1;
            insertStatement.setString(index, username);
            insertStatement.executeUpdate();
        }
    }

    private void updateLoggedOut(String username, Connection conn) throws SQLException {
        try (PreparedStatement pst = conn.prepareStatement(SqlQueries.UPDATE_LOGGED_OUT)) {
            int index = 1;
            pst.setString(index, username);
            pst.executeUpdate();

        }
    }

    private void removeAsset(String username, String offeringCode, Connection conn) throws SQLException {
        try (PreparedStatement pst = conn.prepareStatement(SqlQueries.REMOVE_ASSET)) {
            int index = 1;
            pst.setString(index++, username);
            pst.setString(index, offeringCode);
            pst.executeUpdate();

        }
    }

    private void updateBalance(String username, BigDecimal newBalance,
                               Connection conn) throws SQLException {
        try (PreparedStatement pst = conn.prepareStatement(SqlQueries.UPDATE_BALANCE)) {
            int index = 1;
            pst.setBigDecimal(index++, newBalance);
            pst.setString(index, username);
            pst.executeUpdate();

        }
    }

    private void updateAsset(String username, String offeringCode,
                             BigDecimal amount, BigDecimal priceUsd, Connection conn) throws SQLException {
        try (PreparedStatement pst = conn.prepareStatement(SqlQueries.INSERT_ASSET)) {

            int index = 1;
            pst.setString(index++, username);
            pst.setString(index++, offeringCode);
            pst.setBigDecimal(index, amount);
            pst.executeUpdate();

        }
    }

    private void createUser(String username, String password, Connection conn)
            throws SQLException {
        try (PreparedStatement insertStatement = conn.prepareStatement(SqlQueries.INSERT_USER)) {
            int index = 1;
            insertStatement.setString(index++, username);
            insertStatement.setString(index, password);
            insertStatement.executeUpdate();
        }
    }

    private DataAcessException translateSqlException(SQLException e) {
        if (e instanceof SQLIntegrityConstraintViolationException) {
            return new DataAcessException("This operation violates a database constraint.", e);
        }
        if (e instanceof SQLSyntaxErrorException) {
            return new DataAcessException("A database syntax error occurred. Contact support.", e);
        }
        if (e instanceof SQLTimeoutException) {
            return new DataAcessException("The database operation timed out. Please try again later.", e);
        }
        if (e instanceof SQLTransientException) {
            return new DataAcessException("A temporary database issue occurred. Please retry.", e);
        }
        return new DataAcessException("An unexpected database error occurred. Contact support.", e);
    }

    private User getUser(String username, Connection conn) throws AppExecutionException {
        try (PreparedStatement pst = conn.prepareStatement(SqlQueries.SELECT_USER)) {
            int index = 1;
            pst.setString(index, username);

            try (ResultSet rs = pst.executeQuery()) {
                if (!rs.next()) {
                    throw new UserHasNotBeenRegistered("The user you are trying" +
                            " to access does not have an account");
                }
                User user = new User(rs.getString("username"), rs.getString("password"));
                user.setLoggedIn(rs.getBoolean("isLoggedIn"));

                setBalance(username, conn, user, rs);
                setAssets(username, conn, user);

                return user;

            } catch (NegativeMoneyException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error loading user from database", e);
        }
    }

    private void setAssets(String username, Connection conn, User user) throws SQLException {
        Map<String, BigDecimal> assets = new HashMap<>();
        try (PreparedStatement preparedStatement = conn.prepareStatement(SqlQueries.SELECT_ASSETS)) {
            preparedStatement.setString(1, username);

            try (ResultSet res = preparedStatement.executeQuery()) {
                while (res.next()) {
                    String assetName = res.getString("asset_name");
                    BigDecimal amount = res.getBigDecimal("amount");
                    assets.put(assetName, amount);
                }
            }
        }

        user.getUserWallet().setAssets(assets);
    }

    private void setBalance(String username, Connection conn, User user, ResultSet rs) throws SQLException,
            NegativeMoneyException {
        try (PreparedStatement pst = conn.prepareStatement(SqlQueries.SELECT_WALLET)) {
            int index = 1;
            pst.setString(index, username);

            try (ResultSet resultSet = pst.executeQuery()) {
                if (resultSet.next()) {
                    user.getUserWallet().depositMoney(resultSet.getBigDecimal("balance"));
                }
            }
        }
    }

    private static void markAsLoggedIn(String username, Connection conn) throws SQLException {
        try (PreparedStatement update = conn.prepareStatement(
                SqlQueries.UPDATE_LOGGED_IN)) {
            int index = 1;
            update.setString(index, username);
            update.executeUpdate();
        }
    }

    private BigDecimal getBigDecimal(String discount) throws IllegalAmountTypeException {
        BigDecimal discountAmount = BigDecimal.valueOf(0);
        if (discount != null) {
            try {
                discountAmount = BigDecimal.valueOf(Double.parseDouble(discount));
            } catch (NumberFormatException e) {
                throw new IllegalAmountTypeException("Amount cant be parsed", e);
            }
        }
        return discountAmount;
    }

}
