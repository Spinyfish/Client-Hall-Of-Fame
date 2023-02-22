package encryption.impl;

import encryption.Encryption;
import lombok.Getter;
import lombok.SneakyThrows;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Getter
public final class RSAEncryption implements Encryption {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    @SneakyThrows
    public RSAEncryption() {
        final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);

        final KeyPair pair = keyGen.generateKeyPair();

        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();
    }

    public RSAEncryption(final PrivateKey privateKey, final PublicKey publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    @Override
    @SneakyThrows
    public byte[] encrypt(final byte[] bytes) {
        final Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

        cipher.init(Cipher.ENCRYPT_MODE, this.publicKey);

        return cipher.doFinal(bytes);
    }

    @Override
    @SneakyThrows
    public byte[] decrypt(final byte[] bytes) {
        final Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

        cipher.init(Cipher.DECRYPT_MODE, this.privateKey);

        return cipher.doFinal(bytes);
    }

    @SneakyThrows
    public static PublicKey toPublicKey(final String input) {
        final byte[] derPublicKey = Base64.getDecoder().decode(input);
        final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        final X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(derPublicKey);

        return keyFactory.generatePublic(publicKeySpec);
    }
}
