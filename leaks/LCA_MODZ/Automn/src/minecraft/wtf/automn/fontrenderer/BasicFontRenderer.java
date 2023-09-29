package wtf.automn.fontrenderer;

import net.minecraft.client.resources.IResourceManagerReloadListener;

import java.util.List;

public interface BasicFontRenderer extends IResourceManagerReloadListener {

  static String getFormatFromString(String text) {
    String s = "";
    int i = -1;
    int j = text.length();

    while ((i = text.indexOf(167, i + 1)) != -1) {
      if (i < j - 1) {
        char c0 = text.charAt(i + 1);

        if (isFormatColor(c0)) {
          s = "\u00a7" + c0;
        } else if (isFormatSpecial(c0)) {
          s = s + "\u00a7" + c0;
        }
      }
    }

    return s;
  }

  static boolean isFormatColor(char colorChar) {
    return colorChar >= 48 && colorChar <= 57 || colorChar >= 97 && colorChar <= 102 || colorChar >= 65 && colorChar <= 70;
  }

  /**
   * Checks if the char code is O-K...lLrRk-o... used to set special formatting.
   */
  static boolean isFormatSpecial(char formatChar) {
    return formatChar >= 107 && formatChar <= 111 || formatChar >= 75 && formatChar <= 79 || formatChar == 114 || formatChar == 82;
  }

  int getFontHeight();

  int drawStringWithShadow(String text, float x, float y, int color);

  int drawString(String text, float x, float y, int color);

  int drawString(String text, float x, float y, int color, boolean dropShadow);

  int drawCenteredString(String text, float x, float y, int color, boolean dropShadow);

  int getStringWidth(String text);

  int getCharWidth(char c);

  boolean getBidiFlag();

  void setBidiFlag(boolean state);

  String wrapFormattedStringToWidth(String str, int wrapWidth);

  List listFormattedStringToWidth(String str, int wrapWidth);

  String trimStringToWidth(String text, int width, boolean reverse);

  String trimStringToWidth(String text, int width);

  int getColorCode(char character);

  boolean isEnabled();

  boolean setEnabled(boolean state);

  void setFontRandomSeed(long seed);

  void drawSplitString(String str, int x, int y, int wrapWidth, int textColor);

  int splitStringWidth(String p_78267_1_, int p_78267_2_);

  boolean getUnicodeFlag();

  void setUnicodeFlag(boolean state);
}
