package bg.sofia.uni.fmi.mjt.repository;

import bg.sofia.uni.fmi.mjt.cache.CacheCrypto;
import bg.sofia.uni.fmi.mjt.entity.Asset;
import bg.sofia.uni.fmi.mjt.entity.User;
import bg.sofia.uni.fmi.mjt.exceptions.api.*;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.IllegalNameException;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.IllegalPasswordException;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.UserAlreadyExistsInDB;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.UserAlreadyLoggedIn;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.UserHasNotBeenRegistered;
import bg.sofia.uni.fmi.mjt.exceptions.app.command.WrongPasswordException;
import bg.sofia.uni.fmi.mjt.exceptions.app.wallet.CryptoNotFoundInWallet;
import bg.sofia.uni.fmi.mjt.exceptions.app.wallet.NegativeMoneyException;
import org.junit.jupiter.api.*;

import java.io.*;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class InMemoryWalletManagerTest {

    private WalletManagerRepository repository;
    private Reader reader;
    private CacheCrypto mocked = mock();

    @BeforeEach
    void setUp() throws ApiExecutionException {
        when(mocked.get(any())).thenReturn(setUpAssets()[0]);
        reader = new StringReader("");
        repository = new InMemoryWalletManager(reader, mocked);
    }

    @Test
    void testRegisterUserThrowsUserAlreadyExistsInDB() throws Exception {
        repository.registerUser("test", "pass");
        assertThrows(UserAlreadyExistsInDB.class,
                () -> repository.registerUser("test", "pass"));
    }

    @Test
    void testRegisterUserThrowsIllegalPasswordException() {
        assertThrows(IllegalPasswordException.class,
                () -> repository.registerUser("test", null));
    }

    @Test
    void testRegisterUserThrowsIllegalNameException() {
        assertThrows(IllegalNameException.class,
                () -> repository.registerUser(null, "pass"));
    }

    @Test
    void testRegisterUserSuccessfullyCreatesUser() throws Exception {
        repository.registerUser("john", "1234");
        User user = ((InMemoryWalletManager) repository).getUser("john");
        assertNotNull(user);
        assertEquals("john", user.username());
    }

    @Test
    void testLoginUserThrowsUserHasNotBeenRegistered() {
        assertThrows(UserHasNotBeenRegistered.class,
                () -> repository.loginUser("ghost", "pass"));
    }

    @Test
    void testLoginUserThrowsWrongPasswordException() throws Exception {
        repository.registerUser("test", "correct");
        assertThrows(WrongPasswordException.class,
                () -> repository.loginUser("test", "wrong"));
    }

    @Test
    void testLoginUserThrowsUserAlreadyLoggedIn() throws Exception {
        repository.registerUser("test", "pass");
        repository.loginUser("test", "pass");
        assertThrows(UserAlreadyLoggedIn.class,
                () -> repository.loginUser("test", "pass"));
    }

    @Test
    void testLoginUserReturnsUserOnSuccess() throws Exception {
        repository.registerUser("test", "pass");
        User user = repository.loginUser("test", "pass");
        assertNotNull(user);
        assertTrue(user.isLoggedIn());
    }

    @Test
    void testDepositMoneyThrowsNegativeMoneyException() throws Exception {
        repository.registerUser("test", "pass");
        repository.loginUser("test", "pass");
        assertThrows(NegativeMoneyException.class,
                () -> repository.depositMoney(BigDecimal.valueOf(-10), "test"));
    }

    @Test
    void testDepositMoneyIncreasesBalance() throws Exception {
        repository.registerUser("test", "pass");
        repository.loginUser("test", "pass");
        BigDecimal result = repository.depositMoney(BigDecimal.valueOf(100), "test");
        assertEquals(BigDecimal.valueOf(100), result);
    }

    @Test
    void testBuyOfferingThrowsNegativeMoneyExceptionWithMoney() throws Exception {
        repository.registerUser("test", "pass");
        repository.loginUser("test", "pass");
        assertThrows(NegativeMoneyException.class,
                () -> repository.buyOffering("BTC", BigDecimal.valueOf(-5), "0", "test"));
    }

    @Test
    void testBuyOfferingThrowsNegativeMoneyExceptionWithDiscount() throws Exception {
        repository.registerUser("test", "pass");
        repository.loginUser("test", "pass");
        assertThrows(NegativeMoneyException.class,
                () -> repository.buyOffering("BTC", BigDecimal.valueOf(10), "-10", "test"));
    }

    @Test
    void testSellOfferingThrowsCryptoNotFoundInWallet() throws Exception {
        repository.registerUser("name", "pass");
        repository.loginUser("name", "pass");
        assertThrows(CryptoNotFoundInWallet.class,
                () -> repository.sellOffering("BTC", "name"));
    }

    @Test
    void testListOfferingReturnsString() throws Exception {
        CacheCrypto mocked = mock();
        when(mocked.get("BTC")).thenReturn(setUpAssets()[0]);
        String result = repository.listOffering("BTC");
        assertTrue(result.contains("BTC"));
    }

    @Test
    void testListOfferingsReturnsArray() throws Exception {
        Asset[] assets = setUpAssets();
        when(mocked.getAllFrequentAssets()).thenReturn(assets);
        String result = repository.listOfferings();
        assertTrue(result.contains("BTC"));
    }

    @Test
    void testListOfferingsThrowsCryptoNotFoundException()
            throws DataUnavailableException, InsufficientPermissions, BadRequestException, UnauthorizedKeyException,
            ApiKeyLimitExceededException, CryptoNotFoundException {

        CacheCrypto cachedCryptoCurrency = mock();
        when(cachedCryptoCurrency.getAllFrequentAssets()).thenReturn(null);
        assertThrows(CryptoNotFoundException.class,
                () -> repository.listOfferings());
    }

    @Test
    void testGetWalletSummaryReturnsString() throws Exception {
        repository.registerUser("test", "pass");
        repository.loginUser("test", "pass");
        String summary = repository.getWalletSummary("test");
        assertTrue(summary.contains("balance="));
    }

    @Test
    void testGetWalletOverallSummaryReturnsStringWithinCachedValues() throws Exception {
        repository.registerUser("test", "pass");
        repository.loginUser("test", "pass");
        repository.depositMoney(BigDecimal.valueOf(10), "test");
        var assets = setUpAssets();
        when(mocked.get("BTC")).thenReturn(assets[0]);
        repository.buyOffering("BTC", BigDecimal.valueOf(10), "0","test");
        String summary = repository.getWalletOverallSummary("test");
        assertTrue(summary.contains("UserWallet summary"));
        assertTrue(summary.contains("BTC"));
    }

    @Test
    void testSaveUsersToDatabaseWritesFile() throws Exception {
        repository.registerUser("test", "pass");
        Writer writer = new StringWriter();
        repository.saveUsersToDataBase(writer);
        assertFalse(writer.toString().isBlank());
        InMemoryWalletManager testrepo = new InMemoryWalletManager(new StringReader(writer.toString()), mocked);
        assertNotNull(testrepo.getUser("test"));
    }

    @Test
    void testReadUsersFromFileV2WithCorruptedJson() {
        Reader corruptedReader = new StringReader("not valid json !!!");
        assertThrows(com.google.gson.JsonSyntaxException.class,
                () ->  new InMemoryWalletManager(corruptedReader, mocked),
                "Expected to be thrown when the json is corrupt");
    }

    @Test
    void testWriteUsersToFileV2ThrowsUncheckedIOException() throws IOException {
        Writer failingWriter = mock(Writer.class);

        doThrow(new IOException("Simulated IO error"))
                .when(failingWriter)
                .write(any(char[].class), anyInt(), anyInt());

        assertThrows(UncheckedIOException.class,
                () -> repository.saveUsersToDataBase(failingWriter),
                "Expected the Io exception to be wrapped"
        );
    }

    @Test
    void testReadUsersToFileV2ThrowsUncheckedIOException() throws IOException {
        Reader failingReader = mock(Reader.class);

        doThrow(new IOException("Simulated IO error"))
                .when(failingReader)
                .read(any(char[].class), anyInt(), anyInt());

        assertDoesNotThrow(() -> new InMemoryWalletManager(failingReader, mocked),
                "Expected nothing to be thrown and an empty db to be intialized"
        );
    }

    private Asset[] setUpAssets() {
        Asset a1 = new Asset("BTC", "Bitcoin", 1, null, null, null, null,
                null, null, null, null, null, null,
                50.0, null, null, null, null, null, null, null);

        Asset a2 = new Asset("ETH", "BitcoinV2", 1, null, null, null, null,
                null, null, null, null, null, null,
                21.0, null, null, null, null, null, null, null);

        return new Asset[]{a1, a2};
    }
}