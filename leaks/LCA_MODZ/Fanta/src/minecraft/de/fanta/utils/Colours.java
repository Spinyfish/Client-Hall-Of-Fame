package de.fanta.utils;

import java.awt.Color;

public class Colours {
   public static int getColor(int r, int g, int b, int a) {
      return (new Color(r, g, b, a)).getRGB();
   }

   public static int getLighterMain(int a) {
      return (new Color(60, 120, 200, a)).getRGB();
   }

   public static int getMain(int a) {
      return (new Color(50, 100, 200, a)).getRGB();
   }

   public static int getLightBlue(int a) {
      return (new Color(100, 150, 200, a)).getRGB();
   }
}
