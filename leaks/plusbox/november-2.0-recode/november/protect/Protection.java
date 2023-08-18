/* November.lol © 2023 */
package lol.november.protect;

import java.io.File;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import lol.november.protect.checks.Check;
import lol.november.protect.checks.JavaAgentCheck;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Log4j2
public class Protection {

  /**
   * The MD5 digest algorithm
   */
  private static MessageDigest MD5;

  /**
   * An {@link ArrayList} of checks
   */
  private static final List<Check> checks = new ArrayList<>();

  static {
    try {
      MD5 = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException ignored) {
      // algorithm for some reason doesn't exist, crash JVM
      crashJVM();
    }

    // add checks
    checks.add(new JavaAgentCheck());
  }

  /**
   * Validates November to check for tampering
   */
  public static void validate() {
    try {
      File file = new File(
        Protection.class.getProtectionDomain()
          .getCodeSource()
          .getLocation()
          .toURI()
          .getPath()
      );
      if (!file.exists()) crashJVM();

      String checksum = checksum(file);
      // TODO: check checksum of client jar with the server jar

      tsa();
    } catch (Exception ignored) {
      // we're not in a production environment, ignore
      log.warn("In a development environment, ignoring integrity checks");
    }
  }

  /**
   * Obvious reference to TSA, it basically runs checks to prevent cracking
   */
  private static void tsa() {
    if (checks.isEmpty()) crashJVM();

    List<String> failedChecks = new ArrayList<>();
    for (Check check : checks) {
      if (!check.check()) failedChecks.add(check.name());
    }

    if (!failedChecks.isEmpty()) {
      // TODO: send failed checks and their failure points to client websocket
      crashJVM();
    }
  }

  /**
   * Gets the checksum for a file
   *
   * @param file the {@link File} object
   * @return the checksum
   */
  @SneakyThrows
  private static String checksum(File file) {
    return checksum(Files.readAllBytes(file.toPath()));
  }

  @SneakyThrows
  public static String checksum(byte[] data) {
    return new BigInteger(1, MD5.digest(data)).toString(16);
  }

  /**
   * Crashes the JVM by causing an untraceable segfault
   */
  @SneakyThrows
  public static void crashJVM() {
    Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
    Field theUnsafeField = unsafeClass.getDeclaredField("theUnsafe");
    theUnsafeField.setAccessible(true);
    Object theUnsafe = theUnsafeField.get(null);
    theUnsafe
      .getClass()
      .getDeclaredMethod("putAddress", long.class, long.class)
      .invoke(theUnsafe, (long) 1, (long) 0);
  }
}
