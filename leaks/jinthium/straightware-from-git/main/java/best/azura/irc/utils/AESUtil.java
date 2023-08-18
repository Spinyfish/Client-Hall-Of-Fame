package best.azura.irc.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class AESUtil {

    private static final String algorithm = "AES";

    /**
     * Returns an encrypted text using AES
     * @param text Text to encrypt
     * @return the encrypted text
     */
    public static String encrypt(String text, Key key) {

        try {

            //
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(text.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns the decrypted text
     * @param text Text to decrypt
     * @return the decrypted text
     */
    public static String decrypt(String text, Key key) {

        try {

            // Init decryptor
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, key);

            // Decode the in the encryption process applied Base64
            byte[] decrypted64 = Base64.getDecoder().decode(text);
            return new String(cipher.doFinal(decrypted64));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Key generateSecureKey() throws NoSuchAlgorithmException {
        KeyGenerator kg = KeyGenerator.getInstance(algorithm);
        return kg.generateKey();
    }

}
