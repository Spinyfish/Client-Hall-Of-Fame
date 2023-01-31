package intent.AquaDev.aqua.utils;

import intent.AquaDev.aqua.Aqua;
import net.minecraft.util.StringUtils;

public class FontUtil {
   private static UnicodeFontRenderer fontRenderer;

   public static void setupFontUtils() {
      fontRenderer = Aqua.INSTANCE.comfortaa4;
   }

   public static int getStringWidth(String text) {
      return fontRenderer.getStringWidth(StringUtils.stripControlCodes(text));
   }

   public static int getFontHeight() {
      return 9;
   }

   public static void drawString(String text, double x, double y, int color) {
      fontRenderer.drawString(text, (float)((int)x), (float)((int)y), color);
   }

   public static void drawStringWithShadow(String text, double x, double y, int color) {
      fontRenderer.drawString(text, (float)((int)x), (float)((int)y), color);
   }

   public static void drawCenteredString(String text, double x, double y, int color) {
      drawString(text, x - (double)(fontRenderer.getStringWidth(text) / 2), y, color);
   }

   public static void drawCenteredStringWithShadow(String text, double x, double y, int color) {
      drawStringWithShadow(text, x - (double)(fontRenderer.getStringWidth(text) / 2), y, color);
   }

   public static void drawTotalCenteredString(String text, double x, double y, int color) {
      drawString(text, x - (double)(fontRenderer.getStringWidth(text) / 2), y - (double)(9 / 2), color);
   }

   public static void drawTotalCenteredStringWithShadow(String text, double x, double y, int color) {
      drawStringWithShadow(text, x - (double)(fontRenderer.getStringWidth(text) / 2), y - (double)(9.0F / 2.0F), color);
   }
}
