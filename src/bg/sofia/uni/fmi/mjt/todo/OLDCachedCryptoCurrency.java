package bg.sofia.uni.fmi.mjt.todo;

public class OLDCachedCryptoCurrency {
    /*
    private Map<String, Pair<Long , Asset>> pairHashMap;
    private static final  int VALID_PERIOD = 108_000_100;    //30 * 60 *60 + (000 to not retrieve often)
    private static final  int MILLIS_TO_SECONDS = 1_000;
    private final CryptoCurrencyRetriever cryptoCurrencyRetriever;

    public OLDCachedCryptoCurrency(Path loadFrom, CryptoCurrencyRetriever cryptoCurrencyRetriever) {
        pairHashMap = new ConcurrentHashMap<>();
        this.cryptoCurrencyRetriever = cryptoCurrencyRetriever;
        readCacheFromFile(loadFrom);
    }

    public synchronized Asset get(String cryptoCurrency)
            throws DataUnavailableException, InsufficientPermissions, BadRequestException, UnauthorizedKeyException,
            ApiKeyLimitExceededException, CryptoNotFoundException {
        var pair = pairHashMap.get(cryptoCurrency);
        long currentTime = System.currentTimeMillis() / MILLIS_TO_SECONDS;

        if (pair != null && currentTime - pair.first() <= VALID_PERIOD) {
            return pair.second();
        }

        Asset[] array = joinOrPropagate( cryptoCurrencyRetriever.getCryptoCurrencyAsync(cryptoCurrency, false));
        if (array == null || array.length == 0) {
            throw new CryptoNotFoundException("No asset found for: " + cryptoCurrency);
        }

        Asset result = array[0];
        pairHashMap.put(cryptoCurrency, new Pair<>(currentTime, result));
        return result;
    }

    public Asset[] getAllFrequentAssets()
            throws DataUnavailableException, InsufficientPermissions, BadRequestException, UnauthorizedKeyException,
            ApiKeyLimitExceededException {
        return joinOrPropagate(cryptoCurrencyRetriever.getCryptoCurrencyAsync(null, true));
    }

    public synchronized void putMultiple(List<Asset> assets) {
        long currentTime = System.currentTimeMillis() / MILLIS_TO_SECONDS;
        assets.stream().filter(asset -> asset.name() != null).forEach((asset) ->
                pairHashMap.put(asset.name(), new Pair<>(currentTime, asset)));
    }

    public void saveCacheToFile(Path file) {
        writeCacheToFile(file);
    }

    public void readCache(Path file) {
        readCacheFromFile(file);
    }

    private void writeCacheToFile(Path file) {
        cryptoCurrencyRetriever.shutdownExecutors();
        try (var objectOutputStream = new ObjectOutputStream(Files.newOutputStream(file))) {
            objectOutputStream.writeObject(pairHashMap);
            objectOutputStream.flush();
        } catch (IOException e) {
            throw new UncheckedIOException("A problem occurred while writing to a file", e);
        }
    }

    private void readCacheFromFile(Path file) {
        FileHandler.createDirectoryIfAbsent(file);
        FileHandler.createFileIfAbsent(file);
        if (FileHandler.checkFileIsEmpty(file)) {
            return;
        }

        try (var objectInputStream = new ObjectInputStream(Files.newInputStream(file))) {
            Object pairHashMapObject;
            pairHashMapObject = objectInputStream.readObject();
            pairHashMap = (ConcurrentHashMap<String, Pair<Long , Asset>>) pairHashMapObject;

        } catch (IOException e) {
            pairHashMap = new ConcurrentHashMap<>();
            //writeCacheToFile(file); dont erase the whole db
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("A problem occurred with tan unknown class", e);
        }
    }
*/
}
