package dev.africa.pandaware.utils.java.crypt;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.util.Base64;

public class EncryptionUtil {
    private static final byte[] salt = {
            (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
            (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03
    };

    private static final String KEY = "EDE9hLNyGzuGLK4hSLozxgovsLcJh+W3dHdvKjsXicXpq5aUlv7rnMfn4EQFV";

    public static String encrypt(String plainText, String secretKey) throws Exception {
        KeySpec keySpec = new PBEKeySpec((KEY + secretKey).toCharArray(), salt, 256);
        AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, 256);
        SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);

        Cipher cipher = Cipher.getInstance(key.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);

        return new String(Base64.getEncoder().encode(cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8))));
    }

    public static String decrypt(String encryptedText, String secretKey) throws Exception {
        KeySpec keySpec = new PBEKeySpec((KEY + secretKey).toCharArray(), salt, 256);
        AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, 256);
        SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);

        Cipher decipher = Cipher.getInstance(key.getAlgorithm());
        decipher.init(Cipher.DECRYPT_MODE, key, paramSpec);

        return new String(decipher.doFinal(Base64.getDecoder().decode(encryptedText)), StandardCharsets.UTF_8);
    }
}
