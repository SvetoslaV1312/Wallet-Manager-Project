package bg.sofia.uni.fmi.mjt.todo;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class DataBaseFilesEncryption {
    private static final String ENCRYPTION_ALGORITHM = "AES"; // //  Advanced Encryption Standard
    private static final int KEY_SIZE_IN_BITS = 128; // Key sizes like 192 or 256 might not be available on all systems
    private static final SecretKey SECRET_KEY = loadSecretKey();
    private static final String KEY_FILE_PATH = "secret.key";

    private static void persistSecretKey(SecretKey secretKey) {
        byte[] keyBytes = secretKey.getEncoded();
        Path keyFilePath = Path.of("out", KEY_FILE_PATH);
        // Write key bytes to file
        try {
            Files.write(keyFilePath, keyBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static SecretKey loadSecretKey() {
        Path path = Path.of("out", KEY_FILE_PATH);
        if (Files.exists(path)) {
            byte[] keyBytes = null;
            try {
                keyBytes = Files.readAllBytes(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return new SecretKeySpec(keyBytes, ENCRYPTION_ALGORITHM);
        }
        return generateSecretKey();
    }

    private static SecretKey generateSecretKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM);
            keyGenerator.init(KEY_SIZE_IN_BITS);
            SecretKey secretKey = keyGenerator.generateKey();
            persistSecretKey(secretKey);
            return secretKey;

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encrypt(String password) {
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY);
            var value = cipher.doFinal(password.getBytes());
            return new String(Base64.getEncoder().encode(value), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean verify(String password, String stored) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, SECRET_KEY);

            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(stored.getBytes()));

            return password.equals(new String(decrypted, StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
