package de.fanta.utils;

import net.minecraft.util.MathHelper;

public class Colors {
   public static int getColor(int red, int green, int blue, int alpha) {
      int color = MathHelper.clamp_int(alpha, 0, 255) << 24;
      color |= MathHelper.clamp_int(red, 0, 255) << 16;
      color |= MathHelper.clamp_int(green, 0, 255) << 8;
      color |= MathHelper.clamp_int(blue, 0, 255);
      return color;
   }
   
   public static int[] getRGB(int hex) {
	   int a = (hex >> 24) & 0xFF;
	   int r = (hex >> 16) & 0xFF;
	   int g = (hex >> 8) & 0xFF;
	   int b = hex & 0xFF;
	   return new int[] {r, g, b, a};
   }
}
