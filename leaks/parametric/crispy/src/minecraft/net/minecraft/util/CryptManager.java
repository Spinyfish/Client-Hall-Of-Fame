package net.minecraft.util;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CryptManager
{
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Generate a new shared secret AES key from a secure random source
     */
    public static SecretKey createNewSharedKey()
    {
        try
        {
            KeyGenerator var0 = KeyGenerator.getInstance("AES");
            var0.init(128);
            return var0.generateKey();
        }
        catch (NoSuchAlgorithmException var1)
        {
            throw new Error(var1);
        }
    }

    /**
     * Generates RSA KeyPair
     */
    public static KeyPair generateKeyPair()
    {
        try
        {
            KeyPairGenerator var0 = KeyPairGenerator.getInstance("RSA");
            var0.initialize(1024);
            return var0.generateKeyPair();
        }
        catch (NoSuchAlgorithmException var1)
        {
            var1.printStackTrace();
            LOGGER.error("Key pair generation failed!");
            return null;
        }
    }

    /**
     * Compute a serverId hash for use by sendSessionRequest()
     *  
     * @param serverId The server ID
     * @param publicKey The public key
     * @param secretKey The secret key
     */
    public static byte[] getServerIdHash(String serverId, PublicKey publicKey, SecretKey secretKey)
    {
        try
        {
            return digestOperation("SHA-1", new byte[][] {serverId.getBytes("ISO_8859_1"), secretKey.getEncoded(), publicKey.getEncoded()});
        }
        catch (UnsupportedEncodingException var4)
        {
            var4.printStackTrace();
            return null;
        }
    }

    /**
     * Compute a message digest on arbitrary byte[] data
     *  
     * @param algorithm The name of the algorithm
     * @param data The data
     */
    private static byte[] digestOperation(String algorithm, byte[] ... data)
    {
        try
        {
            MessageDigest var2 = MessageDigest.getInstance(algorithm);
            byte[][] var3 = data;
            int var4 = data.length;

            for (int var5 = 0; var5 < var4; ++var5)
            {
                byte[] var6 = var3[var5];
                var2.update(var6);
            }

            return var2.digest();
        }
        catch (NoSuchAlgorithmException var7)
        {
            var7.printStackTrace();
            return null;
        }
    }

    /**
     * Create a new PublicKey from encoded X.509 data
     *  
     * @param encodedKey The key, encoded to the X.509 standard
     */
    public static PublicKey decodePublicKey(byte[] encodedKey)
    {
        try
        {
            X509EncodedKeySpec var1 = new X509EncodedKeySpec(encodedKey);
            KeyFactory var2 = KeyFactory.getInstance("RSA");
            return var2.generatePublic(var1);
        }
        catch (NoSuchAlgorithmException var3)
        {
            ;
        }
        catch (InvalidKeySpecException var4)
        {
            ;
        }

        LOGGER.error("Public key reconstitute failed!");
        return null;
    }

    /**
     * Decrypt shared secret AES key using RSA private key
     *  
     * @param key The encryption key
     * @param secretKeyEncrypted The encrypted secret key
     */
    public static SecretKey decryptSharedKey(PrivateKey key, byte[] secretKeyEncrypted)
    {
        return new SecretKeySpec(decryptData(key, secretKeyEncrypted), "AES");
    }

    /**
     * Encrypt byte[] data with RSA public key
     *  
     * @param key The encryption key
     * @param data The data
     */
    public static byte[] encryptData(Key key, byte[] data)
    {
        return cipherOperation(1, key, data);
    }

    /**
     * Decrypt byte[] data with RSA private key
     *  
     * @param key The encryption key
     * @param data The data
     */
    public static byte[] decryptData(Key key, byte[] data)
    {
        return cipherOperation(2, key, data);
    }

    /**
     * Encrypt or decrypt byte[] data using the specified key
     *  
     * @param opMode The operation mode of the cipher. (this is one of the following: Cipher.ENCRYPT_MODE,
     * Cipher.DECRYPT_MODE, Cipher.WRAP_MODE or Cipher.UNWRAP_MODE)
     * @param key The encryption key
     * @param data The data
     */
    private static byte[] cipherOperation(int opMode, Key key, byte[] data)
    {
        try
        {
            return createTheCipherInstance(opMode, key.getAlgorithm(), key).doFinal(data);
        }
        catch (IllegalBlockSizeException var4)
        {
            var4.printStackTrace();
        }
        catch (BadPaddingException var5)
        {
            var5.printStackTrace();
        }

        LOGGER.error("Cipher data failed!");
        return null;
    }

    /**
     * Creates the Cipher Instance.
     *  
     * @param opMode The operation mode of the cipher. (this is one of the following: Cipher.ENCRYPT_MODE,
     * Cipher.DECRYPT_MODE, Cipher.WRAP_MODE or Cipher.UNWRAP_MODE)
     * @param transformation The name of the transformation, e.g. DES/CBC/PKCS5Padding.
     * @param key The encryption key
     */
    private static Cipher createTheCipherInstance(int opMode, String transformation, Key key)
    {
        try
        {
            Cipher var3 = Cipher.getInstance(transformation);
            var3.init(opMode, key);
            return var3;
        }
        catch (InvalidKeyException var4)
        {
            var4.printStackTrace();
        }
        catch (NoSuchAlgorithmException var5)
        {
            var5.printStackTrace();
        }
        catch (NoSuchPaddingException var6)
        {
            var6.printStackTrace();
        }

        LOGGER.error("Cipher creation failed!");
        return null;
    }

    /**
     * Creates an Cipher instance using the AES/CFB8/NoPadding algorithm. Used for protocol encryption.
     *  
     * @param opMode The operation mode of the cipher. (this is one of the following: Cipher.ENCRYPT_MODE,
     * Cipher.DECRYPT_MODE, Cipher.WRAP_MODE or Cipher.UNWRAP_MODE)
     * @param key The encryption key
     */
    public static Cipher createNetCipherInstance(int opMode, Key key)
    {
        try
        {
            Cipher var2 = Cipher.getInstance("AES/CFB8/NoPadding");
            var2.init(opMode, key, new IvParameterSpec(key.getEncoded()));
            return var2;
        }
        catch (GeneralSecurityException var3)
        {
            throw new RuntimeException(var3);
        }
    }
}
