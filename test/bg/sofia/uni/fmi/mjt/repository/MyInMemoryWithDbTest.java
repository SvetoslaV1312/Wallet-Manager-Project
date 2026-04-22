package bg.sofia.uni.fmi.mjt.repository;

import bg.sofia.uni.fmi.mjt.cache.CacheCrypto;
import bg.sofia.uni.fmi.mjt.entity.Asset;
import bg.sofia.uni.fmi.mjt.entity.SqlQueries;
import bg.sofia.uni.fmi.mjt.entity.User;
import bg.sofia.uni.fmi.mjt.exceptions.api.*;
import bg.sofia.uni.fmi.mjt.exceptions.app.command.WrongPasswordException;
import bg.sofia.uni.fmi.mjt.exceptions.app.repository.DataAcessException;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.*;
import bg.sofia.uni.fmi.mjt.exceptions.app.wallet.*;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.*;

import static bg.sofia.uni.fmi.mjt.encryption.PasswordEncryptionV2.hash;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MyInMemoryWithDbTest {

    private final CacheCrypto cache = mock(CacheCrypto.class);
    private final DataSource dataSource = mock(DataSource.class);
    private final Connection conn = mock(Connection.class);
    private final PreparedStatement pst = mock(PreparedStatement.class);
    private final ResultSet rs = mock(ResultSet.class);
    private User user;

    private MyInMemoryWithDb repo;

    @BeforeEach
    void setup() throws Exception {
        user = new User("john", "pass");
        repo = new MyInMemoryWithDb(cache, dataSource);
    }

    @Test
    void testRegisterUserThrowsUserAlreadyExists() throws Exception {
        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(startsWith(SqlQueries.SELECT_USER)))
                .thenThrow(new SQLIntegrityConstraintViolationException());
        assertThrows(UserAlreadyExistsInDB.class,
                () -> repo.registerUser("john", "pass"));
    }

    @Test
    void testLogoutThrowsExceptionWhenNotRegisteredUsed() throws SQLException {
        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(startsWith(SqlQueries.SELECT_USER))).thenReturn(pst);
        when(pst.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        assertThrows(UserHasNotBeenRegistered.class,
                () -> repo.logout(user));
    }

    @Test
    void testGetWalletOverallSummaryReturnsStringWithinCachedValues() throws Exception {
        when(conn.prepareStatement(startsWith(SqlQueries.SELECT_WALLET))).thenReturn(pst);
        when(pst.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getBigDecimal("balance")).thenReturn(BigDecimal.ZERO);
        String summary = repo.getWalletOverallSummary(user);
        assertTrue(summary.contains("UserWallet summary"));
        assertFalse(summary.contains("BTC"));
    }

    @Test
    void testRegisterUserThrowsIllegalName() {
        assertThrows(IllegalNameException.class,
                () -> repo.registerUser(null, "pass"));
    }

    @Test
    void testRegisterUserThrowsIllegalPassword() {
        assertThrows(IllegalPasswordException.class,
                () -> repo.registerUser("john", ""));
    }

    @Test
    void testRegisterUserSuccessfullyRegistersNewUser() throws Exception {
        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(startsWith("SELECT username, password, isLoggedIn"))).thenReturn(pst);
        when(pst.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);
        when(conn.prepareStatement(any())).thenReturn(pst);
        assertDoesNotThrow(() -> repo.registerUser("john", "pass"));
        verify(conn).commit();
    }

    @Test
    void testLoginUserThrowsUserNotRegistered() throws Exception {
        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(startsWith("SELECT username, password, isLoggedIn"))).thenReturn(pst);
        when(pst.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        assertThrows(UserHasNotBeenRegistered.class,
                () -> repo.loginUser("ghost", "pass"));
    }

    @Test
    void testLoginUserThrowsWrongPassword() throws Exception {
        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(startsWith("SELECT username, password, isLoggedIn"))).thenReturn(pst);
        when(pst.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getString("username")).thenReturn("john");
        when(rs.getString("password")).thenReturn(hash("correct"));
        when(rs.getBoolean("isLoggedIn")).thenReturn(false);
        when(conn.prepareStatement(startsWith("UPDATE users SET"))).thenReturn(pst);
        assertThrows(WrongPasswordException.class,
                () -> repo.loginUser("john", "wrong"));
    }

    @Test
    void testLoginUserThrowsAlreadyLoggedIn() throws Exception {
        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(startsWith("SELECT username, password, isLoggedIn"))).thenReturn(pst);
        when(pst.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getString("username")).thenReturn("john");
        when(rs.getString("password")).thenReturn(hash("pass"));
        when(rs.getBoolean("isLoggedIn")).thenReturn(true);

        assertThrows(UserAlreadyLoggedIn.class,
                () -> repo.loginUser("john", "pass"));
    }

    @Test
    void testLoginUserSuccessfullyConstructsUser() throws Exception {
        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(startsWith("SELECT username, password, isLoggedIn"))).thenReturn(pst);
        when(pst.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true);
        when(rs.getString("username")).thenReturn("john");
        when(rs.getString("password")).thenReturn(hash("pass"));
        when(rs.getBoolean("isLoggedIn")).thenReturn(false);
        ResultSet walletRs = mock(ResultSet.class);
        PreparedStatement walletPst = mock(PreparedStatement.class);

        when(conn.prepareStatement(startsWith(SqlQueries.SELECT_WALLET))).thenReturn(walletPst);
        when(walletPst.executeQuery()).thenReturn(walletRs);
        when(walletRs.next()).thenReturn(true);
        when(walletRs.getBigDecimal("balance")).thenReturn(BigDecimal.ZERO);

        when(conn.prepareStatement(startsWith(SqlQueries.UPDATE_LOGGED_IN))).thenReturn(pst);

        PreparedStatement assetPst = mock(PreparedStatement.class);
        when(conn.prepareStatement(startsWith("SELECT asset_name")))
                .thenReturn(assetPst);
        ResultSet assetsRs = mock();

        when(assetPst.executeQuery()).thenReturn(assetsRs);
        when(assetsRs.next()).thenReturn(false);

        User u = repo.loginUser("john", "pass");
        assertNotNull(u);
    }

    @Test
    void testDepositThrowsNegativeMoney() {
        assertThrows(NegativeMoneyException.class,
                () -> repo.depositMoney(BigDecimal.valueOf(-5), user));
    }

    @Test
    void testDepositSuccessfullyUpdateAmountOnNewAccount() throws Exception {
        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(startsWith(SqlQueries.UPDATE_BALANCE))).thenReturn(pst);

        BigDecimal result = repo.depositMoney(BigDecimal.valueOf(100), user);
        assertEquals(BigDecimal.valueOf(100), result);
    }

    @Test
    void testBuyOfferingThrowsNegativeMoney() throws ApiExecutionException {
        setUpBTCCrypto();
        assertThrows(NegativeMoneyException.class,
                () -> repo.buyOffering("BTC", BigDecimal.valueOf(-5), "0", user));
    }

    private void setUpBTCCrypto() throws ApiExecutionException {
        when(cache.get("BTC")).thenReturn(new Asset("1", "BTC", 1, null, null, null, null,
                null, null , null ,null, null, null, 1.0,
                null , null ,null, null, null,null,null));
    }

    @Test
    void testBuyOfferingThrowsIllegalDiscount() {
        assertThrows(IllegalAmountTypeException.class,
                () -> repo.buyOffering("BTC", BigDecimal.valueOf(10), "abc", user));
    }

    @Test
    void testSellOfferingThrowsCryptoNotFound() throws Exception {
        setUpBTCCrypto();
        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(startsWith(SqlQueries.REMOVE_ASSET))).thenReturn(pst);
        when(conn.prepareStatement(startsWith(SqlQueries.UPDATE_BALANCE))).thenReturn(pst);
        assertThrows(CryptoNotFoundInWallet.class,
                () -> repo.sellOffering("BTC", user));
    }

    @Test
    void testListOfferingReturnsValidString() throws Exception {
        Asset a = new Asset("BTC", "Bitcoin", 1, null, null, null, null,
                null, null, null, null, null, null,
                50.0, null, null, null, null, null, null, null);

        when(cache.get("BTC")).thenReturn(a);

        String result = repo.listOffering("BTC");
        assertTrue(result.contains("BTC"));
    }

    @Test
    void testListOfferingsThrowsCryptoNotFound() throws Exception {
        when(cache.getAllFrequentAssets()).thenReturn(null);

        assertThrows(CryptoNotFoundException.class,
                () -> repo.listOfferings());
    }

    @Test
    void testGetWalletSummaryReturnsValidString() throws Exception {
        when(rs.next()).thenReturn(true);
        when(rs.getString("password")).thenReturn("pass");
        when(rs.getBoolean("isLoggedIn")).thenReturn(true);

        when(conn.prepareStatement(startsWith(SqlQueries.SELECT_WALLET))).thenReturn(pst);
        when(pst.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getBigDecimal("balance")).thenReturn(BigDecimal.ZERO);

        when(conn.prepareStatement(startsWith("SELECT asset_name")))
                .thenReturn(pst);
        when(pst.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        String summary = repo.getWalletSummary(user);
        assertTrue(summary.contains("balance"));
    }

    @Test
    void testSetPriceAlertSuccess() throws Exception {
        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(startsWith("Insert into user_notifications"))).thenReturn(pst);

        assertDoesNotThrow(() ->
                repo.setPriceAlert(user, "BTC", BigDecimal.valueOf(100))
        );

        verify(pst).setString(1, "john");
        verify(pst).setString(2, "BTC");
        verify(pst).setBigDecimal(3, BigDecimal.valueOf(100));
        verify(pst).executeUpdate();
    }

    @Test
    void testSetPriceAlertThrowsDataAccessException() throws Exception {
        when(dataSource.getConnection()).thenReturn(conn);

        when(conn.prepareStatement(startsWith("Insert into user_notifications")))
                .thenThrow(new SQLIntegrityConstraintViolationException());

        assertThrows(DataAcessException.class,
                () -> repo.setPriceAlert(user, "BTC", BigDecimal.valueOf(100)));
    }

    @Test
    void testCheckAlertsReturnsNoAlertsMessage() throws Exception {
        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(startsWith("SELECT crypto, price_to_notify, date_triggered"))).thenReturn(pst);
        when(pst.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        String result = repo.checkAlerts(user);

        assertEquals("You have no triggered alerts.", result);
    }

    @Test
    void testCheckAlertsThrowsDataAccessException() throws Exception {
        when(dataSource.getConnection()).thenReturn(conn);

        when(conn.prepareStatement(startsWith("SELECT crypto, price_to_notify, date_triggered")))
                .thenThrow(new SQLSyntaxErrorException());

        assertThrows(DataAcessException.class,
                () -> repo.checkAlerts(user));
    }


}
