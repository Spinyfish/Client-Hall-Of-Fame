package net.minecraft.util;

import java.io.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.spec.*;
import java.security.*;
import org.apache.logging.log4j.*;

public class CryptManager
{
    private static final Logger LOGGER;
    
    public static SecretKey createNewSharedKey() {
        try {
            final KeyGenerator keygenerator = KeyGenerator.getInstance("AES");
            keygenerator.init(128);
            return keygenerator.generateKey();
        }
        catch (NoSuchAlgorithmException nosuchalgorithmexception) {
            throw new Error(nosuchalgorithmexception);
        }
    }
    
    public static KeyPair generateKeyPair() {
        try {
            final KeyPairGenerator keypairgenerator = KeyPairGenerator.getInstance("RSA");
            keypairgenerator.initialize(1024);
            return keypairgenerator.generateKeyPair();
        }
        catch (NoSuchAlgorithmException nosuchalgorithmexception) {
            nosuchalgorithmexception.printStackTrace();
            CryptManager.LOGGER.error("Key pair generation failed!");
            return null;
        }
    }
    
    public static byte[] getServerIdHash(final String serverId, final PublicKey publicKey, final SecretKey secretKey) {
        try {
            return digestOperation("SHA-1", new byte[][] { serverId.getBytes("ISO_8859_1"), secretKey.getEncoded(), publicKey.getEncoded() });
        }
        catch (UnsupportedEncodingException unsupportedencodingexception) {
            unsupportedencodingexception.printStackTrace();
            return null;
        }
    }
    
    private static byte[] digestOperation(final String algorithm, final byte[]... data) {
        try {
            final MessageDigest messagedigest = MessageDigest.getInstance(algorithm);
            for (final byte[] abyte : data) {
                messagedigest.update(abyte);
            }
            return messagedigest.digest();
        }
        catch (NoSuchAlgorithmException nosuchalgorithmexception) {
            nosuchalgorithmexception.printStackTrace();
            return null;
        }
    }
    
    public static PublicKey decodePublicKey(final byte[] encodedKey) {
        try {
            final EncodedKeySpec encodedkeyspec = new X509EncodedKeySpec(encodedKey);
            final KeyFactory keyfactory = KeyFactory.getInstance("RSA");
            return keyfactory.generatePublic(encodedkeyspec);
        }
        catch (NoSuchAlgorithmException ex) {}
        catch (InvalidKeySpecException ex2) {}
        CryptManager.LOGGER.error("Public key reconstitute failed!");
        return null;
    }
    
    public static SecretKey decryptSharedKey(final PrivateKey key, final byte[] secretKeyEncrypted) {
        return new SecretKeySpec(decryptData(key, secretKeyEncrypted), "AES");
    }
    
    public static byte[] encryptData(final Key key, final byte[] data) {
        return cipherOperation(1, key, data);
    }
    
    public static byte[] decryptData(final Key key, final byte[] data) {
        return cipherOperation(2, key, data);
    }
    
    private static byte[] cipherOperation(final int opMode, final Key key, final byte[] data) {
        try {
            return createTheCipherInstance(opMode, key.getAlgorithm(), key).doFinal(data);
        }
        catch (IllegalBlockSizeException illegalblocksizeexception) {
            illegalblocksizeexception.printStackTrace();
        }
        catch (BadPaddingException badpaddingexception) {
            badpaddingexception.printStackTrace();
        }
        CryptManager.LOGGER.error("Cipher data failed!");
        return null;
    }
    
    private static Cipher createTheCipherInstance(final int opMode, final String transformation, final Key key) {
        try {
            final Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(opMode, key);
            return cipher;
        }
        catch (InvalidKeyException invalidkeyexception) {
            invalidkeyexception.printStackTrace();
        }
        catch (NoSuchAlgorithmException nosuchalgorithmexception) {
            nosuchalgorithmexception.printStackTrace();
        }
        catch (NoSuchPaddingException nosuchpaddingexception) {
            nosuchpaddingexception.printStackTrace();
        }
        CryptManager.LOGGER.error("Cipher creation failed!");
        return null;
    }
    
    public static Cipher createNetCipherInstance(final int opMode, final Key key) {
        try {
            final Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
            cipher.init(opMode, key, new IvParameterSpec(key.getEncoded()));
            return cipher;
        }
        catch (GeneralSecurityException generalsecurityexception) {
            throw new RuntimeException(generalsecurityexception);
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
