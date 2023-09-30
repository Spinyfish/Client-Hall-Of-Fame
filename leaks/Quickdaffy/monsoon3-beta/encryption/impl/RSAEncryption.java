/*
 * Decompiled with CFR 0.152.
 */
package encryption.impl;

import encryption.Encryption;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;

public final class RSAEncryption
implements Encryption {
    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public RSAEncryption() {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair pair = keyGen.generateKeyPair();
        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();
    }

    public RSAEncryption(PrivateKey privateKey, PublicKey publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    @Override
    public byte[] encrypt(byte[] bytes) {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(1, this.publicKey);
        return cipher.doFinal(bytes);
    }

    @Override
    public byte[] decrypt(byte[] bytes) {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(2, this.privateKey);
        return cipher.doFinal(bytes);
    }

    public static PublicKey toPublicKey(String input) {
        byte[] derPublicKey = Base64.getDecoder().decode(input);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(derPublicKey);
        return keyFactory.generatePublic(publicKeySpec);
    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }
}

