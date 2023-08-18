/* November.lol © 2023 */
package lol.november.utility.math;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

/**
 * @author Gavin
 * @since 1.0.0
 */
public class MathUtils {

  /**
   * The {@link Random} instance
   */
  private static final Random RNG = new Random();

  /**
   * A shortcut to the {@link Math#toRadians(double)} method
   */
  public static final double ANGLE_TO_RADIANS = Math.PI / 180.0;

  /**
   * Generates a random integer between min and max, inclusive
   *
   * @param min the minimum integer
   * @param max the maximum integer
   * @return a random integer between minimum and maximum (inclusive)
   */
  public static int random(int min, int max) {
    return RNG.nextInt((max + 1) - min) + min;
  }

  /**
   * Round a double to an amount of decimal places
   *
   * @param value the value
   * @param scale the scale
   * @return the rounded double
   */
  public static double round(double value, int scale) {
    return new BigDecimal(value)
      .setScale(scale, RoundingMode.HALF_DOWN)
      .doubleValue();
  }
}
