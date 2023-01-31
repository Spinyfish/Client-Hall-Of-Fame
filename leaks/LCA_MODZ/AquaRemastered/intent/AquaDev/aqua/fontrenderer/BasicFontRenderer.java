package intent.AquaDev.aqua.fontrenderer;

import java.util.List;
import net.minecraft.client.resources.IResourceManagerReloadListener;

public interface BasicFontRenderer extends IResourceManagerReloadListener {
   static String getFormatFromString(String text) {
      String s = "";
      int i = -1;
      int j = text.length();

      while((i = text.indexOf(167, i + 1)) != -1) {
         if (i < j - 1) {
            char c0 = text.charAt(i + 1);
            if (isFormatColor(c0)) {
               s = "§" + c0;
            } else if (isFormatSpecial(c0)) {
               s = s + "§" + c0;
            }
         }
      }

      return s;
   }

   static boolean isFormatColor(char colorChar) {
      return colorChar >= '0' && colorChar <= '9' || colorChar >= 'a' && colorChar <= 'f' || colorChar >= 'A' && colorChar <= 'F';
   }

   static boolean isFormatSpecial(char formatChar) {
      return formatChar >= 'k' && formatChar <= 'o' || formatChar >= 'K' && formatChar <= 'O' || formatChar == 'r' || formatChar == 'R';
   }

   int getFontHeight();

   int drawStringWithShadow(String var1, float var2, float var3, int var4);

   int drawString(String var1, float var2, float var3, int var4);

   int drawString(String var1, float var2, float var3, int var4, boolean var5);

   int drawCenteredString(String var1, float var2, float var3, int var4, boolean var5);

   int getStringWidth(String var1);

   int getCharWidth(char var1);

   boolean getBidiFlag();

   void setBidiFlag(boolean var1);

   String wrapFormattedStringToWidth(String var1, int var2);

   List listFormattedStringToWidth(String var1, int var2);

   String trimStringToWidth(String var1, int var2, boolean var3);

   String trimStringToWidth(String var1, int var2);

   int getColorCode(char var1);

   boolean isEnabled();

   boolean setEnabled(boolean var1);

   void setFontRandomSeed(long var1);

   void drawSplitString(String var1, int var2, int var3, int var4, int var5);

   int splitStringWidth(String var1, int var2);

   boolean getUnicodeFlag();

   void setUnicodeFlag(boolean var1);
}
