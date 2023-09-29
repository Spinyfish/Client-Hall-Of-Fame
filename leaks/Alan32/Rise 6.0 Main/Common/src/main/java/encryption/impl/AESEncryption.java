package encryption.impl;

import encryption.Encryption;
import lombok.SneakyThrows;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.util.Base64;

public final class AESEncryption implements Encryption {

    private final String key;
    private final String iv;

    private final SecretKeySpec secret;

    @SneakyThrows
    public AESEncryption(final String key) {
        this.key = key;
        this.iv = "1234567812345678";

        final SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        final KeySpec spec = new PBEKeySpec(key.toCharArray(), iv.getBytes(StandardCharsets.ISO_8859_1), 65536, 256);
        final SecretKey tmp = factory.generateSecret(spec);

        this.secret = new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    @Override
    @SneakyThrows
    public byte[] encrypt(final byte[] bytes) {
        final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        final IvParameterSpec iv = new IvParameterSpec(this.iv.getBytes(StandardCharsets.ISO_8859_1));

        cipher.init(Cipher.ENCRYPT_MODE, this.secret, iv);

        return Base64.getEncoder().encode(cipher.doFinal(bytes));
    }

    @Override
    @SneakyThrows
    public byte[] decrypt(final byte[] bytes) {
        final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        final IvParameterSpec iv = new IvParameterSpec(this.iv.getBytes(StandardCharsets.ISO_8859_1));

        cipher.init(Cipher.DECRYPT_MODE, this.secret, iv);

        return cipher.doFinal(Base64.getDecoder().decode(bytes));
    }
}
