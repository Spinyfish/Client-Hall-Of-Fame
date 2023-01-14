package me.uncodable.srt.impl.utils;

import org.apache.commons.lang3.RandomUtils;

public class CombatUtils {
   private static int counter;
   private static final Timer TIMER = new Timer();

   public static double elevateCPS(int chance, long duration, double randMin, double randMax) {
      if (TIMER.elapsed(duration) && counter > 0) {
         --counter;
         TIMER.reset();
      } else if (counter > 40) {
         counter = 0;
      }

      if (RandomUtils.nextInt(0, 100) <= chance) {
         ++counter;
      }

      if (counter > 0) {
         return counter % 3 == 0 ? RandomUtils.nextDouble(randMin, randMax) : -RandomUtils.nextDouble(randMin, randMax);
      } else {
         return 0.0;
      }
   }

   public static void setCounter(int newVal) {
      counter = newVal;
   }
}
