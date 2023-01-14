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
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;
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

public class CryptManager {
   private static final Logger LOGGER = LogManager.getLogger();

   public static SecretKey createNewSharedKey() {
      try {
         KeyGenerator keygenerator = KeyGenerator.getInstance("AES");
         keygenerator.init(128);
         return keygenerator.generateKey();
      } catch (NoSuchAlgorithmException var1) {
         throw new Error(var1);
      }
   }

   public static KeyPair generateKeyPair() {
      try {
         KeyPairGenerator keypairgenerator = KeyPairGenerator.getInstance("RSA");
         keypairgenerator.initialize(1024);
         return keypairgenerator.generateKeyPair();
      } catch (NoSuchAlgorithmException var1) {
         var1.printStackTrace();
         LOGGER.error("Key pair generation failed!");
         return null;
      }
   }

   public static byte[] getServerIdHash(String serverId, PublicKey publicKey, SecretKey secretKey) {
      try {
         return digestOperation(serverId.getBytes("ISO_8859_1"), secretKey.getEncoded(), publicKey.getEncoded());
      } catch (UnsupportedEncodingException var4) {
         var4.printStackTrace();
         return null;
      }
   }

   private static byte[] digestOperation(byte[]... data) {
      try {
         MessageDigest messagedigest = MessageDigest.getInstance("SHA-1");

         for(byte[] abyte : data) {
            messagedigest.update(abyte);
         }

         return messagedigest.digest();
      } catch (NoSuchAlgorithmException var6) {
         var6.printStackTrace();
         return null;
      }
   }

   public static PublicKey decodePublicKey(byte[] encodedKey) {
      try {
         EncodedKeySpec encodedkeyspec = new X509EncodedKeySpec(encodedKey);
         KeyFactory keyfactory = KeyFactory.getInstance("RSA");
         return keyfactory.generatePublic(encodedkeyspec);
      } catch (InvalidKeySpecException | NoSuchAlgorithmException var3) {
         LOGGER.error("Public key reconstitute failed!");
         return null;
      }
   }

   public static SecretKey decryptSharedKey(PrivateKey key, byte[] secretKeyEncrypted) {
      return new SecretKeySpec(decryptData(key, secretKeyEncrypted), "AES");
   }

   public static byte[] encryptData(Key key, byte[] data) {
      return cipherOperation(1, key, data);
   }

   public static byte[] decryptData(Key key, byte[] data) {
      return cipherOperation(2, key, data);
   }

   private static byte[] cipherOperation(int opMode, Key key, byte[] data) {
      try {
         return Objects.requireNonNull(createTheCipherInstance(opMode, key.getAlgorithm(), key)).doFinal(data);
      } catch (BadPaddingException | IllegalBlockSizeException var4) {
         var4.printStackTrace();
         LOGGER.error("Cipher data failed!");
         return null;
      }
   }

   private static Cipher createTheCipherInstance(int opMode, String transformation, Key key) {
      try {
         Cipher cipher = Cipher.getInstance(transformation);
         cipher.init(opMode, key);
         return cipher;
      } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException var4) {
         var4.printStackTrace();
         LOGGER.error("Cipher creation failed!");
         return null;
      }
   }

   public static Cipher createNetCipherInstance(int opMode, Key key) {
      try {
         Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
         cipher.init(opMode, key, new IvParameterSpec(key.getEncoded()));
         return cipher;
      } catch (GeneralSecurityException var3) {
         throw new RuntimeException(var3);
      }
   }
}
