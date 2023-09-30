package dev.africa.pandaware.utils.java;

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
public class CryptUtils {
    private final byte[] salt = {
            (byte) 0xA5, (byte) 0xB2, (byte) 0xF4, (byte) 0x9A,
            (byte) 0x72, (byte) 0xF9, (byte) 0xC6, (byte) 0x7D
    };

    public String encrypt(String plainText, String password) {
        try {
            final KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, 256);
            final SecretKey secretKey = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
            final AlgorithmParameterSpec parameterSpec = new PBEParameterSpec(salt, 256);

            final Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

            final byte[] byteArray = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            return new String(Base64.getEncoder().encode(byteArray));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public String decrypt(String encryptedText, String password) {
        try {
            final KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, 256);
            final SecretKey secretKey = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
            final AlgorithmParameterSpec parameterSpec = new PBEParameterSpec(salt, 256);

            final Cipher decipher = Cipher.getInstance(secretKey.getAlgorithm());
            decipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

            final byte[] byteArray = decipher.doFinal(Base64.getDecoder().decode(encryptedText));

            return new String(byteArray, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}