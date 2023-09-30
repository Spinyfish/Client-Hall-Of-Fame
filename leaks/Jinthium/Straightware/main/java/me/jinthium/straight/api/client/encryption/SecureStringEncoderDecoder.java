package me.jinthium.straight.api.client.encryption;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class SecureStringEncoderDecoder {

    private static final int KEY_SIZE = 256;
    private static final int IV_SIZE = 16;

    public static String encrypt(String plaintext) {
        try {
            byte[] plaintextBytes = plaintext.getBytes(StandardCharsets.UTF_8);
            byte[] ivBytes = generateIV();
            byte[] keyBytes = generateKey();
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);
            byte[] ciphertext = cipher.doFinal(plaintextBytes);
            byte[] encrypted = new byte[ivBytes.length + ciphertext.length];
            System.arraycopy(ivBytes, 0, encrypted, 0, ivBytes.length);
            System.arraycopy(ciphertext, 0, encrypted, ivBytes.length, ciphertext.length);
            return Base64.getEncoder().encodeToString(encrypted) + ":" + Base64.getEncoder().encodeToString(keyBytes);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return "";
    }

    public static String decrypt(String ciphertext) {
        try {
            String[] parts = ciphertext.split(":");
            byte[] encrypted = Base64.getDecoder().decode(parts[0]);
            byte[] ivBytes = new byte[IV_SIZE];
            System.arraycopy(encrypted, 0, ivBytes, 0, ivBytes.length);
            byte[] ciphertextBytes = new byte[encrypted.length - ivBytes.length];
            System.arraycopy(encrypted, ivBytes.length, ciphertextBytes, 0, ciphertextBytes.length);
            byte[] keyBytes = Base64.getDecoder().decode(parts[1]);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);
            byte[] plaintext = cipher.doFinal(ciphertextBytes);
            return new String(plaintext, StandardCharsets.UTF_8);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "";
    }

    private static byte[] generateKey() throws Exception {
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[KEY_SIZE / 8];
        random.nextBytes(key);
        return key;
    }

    private static byte[] generateIV() {
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[IV_SIZE];
        random.nextBytes(iv);
        return iv;
    }
}