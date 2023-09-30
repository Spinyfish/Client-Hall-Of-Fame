package dev.africa.pandaware.utils.java.crypt;

import lombok.experimental.UtilityClass;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.util.Base64;

@UtilityClass
public class AESEncryptionUtils {
    private final byte[] salt = {
            (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
            (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03
    };

    public String encrypt(String plainText, String secretKey) {
        try {
            KeySpec keySpec = new PBEKeySpec(secretKey.toCharArray(), salt, 256);
            AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, 256);
            SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);

            Cipher cipher = Cipher.getInstance(key.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);

            return new String(Base64.getEncoder().encode(cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8))));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String decrypt(String encryptedText, String secretKey) {
        try {
            KeySpec keySpec = new PBEKeySpec(secretKey.toCharArray(), salt, 256);
            AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, 256);
            SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);

            Cipher decipher = Cipher.getInstance(key.getAlgorithm());
            decipher.init(Cipher.DECRYPT_MODE, key, paramSpec);

            return new String(decipher.doFinal(Base64.getDecoder().decode(encryptedText)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
