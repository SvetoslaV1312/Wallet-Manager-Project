package bg.sofia.uni.fmi.mjt.cache;

import bg.sofia.uni.fmi.mjt.exceptions.app.encryption.CacheFileEncryptionException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.NoSuchAlgorithmException;

public class CryptoMemoryFilesEncryption {
    private static final String ENCRYPTION_ALGORITHM = "AES"; // //  Advanced Encryption Standard
    private static final int KEY_SIZE_IN_BITS = 128; // Key sizes like 192 or 256 might not be available on all systems
    private static final SecretKey SECRET_KEY = loadSecretKey();
    private static final String KEY_FILE_PATH = "secret.key";

    public static byte[] encryptBytes(byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new CacheFileEncryptionException("An error occurred while saving cache", e);
        }
    }

    public static byte[] decryptBytes(byte[] encrypted) {
        try {
            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, SECRET_KEY);
            return cipher.doFinal(encrypted);
        } catch (Exception e) {
            throw new CacheFileEncryptionException("An error occurred while loading cache", e);
        }
    }

    private static void persistSecretKey(SecretKey secretKey) {
        byte[] keyBytes = secretKey.getEncoded();
        Path keyFilePath = Path.of("out", KEY_FILE_PATH);
        // Write key bytes to file
        try {
            Files.write(keyFilePath, keyBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new CacheFileEncryptionException( "An error occurred while loading cache",e);
        }

    }

    private static SecretKey loadSecretKey() {
        Path path = Path.of("out", KEY_FILE_PATH);
        if (Files.exists(path)) {
            byte[] keyBytes;
            try {
                keyBytes = Files.readAllBytes(path);
            } catch (IOException e) {
                throw new CacheFileEncryptionException("An error occurred while loading cache", e);
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
            throw new CacheFileEncryptionException("An error occurred while loading cache", e);
        }
    }

}
