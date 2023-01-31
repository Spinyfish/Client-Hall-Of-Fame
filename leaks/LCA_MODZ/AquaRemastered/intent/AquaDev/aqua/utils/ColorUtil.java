package intent.AquaDev.aqua.utils;

import intent.AquaDev.aqua.Aqua;
import java.awt.Color;

public class ColorUtil {
   public static Color getClickGUIColor() {
      return Aqua.setmgr == null ? Color.white : new Color(Aqua.setmgr.getSetting("HUDColor").getColor());
   }

   public static int[] getRGB(int hex) {
      int a = hex >> 24 & 0xFF;
      int r = hex >> 16 & 0xFF;
      int g = hex >> 8 & 0xFF;
      int b = hex & 0xFF;
      return new int[]{r, g, b, a};
   }
}
