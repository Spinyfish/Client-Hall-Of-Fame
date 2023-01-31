package intent.AquaDev.aqua.utils;

import java.awt.Color;
import net.minecraft.util.MathHelper;

public class ColorUtils {
   public static Color getColorAlpha(Color color, int alpha) {
      return getColorAlpha(color.getRGB(), alpha);
   }

   public static Color getColorAlpha(int color, int alpha) {
      return new Color(new Color(color).getRed(), new Color(color).getGreen(), new Color(color).getBlue(), alpha);
   }

   public static int getColor(int red, int green, int blue, int alpha) {
      int color = MathHelper.clamp_int(alpha, 0, 255) << 24;
      color |= MathHelper.clamp_int(red, 0, 255) << 16;
      color |= MathHelper.clamp_int(green, 0, 255) << 8;
      return color | MathHelper.clamp_int(blue, 0, 255);
   }
}
