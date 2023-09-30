/*
 * Decompiled with CFR 0.152.
 */
package encryption.impl;

import encryption.Encryption;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public final class AESEncryption
implements Encryption {
    private final String key;
    private final String iv;
    private final SecretKeySpec secret;

    public AESEncryption(String key) {
        this.key = key;
        this.iv = "1234567812345678";
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec spec = new PBEKeySpec(key.toCharArray(), this.iv.getBytes(StandardCharsets.ISO_8859_1), 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        this.secret = new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    @Override
    public byte[] encrypt(byte[] bytes) {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec iv = new IvParameterSpec(this.iv.getBytes(StandardCharsets.ISO_8859_1));
        cipher.init(1, (Key)this.secret, iv);
        return Base64.getEncoder().encode(cipher.doFinal(bytes));
    }

    @Override
    public byte[] decrypt(byte[] bytes) {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec iv = new IvParameterSpec(this.iv.getBytes(StandardCharsets.ISO_8859_1));
        cipher.init(2, (Key)this.secret, iv);
        return cipher.doFinal(Base64.getDecoder().decode(bytes));
    }
}

