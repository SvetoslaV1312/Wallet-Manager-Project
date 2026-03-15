package bg.sofia.uni.fmi.mjt.encryption;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordEncryptionV2 {

    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;
    private static final String SHA_256 = "PBKDF2WithHmacSHA256";
    private static final String DELIMITER = ":";

    public static  String hash(String password) {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);

        byte[] hash = pbkdf2(password.toCharArray(), salt);

        return Base64.getEncoder().encodeToString(salt) + DELIMITER +
            Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verify(String password, String stored) {
        String[] parts = stored.split(DELIMITER);
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] expected = Base64.getDecoder().decode(parts[1]);

        byte[] actual = pbkdf2(password.toCharArray(), salt);

        if (actual.length != expected.length) return false;

        int diff = 0;
        for (int i = 0; i < actual.length; i++) {
            diff |= actual[i] ^ expected[i];
        }
        return diff == 0;
    }

    private static byte[] pbkdf2(char[] password, byte[] salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(SHA_256);
            return skf.generateSecret(spec).getEncoded();
        } catch (Exception e) {
            throw new RuntimeException("An error occurred when securing the password", e);
        }
    }
}
