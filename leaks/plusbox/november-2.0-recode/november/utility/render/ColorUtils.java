/* November.lol © 2023 */
package lol.november.utility.render;

import java.awt.*;

/**
 * @author Gavin
 * @since 2.0.0
 */
public class ColorUtils {

  /**
   * @param firstColor  the first color
   * @param secondColor the second color
   * @param index       the index
   * @param speed       the speed to switch colors at
   * @return the switched color
   * @author G8LOL
   */
  public static Color colorSwitch(
    Color firstColor,
    Color secondColor,
    float index,
    double speed
  ) {
    long now = (long) (speed * System.currentTimeMillis() + -index * 10L);

    float time = 85.0f;

    float rd = (firstColor.getRed() - secondColor.getRed()) / time;
    float gd = (firstColor.getGreen() - secondColor.getGreen()) / time;
    float bd = (firstColor.getBlue() - secondColor.getBlue()) / time;

    float rd2 = (secondColor.getRed() - firstColor.getRed()) / time;
    float gd2 = (secondColor.getGreen() - firstColor.getGreen()) / time;
    float bd2 = (secondColor.getBlue() - firstColor.getBlue()) / time;

    int re1 = Math.round(secondColor.getRed() + rd * (now % (long) time));
    int ge1 = Math.round(secondColor.getGreen() + gd * (now % (long) time));
    int be1 = Math.round(secondColor.getBlue() + bd * (now % (long) time));
    int re2 = Math.round(firstColor.getRed() + rd2 * (now % (long) time));
    int ge2 = Math.round(firstColor.getGreen() + gd2 * (now % (long) time));
    int be2 = Math.round(firstColor.getBlue() + bd2 * (now % (long) time));

    if (now % ((long) time * 2L) < (long) time) {
      return new Color(re2, ge2, be2);
    } else {
      return new Color(re1, ge1, be1);
    }
  }

  public static Color fade(Color color, float minFactor, float factor) {
    float r = (float) color.getRed();
    float g = (float) color.getGreen();
    float b = (float) color.getBlue();

    return new Color(
      (int) Math.max(r * factor, r * minFactor),
      (int) Math.max(g * factor, g * minFactor),
      (int) Math.max(b * factor, b * minFactor)
    );
  }

  /**
   * Changes the alpha bit in a hex color
   *
   * @param color the color with an alpha value
   * @param alpha the new alpha value
   * @return the new color
   */
  public static int alpha(int color, int alpha) {
    return (color & 0x00ffffff) | (alpha << 24);
  }
}
