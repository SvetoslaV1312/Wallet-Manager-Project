package bg.sofia.uni.fmi.mjt.apikey;

public class ApiKeyProviderSystemImpl implements ApiKeyProvider {
    private static final String API_KEY = "API_KEY";
    @Override
    public String getApikey() {
        return System.getenv(API_KEY);
    }
}
