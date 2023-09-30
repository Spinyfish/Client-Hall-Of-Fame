package dev.africa.pandaware.impl.microshit.server;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class MicrosoftEncryption {

    public static String getEncryptionPassword() {
        return decode(
                "wDTyQl/aDOKJ+81FZr4VqJyMSsXC9n6gHGRcbZPeQ8Q=",
                "acwKCiuWT1IZwcHqhcCiABuyBnzYqrKDJbBWF6XAY8z8tcJkB4jCXaoKz4zB"
        );
    }

    public static String decode(String text, String key) {
        byte[] vector = {0x01, 0x02, 0x03, 0x05, 0x07, 0x0B, 0x0D, 0x11};
        byte[] keyArray = new byte[24];
        byte[] temporaryKey;

        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            temporaryKey = m.digest(key.getBytes(StandardCharsets.UTF_8));

            if (temporaryKey.length < 24) {
                int index = 0;
                for (int i = temporaryKey.length; i < 24; i++) {
                    keyArray[i] = temporaryKey[index];
                }
            }

            Cipher c = Cipher.getInstance("DESede/CBC/PKCS5Padding");
            c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyArray, "DESede"), new IvParameterSpec(vector));
            byte[] decrypted = c.doFinal(Base64.getMimeDecoder().decode(text));

            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception ignored) {
        }

        return null;
    }

    public static String encode(String text, String key) {
        byte[] keyArray = new byte[24];
        byte[] toEncryptArray;

        try {
            toEncryptArray = text.getBytes(StandardCharsets.UTF_8);
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] temporaryKey = messageDigest.digest(key.getBytes(StandardCharsets.UTF_8));

            if (temporaryKey.length < 24) {
                int index = 0;
                for (int i = temporaryKey.length; i < 24; ++i) {
                    keyArray[i] = temporaryKey[index];
                }
            }

            Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
            cipher.init(1, new SecretKeySpec(keyArray, "DESede"), new IvParameterSpec(
                    new byte[]{1, 2, 3, 5, 7, 11, 13, 17}));

            return "AUTH:" + Base64.getEncoder().encodeToString(cipher.doFinal(toEncryptArray));
        } catch (Exception ignored) {
        }

        return null;
    }
}
