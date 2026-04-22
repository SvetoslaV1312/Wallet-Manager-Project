package bg.sofia.uni.fmi.mjt.apikey;

public class ApiKeyProviderEnvironmentImpl implements ApiKeyProvider {
    private static final String API_KEY = "API_KEY";
    @Override
    public String getApikey() {
        return System.getenv(API_KEY);
    }
}
