package dev.eternal.client.util.render;

import dev.eternal.client.property.impl.interfaces.INameable;
import dev.eternal.client.util.math.MathUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.minecraft.entity.EntityLivingBase;

import java.awt.*;

@UtilityClass
public class ColorUtil {

  public int getColor(ColorMode colorMode, Color defaultValue, int position) {
    return switch (colorMode) {
      case STATIC -> defaultValue.getRGB();
      case RAINBOW_FAST -> ColorUtil.astolfoColors(-6000, position * 6);
      case SLOW_RAINBOW -> ColorUtil.astolfoColors(-6000, position * 3);
      case RAINBOW_SIMPLE -> ColorUtil.rainbow(6000, position * 15);
      case FADE -> RenderUtil.fade(defaultValue, (position / 11 * 2) + 10).getRGB();
    };
  }

  public static int pulse(int colorIn, int offset) {
    Color rgb = new Color(colorIn);
    float[] colors = new float[3];
    Color.RGBtoHSB(rgb.getRed(), rgb.getGreen(), rgb.getBlue(), colors);
    long time = (System.currentTimeMillis() + offset) / 16L;
    time %= 10000;
    float bright = 0.7F + (0.3F * (float) Math.sin(time / 25F));
    return Color.HSBtoRGB(colors[0], colors[1], bright);
  }

  public int rainbow(int speed, int timeOffset) {
    float hue = (System.currentTimeMillis() + timeOffset) % speed;
    return Color.HSBtoRGB(hue / (speed / 2F), 0.3F, 1F);
  }

  public int astolfoColors(int timeOffset, int yTotal) {
    final float speed = 4000F;
    float hue = (float) (System.currentTimeMillis() % (int) speed) + ((yTotal - timeOffset) * 9);
    while (hue > speed) {
      hue -= speed;
    }
    hue /= speed;
    if (hue > 0.5) {
      hue = 0.5F - (hue - 0.5f);
    }
    hue += 0.5F;
    return Color.HSBtoRGB(hue, .4F, 1F);
  }

  public int getHealthColor(EntityLivingBase entityLivingBase) {
    final float percentage = 100 * ((entityLivingBase.getHealth() / 2) / (entityLivingBase.getMaxHealth() / 2));
    return percentage > 75 ? 0x19ff1900 : percentage > 50 ? 0xffff0000 : percentage > 25 ? 0xff550000 : 0xff090000;
  }

  public Color lerp(Color color1, Color color2, double progress) {
    return new Color(
        (float) MathUtil.lerp(color1.getRed() / 255F, color2.getRed() / 255F, progress),
        (float) MathUtil.lerp(color1.getGreen() / 255F, color2.getGreen() / 255F, progress),
        (float) MathUtil.lerp(color1.getBlue() / 255F, color2.getBlue() / 255F, progress),
        (float) MathUtil.lerp(color1.getAlpha() / 255F, color2.getAlpha() / 255F, progress)
    );
  }

  @AllArgsConstructor
  @Getter
  public enum ColorMode implements INameable {

    STATIC("Static", false),
    RAINBOW_SIMPLE("Simple Rainbow", true),
    RAINBOW_FAST("Fast Rainbow", true),
    SLOW_RAINBOW("Slow Rainbow", true),
    FADE("Fade", false);

    private final String getName;
    private final boolean rainbow;
  }
}
