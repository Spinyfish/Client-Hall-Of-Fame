package me.uncodable.srt.impl.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtils {
   public static boolean isValueInRange(int comparedNo, int minimum, int maximum) {
      return comparedNo <= maximum && comparedNo >= minimum;
   }

   public static double round(double n, int places) {
      BigDecimal bigDecimal = new BigDecimal(n);
      return bigDecimal.setScale(places, RoundingMode.HALF_UP).doubleValue();
   }
}
