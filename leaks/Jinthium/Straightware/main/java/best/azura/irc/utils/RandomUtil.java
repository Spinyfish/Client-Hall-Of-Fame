package best.azura.irc.utils;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Class used for Random Utilities or Utilities which use a Random Algorithm.
 */
public class RandomUtil {

    /**
     * Create a new 126 Byte Base64 String.
     * @return {@link String} A 126 Byte Base64 String.
     */
    public static String getRandomBase64String() {
        SecureRandom random = new SecureRandom();
        byte[] randomBytes = new byte[128];
        random.nextBytes(randomBytes);
        return Base64.getUrlEncoder().encodeToString(randomBytes);
    }

    /**
     * Create a new 126 Byte Base64 String.
     * @return {@link String} A 126 Byte Base64 String.
     */
    public static String getRandomBase64LowString() {
        SecureRandom random = new SecureRandom();
        byte[] randomBytes = new byte[16];
        random.nextBytes(randomBytes);
        return Base64.getUrlEncoder().encodeToString(randomBytes);
    }

}
