package best.azura.irc.utils;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAUtil {

    /**
     * Returns a random keypair for the RSA algorithm (private and public key)
     *
     * @return A random keypair for RSA
     * @throws NoSuchAlgorithmException If the algorithm doesn't exist (will probably never happen)
     */
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(1024);
        return generator.generateKeyPair();
    }

    /**
     * Get a public key from a string (RSA)
     *
     * @param key the string that has to be converted
     * @return the public key
     * @throws NoSuchAlgorithmException If the algorithm doesn't exist (will probably never happen)
     * @throws InvalidKeySpecException If the entered key is invalid
     */
    public static PublicKey getPublicKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {

        // Decode the key
        byte[] byteKey = Base64.getDecoder().decode(key);

        // Generate a new public key
        X509EncodedKeySpec publicKey = new X509EncodedKeySpec(byteKey);
        KeyFactory factory = KeyFactory.getInstance("RSA");

        // Return the generated key
        return factory.generatePublic(publicKey);
    }

    /**
     * Get a public key from a string (RSA)
     *
     * @param key the string that has to be converted
     * @return the public key
     * @throws NoSuchAlgorithmException If the algorithm doesn't exist (will probably never happen)
     * @throws InvalidKeySpecException If the entered key is invalid
     */
    public static PrivateKey getPrivateKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {

        // Decode the key
        byte[] byteKey = Base64.getDecoder().decode(key);

        // Generate a new public key
        X509EncodedKeySpec publicKey = new X509EncodedKeySpec(byteKey);
        KeyFactory factory = KeyFactory.getInstance("RSA");

        // Return the generated key
        return factory.generatePrivate(publicKey);
    }

    /**
     * Encrypt a text using the RSA algorithm
     *
     * @param text The text to encrypt
     * @param key The public key
     * @return The encrypted text
     */
    public static String encrypt(String text, Key key) {
        // Converting the text to bytes
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        try {

            // Create the encryption cipher
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

            // Init cipher by passing over the key
            cipher.init(Cipher.ENCRYPT_MODE, key);

            // Encrypt the text using the cypher
            byte[] encrypted = cipher.doFinal(bytes);

            // Return the encrypted string using an encoder
            return Base64.getEncoder().encodeToString(encrypted);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Decrypt a text using a private key
     *
     * @param text The encrypted text
     * @param key The private key
     * @return The decrypted text
     */
    public static String decrypt(String text, Key key) {

        // Decoding the bytes using a Base64 Decoder
        byte[] decoded = Base64.getDecoder().decode(text.getBytes());
        try {

            // Create the encryption cipher
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

            // Init the cypher using the decryption mode and private key
            cipher.init(Cipher.DECRYPT_MODE, key);

            // Decrypt the text using the cipher
            byte[] decrypted = cipher.doFinal(decoded);

            // Return the decrypted string
            return new String(decrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
