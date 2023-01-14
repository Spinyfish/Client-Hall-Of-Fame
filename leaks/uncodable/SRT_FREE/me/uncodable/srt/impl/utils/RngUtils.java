package me.uncodable.srt.impl.utils;

import org.apache.commons.lang3.RandomUtils;

public class RngUtils {
   public static boolean isChance(int chance, int firstBound, int secondBound) {
      int roll = RandomUtils.nextInt(firstBound, secondBound);
      return roll <= chance;
   }
}
