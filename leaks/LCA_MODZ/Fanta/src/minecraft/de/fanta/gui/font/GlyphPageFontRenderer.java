package de.fanta.gui.font;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import optifine.Config;
import optifine.CustomColors;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;

public class GlyphPageFontRenderer implements BasicFontRenderer {
	public Random fontRandom = new Random();
	/**
	 * Current X coordinate at which to draw the next character.
	 */
	private float posX;
	/**
	 * Current Y coordinate at which to draw the next character.
	 */
	private float posY;
	/**
	 * Array of RGB triplets defining the 16 standard chat colors followed by 16
	 * darker version of the same colors for drop shadows.
	 */
	private int[] colorCode = new int[32];
	/**
	 * Used to specify new red value for the current color.
	 */
	private float red;
	/**
	 * Used to specify new blue value for the current color.
	 */
	private float blue;
	/**
	 * Used to specify new green value for the current color.
	 */
	private float green;
	/**
	 * Used to speify new alpha value for the current color.
	 */
	private float alpha;
	/**
	 * Text color of the currently rendering string.
	 */
	private int textColor;

	/**
	 * Set if the "k" style (random) is active in currently rendering string
	 */
	private boolean randomStyle;
	/**
	 * Set if the "l" style (bold) is active in currently rendering string
	 */
	private boolean boldStyle;
	/**
	 * Set if the "o" style (italic) is active in currently rendering string
	 */
	private boolean italicStyle;
	/**
	 * Set if the "n" style (underlined) is active in currently rendering string
	 */
	private boolean underlineStyle;
	/**
	 * Set if the "m" style (strikethrough) is active in currently rendering string
	 */
	private boolean strikethroughStyle;

	private GlyphPage regularGlyphPage, boldGlyphPage, italicGlyphPage, boldItalicGlyphPage;

	public GlyphPageFontRenderer(GlyphPage regularGlyphPage, GlyphPage boldGlyphPage, GlyphPage italicGlyphPage,
			GlyphPage boldItalicGlyphPage) {
		this.regularGlyphPage = regularGlyphPage;
		this.boldGlyphPage = boldGlyphPage;
		this.italicGlyphPage = italicGlyphPage;
		this.boldItalicGlyphPage = boldItalicGlyphPage;

		for (int i = 0; i < 32; ++i) {
			int j = (i >> 3 & 1) * 85;
			int k = (i >> 2 & 1) * 170 + j;
			int l = (i >> 1 & 1) * 170 + j;
			int i1 = (i & 1) * 170 + j;

			if (i == 6) {
				k += 85;
			}

			if (i >= 16) {
				k /= 4;
				l /= 4;
				i1 /= 4;
			}

			this.colorCode[i] = (k & 255) << 16 | (l & 255) << 8 | i1 & 255;
		}
	}

	public static GlyphPageFontRenderer create(String fontName, int size, boolean bold, boolean italic,
			boolean boldItalic) {
		char[] chars = new char[256];

		for (int i = 0; i < chars.length; i++) {
			chars[i] = (char) i;
		}

		GlyphPage regularPage;

		regularPage = new GlyphPage(new Font(fontName, Font.PLAIN, size), true, true);

		regularPage.generateGlyphPage(chars);
		regularPage.setupTexture();

		GlyphPage boldPage = regularPage;
		GlyphPage italicPage = regularPage;
		GlyphPage boldItalicPage = regularPage;

		if (bold) {
			boldPage = new GlyphPage(new Font(fontName, Font.BOLD, size), true, true);

			boldPage.generateGlyphPage(chars);
			boldPage.setupTexture();
		}

		if (italic) {
			italicPage = new GlyphPage(new Font(fontName, Font.ITALIC, size), true, true);

			italicPage.generateGlyphPage(chars);
			italicPage.setupTexture();
		}

		if (boldItalic) {
			boldItalicPage = new GlyphPage(new Font(fontName, Font.BOLD | Font.ITALIC, size), true, true);

			boldItalicPage.generateGlyphPage(chars);
			boldItalicPage.setupTexture();
		}

		return new GlyphPageFontRenderer(regularPage, boldPage, italicPage, boldItalicPage);
	}

	/**
	 * Draws the specified string.
	 */
	public int drawString(String text, float x, float y, int color, boolean dropShadow) {
		GlStateManager.enableAlpha();
		this.resetStyles();
		int i;

		if (dropShadow) {
			i = this.renderString(text, x + 1.0F, y + 1.0F, color, true);
			i = Math.max(i, this.renderString(text, x, y, color, false));
		} else {
			i = this.renderString(text, x, y, color, false);
		}

		return i;
	}

	@Override
	public int drawStringWithShadow(String text, float x, float y, int color) {
		return drawString(text, x, y, color, true);
	}

	public int drawString(String text, float x, float y, int color) {
		return drawString(text, x, y, color, false);
	}

	public int drawCenteredString(String text, float x, float y, int color, boolean dropShadow) {
		return drawString(text, x - getStringWidth(text) / 2, y, color, dropShadow);
	}

	/**
	 * Render single line string by setting GL color, current (posX,posY), and
	 * calling renderStringAtPos()
	 */
	private int renderString(String text, float x, float y, int color, boolean dropShadow) {
		if (text == null) {
			return 0;
		} else {

			if ((color & -67108864) == 0) {
				color |= -16777216;
			}

			if (dropShadow) {
				color = (color & 16579836) >> 2 | color & -16777216;
			}

			this.red = (float) (color >> 16 & 255) / 255.0F;
			this.blue = (float) (color >> 8 & 255) / 255.0F;
			this.green = (float) (color & 255) / 255.0F;
			this.alpha = (float) (color >> 24 & 255) / 255.0F;
			GlStateManager.color(this.red, this.blue, this.green, this.alpha);
			this.posX = x * 2.0f;
			this.posY = y * 2.0f;
			this.renderStringAtPos(text, dropShadow);
			return (int) (this.posX/2);
		}
	}

	/**
	 * Render a single line string at the current (posX,posY) and update posX
	 */
	private void renderStringAtPos(String text, boolean shadow) {
		GlyphPage glyphPage = getCurrentGlyphPage();

		glPushMatrix();

		glScaled(0.5, 0.5, 0.5);

		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableTexture2D();

		glyphPage.bindTexture();

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

		for (int i = 0; i < text.length(); ++i) {
			char c0 = text.charAt(i);

			if (c0 == 167 && i + 1 < text.length()) {
				int i1 = "0123456789abcdefklmnor".indexOf(text.toLowerCase(Locale.ENGLISH).charAt(i + 1));

				if (i1 < 16) {
					this.randomStyle = false;
					this.boldStyle = false;
					this.strikethroughStyle = false;
					this.underlineStyle = false;
					this.italicStyle = false;

					if (i1 < 0) {
						i1 = 15;
					}

					if (shadow) {
						i1 += 16;
					}

					int j1 = this.colorCode[i1];
					this.textColor = j1;

					GlStateManager.color((float) (j1 >> 16) / 255.0F, (float) (j1 >> 8 & 255) / 255.0F,
							(float) (j1 & 255) / 255.0F, this.alpha);
				} else if (i1 == 16) {
					this.randomStyle = true;
				} else if (i1 == 17) {
					this.boldStyle = true;
				} else if (i1 == 18) {
					this.strikethroughStyle = true;
				} else if (i1 == 19) {
					this.underlineStyle = true;
				} else if (i1 == 20) {
					this.italicStyle = true;
				} else {
					this.randomStyle = false;
					this.boldStyle = false;
					this.strikethroughStyle = false;
					this.underlineStyle = false;
					this.italicStyle = false;

					GlStateManager.color(this.red, this.blue, this.green, this.alpha);
				}

				++i;
			} else {
				glyphPage = getCurrentGlyphPage();

				glyphPage.bindTexture();

				float f = glyphPage.drawChar(c0, posX, posY);

				doDraw(f, glyphPage);
			}
		}

		glyphPage.unbindTexture();

		glPopMatrix();
	}

	private void doDraw(float f, GlyphPage glyphPage) {
		if (this.strikethroughStyle) {
			Tessellator tessellator = Tessellator.getInstance();
			WorldRenderer worldrenderer = tessellator.getWorldRenderer();
			GlStateManager.disableTexture2D();
			worldrenderer.begin(7, DefaultVertexFormats.POSITION);
			worldrenderer
					.pos((double) this.posX, (double) (this.posY + (float) (glyphPage.getMaxFontHeight() / 2)), 0.0D)
					.endVertex();
			worldrenderer.pos((double) (this.posX + f),
					(double) (this.posY + (float) (glyphPage.getMaxFontHeight() / 2)), 0.0D).endVertex();
			worldrenderer.pos((double) (this.posX + f),
					(double) (this.posY + (float) (glyphPage.getMaxFontHeight() / 2) - 1.0F), 0.0D).endVertex();
			worldrenderer.pos((double) this.posX,
					(double) (this.posY + (float) (glyphPage.getMaxFontHeight() / 2) - 1.0F), 0.0D).endVertex();
			tessellator.draw();
			GlStateManager.enableTexture2D();
		}

		if (this.underlineStyle) {
			Tessellator tessellator1 = Tessellator.getInstance();
			WorldRenderer worldrenderer1 = tessellator1.getWorldRenderer();
			GlStateManager.disableTexture2D();
			worldrenderer1.begin(7, DefaultVertexFormats.POSITION);
			int l = this.underlineStyle ? -1 : 0;
			worldrenderer1.pos((double) (this.posX + (float) l),
					(double) (this.posY + (float) glyphPage.getMaxFontHeight()), 0.0D).endVertex();
			worldrenderer1
					.pos((double) (this.posX + f), (double) (this.posY + (float) glyphPage.getMaxFontHeight()), 0.0D)
					.endVertex();
			worldrenderer1.pos((double) (this.posX + f),
					(double) (this.posY + (float) glyphPage.getMaxFontHeight() - 1.0F), 0.0D).endVertex();
			worldrenderer1.pos((double) (this.posX + (float) l),
					(double) (this.posY + (float) glyphPage.getMaxFontHeight() - 1.0F), 0.0D).endVertex();
			tessellator1.draw();
			GlStateManager.enableTexture2D();
		}

		this.posX += f;
	}

	private GlyphPage getCurrentGlyphPage() {
		if (boldStyle && italicStyle)
			return boldItalicGlyphPage;
		else if (boldStyle)
			return boldGlyphPage;
		else if (italicStyle)
			return italicGlyphPage;
		else
			return regularGlyphPage;
	}

	/**
	 * Reset all style flag fields in the class to false; called at the start of
	 * string rendering
	 */
	private void resetStyles() {
		this.randomStyle = false;
		this.boldStyle = false;
		this.italicStyle = false;
		this.underlineStyle = false;
		this.strikethroughStyle = false;
	}

	public int getFontHeight() {
		return regularGlyphPage.getMaxFontHeight() / 2;
	}

	public int getStringWidth(String text) {
		if (text == null) {
			return 0;
		}
		int width = 0;

		GlyphPage currentPage;

		String abc = "0123456789abcdefklmnor";
		for (int i = 0; i < abc.length(); i++) {
			text = text.replace("§" + abc.charAt(i), "");
		}

		int size = text.length();

		boolean on = false;

		for (int i = 0; i < size; i++) {
			char character = text.charAt(i);

			if (character == '§')
				on = true;
			else if (on && character >= '0' && character <= 'r') {
				int colorIndex = "0123456789abcdefklmnor".indexOf(character);
				if (colorIndex < 16) {
					boldStyle = false;
					italicStyle = false;
				} else if (colorIndex == 17) {
					boldStyle = true;
				} else if (colorIndex == 20) {
					italicStyle = true;
				} else if (colorIndex == 21) {
					boldStyle = false;
					italicStyle = false;
				}
				i++;
				on = false;
			} else {
				if (on)
					i--;

				character = text.charAt(i);

				currentPage = getCurrentGlyphPage();

				width += currentPage.getWidth(character) - 8;
			}
		}

		return width / 2;
	}

	/**
	 * Trims a string to fit a specified Width.
	 */
	public String trimStringToWidth(String text, int width) {
		return this.trimStringToWidth(text, width, false);
	}

	/**
	 * Trims a string to a specified width, and will reverse it if par3 is set.
	 */
	public String trimStringToWidth(String text, int maxWidth, boolean reverse) {
		StringBuilder stringbuilder = new StringBuilder();

		boolean on = false;

		int j = reverse ? text.length() - 1 : 0;
		int k = reverse ? -1 : 1;
		int width = 0;

		GlyphPage currentPage;

		for (int i = j; i >= 0 && i < text.length() && i < maxWidth; i += k) {
			char character = text.charAt(i);

			if (character == '§')
				on = true;
			else if (on && character >= '0' && character <= 'r') {
				int colorIndex = "0123456789abcdefklmnor".indexOf(character);
				if (colorIndex < 16) {
					boldStyle = false;
					italicStyle = false;
				} else if (colorIndex == 17) {
					boldStyle = true;
				} else if (colorIndex == 20) {
					italicStyle = true;
				} else if (colorIndex == 21) {
					boldStyle = false;
					italicStyle = false;
				}
				i++;
				on = false;
			} else {
				if (on)
					i--;

				character = text.charAt(i);

				currentPage = getCurrentGlyphPage();

				width += (currentPage.getWidth(character) - 8) / 2;
			}

			if (i > width) {
				break;
			}

			if (reverse) {
				stringbuilder.insert(0, character);
			} else {
				stringbuilder.append(character);
			}
		}

		return stringbuilder.toString();
	}

	@Override
	public int getCharWidth(char c) {
		return getStringWidth("" + c);
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
	}

	@Override
	public void setUnicodeFlag(boolean state) {
	}

	@Override
	public void setBidiFlag(boolean state) {
	}

	@Override
	public boolean getBidiFlag() {
		return false;
	}

	@Override
	public String wrapFormattedStringToWidth(String str, int wrapWidth) {
		int i = this.sizeStringToWidth(str, wrapWidth);

		if (str.length() <= i) {
			return str;
		} else {
			String s = str.substring(0, i);
			char c0 = str.charAt(i);
			boolean flag = c0 == 32 || c0 == 10;
			String s1 = getFormatFromString(s) + str.substring(i + (flag ? 1 : 0));
//			return s + "\n" + this.wrapFormattedStringToWidth(s1, wrapWidth);
			return s;
		}
	}

	public static String getFormatFromString(String text) {
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

	private int sizeStringToWidth(String str, int wrapWidth) {
		int i = str.length();
		float f = 0.0F;
		int j = 0;
		int k = -1;

		for (boolean flag = false; j < i; ++j) {
			char c0 = str.charAt(j);

			switch (c0) {
			case '\n':
				--j;
				break;

			case ' ':
				k = j;

			default:
				f += this.getCharWidthFloat(c0);

				if (flag) {
					++f;
				}

				break;

			case '\u00a7':
				if (j < i - 1) {
					++j;
					char c1 = str.charAt(j);

					if (c1 != 108 && c1 != 76) {
						if (c1 == 114 || c1 == 82 || isFormatColor(c1)) {
							flag = false;
						}
					} else {
						flag = true;
					}
				}
			}

			if (c0 == 10) {
				++j;
				k = j;
				break;
			}

			if (f > (float) wrapWidth) {
				break;
			}
		}

		return j != i && k != -1 && k < j ? k : j;
	}

	private static boolean isFormatColor(char colorChar) {
		return colorChar >= 48 && colorChar <= 57 || colorChar >= 97 && colorChar <= 102
				|| colorChar >= 65 && colorChar <= 70;
	}

	private static boolean isFormatSpecial(char formatChar) {
		return formatChar >= 107 && formatChar <= 111 || formatChar >= 75 && formatChar <= 79 || formatChar == 114
				|| formatChar == 82;
	}

	@Override
	public List listFormattedStringToWidth(String str, int wrapWidth) {
		return Arrays.asList(this.wrapFormattedStringToWidth(str, wrapWidth).split("\n"));
	}

	@Override
	public int getColorCode(char character) {
		int i = "0123456789abcdef".indexOf(character);

		if (i >= 0 && i < this.colorCode.length) {
			int j = this.colorCode[i];

			if (Config.isCustomColors()) {
				j = CustomColors.getTextColor(i, j);
			}

			return j;
		} else {
			return 16777215;
		}
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean setEnabled(boolean state) {
		return true;
	}

	@Override
	public void setFontRandomSeed(long seed) {
		fontRandom.setSeed(seed);
	}

	@Override
	public void drawSplitString(String str, int x, int y, int wrapWidth, int textColor) {
		this.resetStyles();
        this.textColor = textColor;
        str = this.trimStringNewline(str);
        this.renderSplitString(str, x, y, wrapWidth, false);
	}

	@Override
	public int splitStringWidth(String p_78267_1_, int p_78267_2_) {
		 return this.getFontHeight() * this.listFormattedStringToWidth(p_78267_1_, p_78267_2_).size();
	}

	@Override
	public boolean getUnicodeFlag() {
		return false;
	}

	private float getCharWidthFloat(char p_getCharWidthFloat_1_) {
		return getCharWidth(p_getCharWidthFloat_1_);
	}
	
	private int renderStringAligned(String text, int x, int y, int p_78274_4_, int color, boolean dropShadow)
    {
        return this.renderString(text, (float)x, (float)y, color, dropShadow);
    }

	private void renderSplitString(String str, int x, int y, int wrapWidth, boolean addShadow)
    {
        for (Object s : this.listFormattedStringToWidth(str, wrapWidth))
        {
            this.renderStringAligned((String) s, x, y, wrapWidth, this.textColor, addShadow);
            y += this.getFontHeight();
        }
    }
	
	private String trimStringNewline(String text)
    {
        while (text != null && text.endsWith("\n"))
        {
            text = text.substring(0, text.length() - 1);
        }

        return text;
    }
	
}
