package bg.sofia.uni.fmi.mjt.repository;

import bg.sofia.uni.fmi.mjt.entity.User;
import bg.sofia.uni.fmi.mjt.exceptions.api.*;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.IllegalNameException;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.IllegalPasswordException;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.UserAlreadyLoggedIn;
import bg.sofia.uni.fmi.mjt.exceptions.app.wallet.CryptoNotFoundInWallet;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.UserAlreadyExistsInDB;
import bg.sofia.uni.fmi.mjt.exceptions.app.user.UserHasNotBeenRegistered;
import bg.sofia.uni.fmi.mjt.exceptions.app.wallet.IllegalAmountTypeException;
import bg.sofia.uni.fmi.mjt.exceptions.app.wallet.NegativeMoneyException;
import bg.sofia.uni.fmi.mjt.exceptions.app.wallet.WalletBalanceExceeded;
import bg.sofia.uni.fmi.mjt.exceptions.app.command.WrongPasswordException;
import bg.sofia.uni.fmi.mjt.cache.CacheCrypto;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryWalletManager implements WalletManagerRepository {
    private Map<String, User> users;
    private final CacheCrypto cachedCryptoCurrency;
    private static final Gson GSON = new Gson();
    private static final int BUFFER_SIZE = 512;

    public InMemoryWalletManager(Reader reader, CacheCrypto cachedCryptoCurrency) {
        users = new ConcurrentHashMap<>();
        this.cachedCryptoCurrency = cachedCryptoCurrency;
        readUsersFromFileV2(reader);
    }

    @Override
    public synchronized void registerUser(String username, String password)
        throws UserAlreadyExistsInDB, IllegalPasswordException, IllegalNameException {
        User newUser = new User(username, password);
        if (users.containsKey(username)) {
            throw new UserAlreadyExistsInDB("This username is already registered");
        }
        users.put(username, newUser);
    }

    @Override
    public User loginUser(String username, String password)
        throws UserHasNotBeenRegistered, WrongPasswordException, UserAlreadyLoggedIn {
        if (!users.containsKey(username)) {
            throw new UserHasNotBeenRegistered("This username does not have an account to log into");
        }
        User user = getUser(username);
        synchronized (user) {
            if (user.isLoggedIn()) {
                throw new UserAlreadyLoggedIn("The username you are trying to access already has a session active");
            }

            if (user.passwordMatch(password)) {
                user.setLoggedIn(true);
                return user;
            }
        }
        throw new WrongPasswordException("The password does not match the password of the username");
    }

    @Override
    public BigDecimal depositMoney(BigDecimal amount, String user) throws NegativeMoneyException {
        return getUser(user).depositMoney(amount);
    }

    @Override
    public String listOffering(String offeringCode) throws ApiExecutionException {
        var result = cachedCryptoCurrency.get(offeringCode);
        return result.toString();
    }

    @Override
    public String listOfferings()
        throws DataUnavailableException, InsufficientPermissions, BadRequestException, UnauthorizedKeyException,
        ApiKeyLimitExceededException, CryptoNotFoundException {
        var assets = cachedCryptoCurrency.getAllFrequentAssets();
        if (assets == null) {
            throw new CryptoNotFoundException("When retrieving the crypto it was null");
        }
        cachedCryptoCurrency.putMultiple((Arrays.stream(assets).toList()));
        return Arrays.toString(assets);
    }

    @Override
    public void buyOffering(String offeringCode, BigDecimal amount, String discount, String user)
            throws WalletBalanceExceeded, ApiExecutionException,
            NegativeMoneyException, IllegalAmountTypeException {
        BigDecimal discountAmount = BigDecimal.valueOf(0);
        if (discount != null) {
            try {
                discountAmount = BigDecimal.valueOf(Double.parseDouble(discount));
            } catch (NumberFormatException e) {
                throw new IllegalAmountTypeException("Amount cant be parsed", e);
            }
        }
        getUser(user).buyOffering(offeringCode, amount.add(discountAmount),
                BigDecimal.valueOf(cachedCryptoCurrency.get(offeringCode).price_usd()));
    }

    @Override
    public void sellOffering(String offeringCode, String user)
            throws CryptoNotFoundInWallet, ApiExecutionException {
        getUser(user).sellOffering(offeringCode, BigDecimal.valueOf(cachedCryptoCurrency.get(offeringCode).price_usd())
        );
    }

    @Override
    public String getWalletSummary(String user) {
        return getUser(user).walletSummary();
    }

    @Override
    public void logout(String username) throws UserHasNotBeenRegistered {
        if (!users.containsKey(username)) {
            throw new UserHasNotBeenRegistered("This username does not have an account to log into");
        }
        User user = getUser(username);
        synchronized (user) {
            user.setLoggedIn(false);
        }
    }

    @Override
    public String getWalletOverallSummary(String user) throws ApiExecutionException {
        var assets = getUser(user).assets();
        BigDecimal sum = BigDecimal.valueOf(0);
        for (var entry : assets.entrySet()) {
            String crypto = entry.getKey();
            BigDecimal amount = entry.getValue();
            sum = sum.add(amount.multiply(BigDecimal.valueOf(cachedCryptoCurrency.get(crypto).price_usd())));
        }
        return getUser(user).walletOverallSummary(sum);
    }

    @Override
    public void saveUsersToDataBase(Writer writer) {
        writeUsersToFileV2(users, writer);
    }

    @Override
    public CacheCrypto cachedCryptoCurrency() {
        return cachedCryptoCurrency;
    }

    public User getUser(String user) {
        return users.get(user);
    }

    private void readUsersFromFileV2(Reader dbFileReader) {
        var bufferedReader = new BufferedReader(dbFileReader, BUFFER_SIZE);
        try {
            String json = readAll(bufferedReader);
            Type type = new TypeToken<Map<String, User>>() {
            }.getType();
            Map<String, User> loadedUsers = GSON.fromJson(json, type);
            if (loadedUsers == null) {
                users = new ConcurrentHashMap<>();
            } else {
                users = new ConcurrentHashMap<>(loadedUsers);
            }
        } catch (IOException e) {
            users = new ConcurrentHashMap<>();
        }
    }

    private String readAll(BufferedReader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        return sb.toString();
    }

    private void writeUsersToFileV2(Map<String, User> users, Writer dbFileWriter) {
        var bufferedWriter = new BufferedWriter(dbFileWriter, BUFFER_SIZE);
        try {
            String json = GSON.toJson(users);
            bufferedWriter.write(json);
            bufferedWriter.flush();
        } catch (IOException e) {
            throw new UncheckedIOException("A problem occurred while writing to a file", e);
        }
    }
}
