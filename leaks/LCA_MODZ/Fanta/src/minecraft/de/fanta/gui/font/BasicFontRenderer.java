package de.fanta.gui.font;

import java.util.List;

import net.minecraft.client.resources.IResourceManagerReloadListener;

public interface BasicFontRenderer extends IResourceManagerReloadListener {

	public int getFontHeight();
	public int drawStringWithShadow(String text, float x, float y, int color);
	public int drawString(String text, float x, float y, int color);
	public int drawString(String text, float x, float y, int color, boolean dropShadow);
	public int drawCenteredString(String text, float x, float y, int color, boolean dropShadow);
	public int getStringWidth(String text);
	public int getCharWidth(char c);
	public void setUnicodeFlag(boolean state);
	public void setBidiFlag(boolean state);
	public boolean getBidiFlag();
	public String wrapFormattedStringToWidth(String str, int wrapWidth);
	public List listFormattedStringToWidth(String str, int wrapWidth);
	public String trimStringToWidth(String text, int width, boolean reverse);
	public String trimStringToWidth(String text, int width);
	public int getColorCode(char character);
	public boolean isEnabled();
	public boolean setEnabled(boolean state);
	public void setFontRandomSeed(long seed);
	public void drawSplitString(String str, int x, int y, int wrapWidth, int textColor);
	public int splitStringWidth(String p_78267_1_, int p_78267_2_);
	public boolean getUnicodeFlag();
	public static String getFormatFromString(String text) {
		String s = "";
        int i = -1;
        int j = text.length();

        while ((i = text.indexOf(167, i + 1)) != -1)
        {
            if (i < j - 1)
            {
                char c0 = text.charAt(i + 1);

                if (isFormatColor(c0))
                {
                    s = "\u00a7" + c0;
                }
                else if (isFormatSpecial(c0))
                {
                    s = s + "\u00a7" + c0;
                }
            }
        }

        return s;
	}
	
	static boolean isFormatColor(char colorChar)
    {
        return colorChar >= 48 && colorChar <= 57 || colorChar >= 97 && colorChar <= 102 || colorChar >= 65 && colorChar <= 70;
    }

    /**
     * Checks if the char code is O-K...lLrRk-o... used to set special formatting.
     */
    static boolean isFormatSpecial(char formatChar)
    {
        return formatChar >= 107 && formatChar <= 111 || formatChar >= 75 && formatChar <= 79 || formatChar == 114 || formatChar == 82;
    }
}
