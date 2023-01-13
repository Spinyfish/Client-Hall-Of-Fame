package net.optifine;

import net.minecraft.src.Config;

public class GlErrors {
   private static boolean frameStarted = false;
   private static long timeCheckStartMs = -1L;
   private static int countErrors = 0;
   private static int countErrorsSuppressed = 0;
   private static boolean suppressed = false;
   private static boolean oneErrorEnabled = false;

   public static void frameStart() {
      frameStarted = true;
      if (timeCheckStartMs < 0L) {
         timeCheckStartMs = System.currentTimeMillis();
      }

      if (System.currentTimeMillis() > timeCheckStartMs + 3000L) {
         if (countErrorsSuppressed > 0) {
            Config.error("Suppressed " + countErrors + " OpenGL errors");
         }

         suppressed = countErrors > 10;
         timeCheckStartMs = System.currentTimeMillis();
         countErrors = 0;
         countErrorsSuppressed = 0;
         oneErrorEnabled = true;
      }
   }

   public static boolean isEnabled() {
      if (!frameStarted) {
         return true;
      } else {
         ++countErrors;
         if (oneErrorEnabled) {
            oneErrorEnabled = false;
            return true;
         } else {
            if (suppressed) {
               ++countErrorsSuppressed;
            }

            return !suppressed;
         }
      }
   }
}
