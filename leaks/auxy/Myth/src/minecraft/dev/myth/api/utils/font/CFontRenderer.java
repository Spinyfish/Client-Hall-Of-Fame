package dev.myth.api.utils.font;

import dev.myth.api.utils.render.ColorUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CFontRenderer
		extends CFont {
	protected CharData[] boldChars = new CharData[256];
	protected CharData[] italicChars = new CharData[256];
	protected CharData[] boldItalicChars = new CharData[256];
	private final int[] colorCode = new int[32];
	private final String colorcodeIdentifiers = "0123456789abcdefklmnor";
	protected DynamicTexture texBold;
	protected DynamicTexture texItalic;
	protected DynamicTexture texItalicBold;

	public CFontRenderer(Font font, boolean antiAlias, boolean fractionalMetrics) {
		super(font, antiAlias, fractionalMetrics);
		this.setupMinecraftColorcodes();
		this.setupBoldItalicIDs();
	}

	public float drawStringWithShadow(String text, double x, double y, int color) {
		float shadowWidth = this.drawString(text, x + 0.5, y + 0.5, color, true);
		return Math.max(shadowWidth, this.drawString(text, x, y, color, false));
	}

	public float drawOutlinedString(String text, float x, float y, int color, int outlineColor) {
		return this.drawOutlinedString(text, x, y, color, outlineColor, 0.5F);
	}

	public float drawOutlinedString(String text, float x, float y, int color, int outlineColor, float outlineWidth) {
		this.drawString(text, x - outlineWidth, y, outlineColor);
		this.drawString(text, x, y - outlineWidth, outlineColor);
		this.drawString(text, x + outlineWidth, y, outlineColor);
		this.drawString(text, x, y + outlineWidth, outlineColor);
		return this.drawString(text, x, y, color);
	}

	public float drawString(String text, float x, float y, int color) {
		GlStateManager.color(1,1,1);

		float f = (color >> 24 & 0xFF) / 255.0F;
		float f1 = (color >> 16 & 0xFF) / 255.0F;
		float f2 = (color >> 8 & 0xFF) / 255.0F;
		float f3 = (color & 0xFF) / 255.0F;
		GL11.glColor4f(f1, f2, f3, f);

		return this.drawString(text, x, y, color, false);
	}

	public float drawCenteredString(String text, double x, double y, int color) {
		GlStateManager.color(1,1,1);

		return this.drawString(text, (float)(x - (float)(this.getStringWidth(text) / 2)), (float)y, color);

	}

	public float drawCenteredStringWithShadow(String text, float x, float y, int color) {
		return this.drawStringWithShadow(text, x - (float)(this.getStringWidth(text) / 2), y, color);
	}

	public float drawCenteredStringWithShadow(String text, double x, double y, int color) {
		return this.drawStringWithShadow(text, x - (double)(this.getStringWidth(text) / 2), y, color);
	}

	public float drawString(String text, double x, double y, int color, boolean shadow) {
		GlStateManager.enableBlend();
		x -= 1.0;
		if (text == null) {
			return 0.0f;
		}
		if (color == 553648127) {
			color = 16777215;
		}
		if ((color & -67108864) == 0) {
			color |= -16777216;
		}
		if (shadow) {
			color = new Color(0,0,0).getRGB();
		}
		CharData[] currentData = this.charData;
		float alpha = (float)(color >> 24 & 255) / 255.0f;
		boolean randomCase = false;
		boolean bold = false;
		boolean italic = false;
		boolean strikethrough = false;
		boolean underline = false;
		boolean render = true;
		x *= 2.0;
		y = (y - 3.0) * 2.0;
		if (render) {
			GL11.glPushMatrix();
			GlStateManager.scale(0.5, 0.5, 0.5);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(770, 771);
			GlStateManager.color((float)(color >> 16 & 255) / 255.0f, (float)(color >> 8 & 255) / 255.0f, (float)(color & 255) / 255.0f, alpha);
			int size = text.length();
			GlStateManager.enableTexture2D();
			GlStateManager.bindTexture(this.tex.getGlTextureId());
			GL11.glBindTexture(3553, this.tex.getGlTextureId());
			int i = 0;
			while (i < size) {
				char character = text.charAt(i);
				if (character == '\u00a7' && i < size) {
					int colorIndex = 21;
					try {
						colorIndex = "0123456789abcdefklmnor".indexOf(text.charAt(i + 1));
					}
					catch (Exception e) {
						e.printStackTrace();
					}
					if (colorIndex < 16) {
						bold = false;
						italic = false;
						randomCase = false;
						underline = false;
						strikethrough = false;
						GlStateManager.bindTexture(this.tex.getGlTextureId());
						currentData = this.charData;
						if (colorIndex < 0 || colorIndex > 15) {
							colorIndex = 15;
						}
						if (shadow) {
							colorIndex += 16;
						}
						int colorcode = this.colorCode[colorIndex];
						GlStateManager.color((float)(colorcode >> 16 & 255) / 255.0f, (float)(colorcode >> 8 & 255) / 255.0f, (float)(colorcode & 255) / 255.0f, alpha);
					} else if (colorIndex == 16) {
						randomCase = true;
					} else if (colorIndex == 17) {
						bold = true;
						if (italic) {
							GlStateManager.bindTexture(this.texItalicBold.getGlTextureId());
							currentData = this.boldItalicChars;
						} else {
							GlStateManager.bindTexture(this.texBold.getGlTextureId());
							currentData = this.boldChars;
						}
					} else if (colorIndex == 18) {
						strikethrough = true;
					} else if (colorIndex == 19) {
						underline = true;
					} else if (colorIndex == 20) {
						italic = true;
						if (bold) {
							GlStateManager.bindTexture(this.texItalicBold.getGlTextureId());
							currentData = this.boldItalicChars;
						} else {
							GlStateManager.bindTexture(this.texItalic.getGlTextureId());
							currentData = this.italicChars;
						}
					} else if (colorIndex == 21) {
						bold = false;
						italic = false;
						randomCase = false;
						underline = false;
						strikethrough = false;
						GlStateManager.color((float)(color >> 16 & 255) / 255.0f, (float)(color >> 8 & 255) / 255.0f, (float)(color & 255) / 255.0f, alpha);
						GlStateManager.bindTexture(this.tex.getGlTextureId());
						currentData = this.charData;
					}
					++i;
				} else if (character < currentData.length && character >= '\u0000') {
					GL11.glBegin(4);
					this.drawChar(currentData, character, (float)x, (float)y);
					GL11.glEnd();
					if (strikethrough) {
						this.drawLine(x, y + (double)(currentData[character].height / 2), x + (double)currentData[character].width - 8.0, y + (double)(currentData[character].height / 2), 1.0f);
					}
					if (underline) {
						this.drawLine(x, y + (double)currentData[character].height - 2.0, x + (double)currentData[character].width - 8.0, y + (double)currentData[character].height - 2.0, 1.0f);
					}
					x += currentData[character].width - 8 + this.charOffset;
				}
				++i;
			}
			GL11.glHint(3155, 4352);
			GL11.glPopMatrix();
		}
		GlStateManager.disableBlend();
		return (float)x / 2.0f;
	}

	@Override
	public int getStringWidth(String text) {
		if (text == null) {
			return 0;
		}
		int width = 0;
		CharData[] currentData = this.charData;
		boolean bold = false;
		boolean italic = false;
		int size = text.length();
		int i = 0;
		while (i < size) {
			char character = text.charAt(i);
			if (character == '\u00a7' && i < size) {
				int colorIndex = "0123456789abcdefklmnor".indexOf(character);
				if (colorIndex < 16) {
					bold = false;
					italic = false;
				} else if (colorIndex == 17) {
					bold = true;
					currentData = italic ? this.boldItalicChars : this.boldChars;
				} else if (colorIndex == 20) {
					italic = true;
					currentData = bold ? this.boldItalicChars : this.italicChars;
				} else if (colorIndex == 21) {
					bold = false;
					italic = false;
					currentData = this.charData;
				}
				++i;
			} else if (character < currentData.length && character >= '\u0000') {
				width += currentData[character].width - 8 + this.charOffset;
			}
			++i;
		}
		return width / 2;
	}

	@Override
	public void setFont(Font font) {
		super.setFont(font);
		this.setupBoldItalicIDs();
	}

	@Override
	public void setAntiAlias(boolean antiAlias) {
		super.setAntiAlias(antiAlias);
		this.setupBoldItalicIDs();
	}

	@Override
	public void setFractionalMetrics(boolean fractionalMetrics) {
		super.setFractionalMetrics(fractionalMetrics);
		this.setupBoldItalicIDs();
	}

	private void setupBoldItalicIDs() {
		this.texBold = this.setupTexture(this.font.deriveFont(1), this.antiAlias, this.fractionalMetrics, this.boldChars);
		this.texItalic = this.setupTexture(this.font.deriveFont(2), this.antiAlias, this.fractionalMetrics, this.italicChars);
	}

	private void drawLine(double x, double y, double x1, double y1, float width) {
		GL11.glDisable(3553);
		GL11.glLineWidth(width);
		GL11.glBegin(1);
		GL11.glVertex2d(x, y);
		GL11.glVertex2d(x1, y1);
		GL11.glEnd();
		GL11.glEnable(3553);
	}

	public List<String> wrapWords(String text, double width) {
		ArrayList<String> finalWords = new ArrayList<String>();
		if ((double)this.getStringWidth(text) > width) {
			String[] words = text.split(" ");
			String currentWord = "";
			int lastColorCode = 65535;
			String[] arrstring = words;
			int n = arrstring.length;
			int n2 = 0;
			while (n2 < n) {
				String word = arrstring[n2];
				int i = 0;
				while (i < word.toCharArray().length) {
					char c = word.toCharArray()[i];
					if (c == '\u00a7' && i < word.toCharArray().length - 1) {
						lastColorCode = word.toCharArray()[i + 1];
					}
					++i;
				}
				if ((double)this.getStringWidth(currentWord + word + " ") < width) {
					currentWord = currentWord + word + " ";
				} else {
					finalWords.add(currentWord);
					currentWord = 167 + lastColorCode + word + " ";
				}
				++n2;
			}
			if (currentWord.length() > 0) {
				if ((double)this.getStringWidth(currentWord) < width) {
					finalWords.add(167 + lastColorCode + currentWord + " ");
					currentWord = "";
				} else {
					for (String s : this.formatString(currentWord, width)) {
						finalWords.add(s);
					}
				}
			}
		} else {
			finalWords.add(text);
		}
		return finalWords;
	}

	public List<String> formatString(String string, double width) {
		ArrayList<String> finalWords = new ArrayList<String>();
		String currentWord = "";
		int lastColorCode = 65535;
		char[] chars = string.toCharArray();
		int i = 0;
		while (i < chars.length) {
			char c = chars[i];
			if (c == '\u00a7' && i < chars.length - 1) {
				lastColorCode = chars[i + 1];
			}
			if ((double)this.getStringWidth(currentWord + c) < width) {
				currentWord = currentWord + c;
			} else {
				finalWords.add(currentWord);
				currentWord = String.valueOf(167 + lastColorCode) + c;
			}
			++i;
		}
		if (currentWord.length() > 0) {
			finalWords.add(currentWord);
		}
		return finalWords;
	}

	private void setupMinecraftColorcodes() {
		int index = 0;
		while (index < 32) {
			int noClue = (index >> 3 & 1) * 85;
			int red = (index >> 2 & 1) * 170 + noClue;
			int green = (index >> 1 & 1) * 170 + noClue;
			int blue = (index >> 0 & 1) * 170 + noClue;
			if (index == 6) {
				red += 85;
			}
			if (index >= 16) {
				red /= 4;
				green /= 4;
				blue /= 4;
			}
			this.colorCode[index] = (red & 255) << 16 | (green & 255) << 8 | blue & 255;
			++index;
		}
	}
}
