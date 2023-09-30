package net.minecraft.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class CryptManager {
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Generate a new shared secret AES key from a secure random source
     */
    public static SecretKey createNewSharedKey() {
        try {
            final KeyGenerator keygenerator = KeyGenerator.getInstance("AES");
            keygenerator.init(128);
            return keygenerator.generateKey();
        } catch (final NoSuchAlgorithmException nosuchalgorithmexception) {
            throw new Error(nosuchalgorithmexception);
        }
    }

    /**
     * Generates RSA KeyPair
     */
    public static KeyPair generateKeyPair() {
        try {
            final KeyPairGenerator keypairgenerator = KeyPairGenerator.getInstance("RSA");
            keypairgenerator.initialize(1024);
            return keypairgenerator.generateKeyPair();
        } catch (final NoSuchAlgorithmException nosuchalgorithmexception) {
            nosuchalgorithmexception.printStackTrace();
            LOGGER.error("Key pair generation failed!");
            return null;
        }
    }

    /**
     * Compute a serverId hash for use by sendSessionRequest()
     *
     * @param serverId  The server ID
     * @param publicKey The public key
     * @param secretKey The secret key
     */
    public static byte[] getServerIdHash(final String serverId, final PublicKey publicKey, final SecretKey secretKey) {
        try {
            return digestOperation("SHA-1", serverId.getBytes("ISO_8859_1"), secretKey.getEncoded(), publicKey.getEncoded());
        } catch (final UnsupportedEncodingException unsupportedencodingexception) {
            unsupportedencodingexception.printStackTrace();
            return null;
        }
    }

    /**
     * Compute a message digest on arbitrary byte[] data
     *
     * @param algorithm The name of the algorithm
     * @param data      The data
     */
    private static byte[] digestOperation(final String algorithm, final byte[]... data) {
        try {
            final MessageDigest messagedigest = MessageDigest.getInstance(algorithm);

            for (final byte[] abyte : data) {
                messagedigest.update(abyte);
            }

            return messagedigest.digest();
        } catch (final NoSuchAlgorithmException nosuchalgorithmexception) {
            nosuchalgorithmexception.printStackTrace();
            return null;
        }
    }

    /**
     * Create a new PublicKey from encoded X.509 data
     *
     * @param encodedKey The key, encoded to the X.509 standard
     */
    public static PublicKey decodePublicKey(final byte[] encodedKey) {
        try {
            final EncodedKeySpec encodedkeyspec = new X509EncodedKeySpec(encodedKey);
            final KeyFactory keyfactory = KeyFactory.getInstance("RSA");
            return keyfactory.generatePublic(encodedkeyspec);
        } catch (final NoSuchAlgorithmException var3) {
        } catch (final InvalidKeySpecException var4) {
        }

        LOGGER.error("Public key reconstitute failed!");
        return null;
    }

    /**
     * Decrypt shared secret AES key using RSA private key
     *
     * @param key                The encryption key
     * @param secretKeyEncrypted The encrypted secret key
     */
    public static SecretKey decryptSharedKey(final PrivateKey key, final byte[] secretKeyEncrypted) {
        return new SecretKeySpec(decryptData(key, secretKeyEncrypted), "AES");
    }

    /**
     * Encrypt byte[] data with RSA public key
     *
     * @param key  The encryption key
     * @param data The data
     */
    public static byte[] encryptData(final Key key, final byte[] data) {
        return cipherOperation(1, key, data);
    }

    /**
     * Decrypt byte[] data with RSA private key
     *
     * @param key  The encryption key
     * @param data The data
     */
    public static byte[] decryptData(final Key key, final byte[] data) {
        return cipherOperation(2, key, data);
    }

    /**
     * Encrypt or decrypt byte[] data using the specified key
     *
     * @param opMode The operation mode of the cipher. (this is one of the following: Cipher.ENCRYPT_MODE,
     *               Cipher.DECRYPT_MODE, Cipher.WRAP_MODE or Cipher.UNWRAP_MODE)
     * @param key    The encryption key
     * @param data   The data
     */
    private static byte[] cipherOperation(final int opMode, final Key key, final byte[] data) {
        try {
            return createTheCipherInstance(opMode, key.getAlgorithm(), key).doFinal(data);
        } catch (final IllegalBlockSizeException illegalblocksizeexception) {
            illegalblocksizeexception.printStackTrace();
        } catch (final BadPaddingException badpaddingexception) {
            badpaddingexception.printStackTrace();
        }

        LOGGER.error("Cipher data failed!");
        return null;
    }

    /**
     * Creates the Cipher Instance.
     *
     * @param opMode         The operation mode of the cipher. (this is one of the following: Cipher.ENCRYPT_MODE,
     *                       Cipher.DECRYPT_MODE, Cipher.WRAP_MODE or Cipher.UNWRAP_MODE)
     * @param transformation The name of the transformation, e.g. DES/CBC/PKCS5Padding.
     * @param key            The encryption key
     */
    private static Cipher createTheCipherInstance(final int opMode, final String transformation, final Key key) {
        try {
            final Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(opMode, key);
            return cipher;
        } catch (final InvalidKeyException invalidkeyexception) {
            invalidkeyexception.printStackTrace();
        } catch (final NoSuchAlgorithmException nosuchalgorithmexception) {
            nosuchalgorithmexception.printStackTrace();
        } catch (final NoSuchPaddingException nosuchpaddingexception) {
            nosuchpaddingexception.printStackTrace();
        }

        LOGGER.error("Cipher creation failed!");
        return null;
    }

    /**
     * Creates an Cipher instance using the AES/CFB8/NoPadding algorithm. Used for protocol encryption.
     *
     * @param opMode The operation mode of the cipher. (this is one of the following: Cipher.ENCRYPT_MODE,
     *               Cipher.DECRYPT_MODE, Cipher.WRAP_MODE or Cipher.UNWRAP_MODE)
     * @param key    The encryption key
     */
    public static Cipher createNetCipherInstance(final int opMode, final Key key) {
        try {
            final Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
            cipher.init(opMode, key, new IvParameterSpec(key.getEncoded()));
            return cipher;
        } catch (final GeneralSecurityException generalsecurityexception) {
            throw new RuntimeException(generalsecurityexception);
        }
    }
}
