package me.uncodable.srt.impl.utils;

import net.minecraft.util.MathHelper;

public class EasingUtils {
   public static float easeOutCircle(float n) {
      return MathHelper.sqrt_float((float)(1.0 - Math.pow((double)n - 1.0, 2.0)));
   }

   public static float easeInOutSine(float n) {
      return (float)(-(Math.cos(Math.PI * (double)n) - 1.0) / 2.0);
   }

   public static float easeInSecantTest(float n) {
      return (float)Math.abs(1.0 - 1.0 / Math.cos((double)n));
   }

   public static float easeOutBack(float n) {
      float c1 = 1.70158F;
      float c3 = c1 + 1.0F;
      return (float)(1.0 + (double)c3 * Math.pow((double)(n - 1.0F), 3.0) + (double)c1 * Math.pow((double)(n - 1.0F), 2.0));
   }

   public static float easeOutBounce(float n) {
      float n1 = 7.5625F;
      float d1 = 2.75F;
      if (n < 1.0F / d1) {
         return n1 * n * n;
      } else if (n < 2.0F / d1) {
         float var5;
         return n1 * (var5 = n - 1.5F / d1) * var5 + 0.75F;
      } else {
         float var3;
         float var4;
         return n < 2.5F / d1 ? n1 * (var3 = n - 2.25F / d1) * var3 + 0.9375F : n1 * (var4 = n - 2.625F / d1) * var4 + 0.984375F;
      }
   }
}
