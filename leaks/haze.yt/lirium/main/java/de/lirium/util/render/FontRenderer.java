package de.lirium.util.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import optifine.Config;
import optifine.CustomColors;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;

public class FontRenderer extends net.minecraft.client.gui.FontRenderer {

    private final int[] locationsPlain = new int[256 - 32],
            locationsBold = new int[256 - 32],
            locationsItalic = new int[256 - 32],
            locationsBoldItalic = new int[256 - 32],
            colorCode = new int[32];
    private final Texture plainTexture, boldTexture, italicTexture, boldItalicTexture;
    private Texture currentTexture;
    private boolean randomStyle, boldStyle, strikethroughStyle, underlineStyle, italicStyle;

    public FontRenderer(Font font) {
        super(Minecraft.getMinecraft().gameSettings, new ResourceLocation("textures/font/ascii.png"),
                Minecraft.getMinecraft().getTextureManager(), Minecraft.getMinecraft().getLanguageManager().isCurrentLocaleUnicode());
        plainTexture = createFontTexture(Font.PLAIN, font, locationsPlain);
        boldTexture = createFontTexture(Font.BOLD, font, locationsBold);
        italicTexture = createFontTexture(Font.ITALIC, font, locationsItalic);
        boldItalicTexture = createFontTexture(Font.BOLD | Font.ITALIC, font, locationsBoldItalic);
        currentTexture = plainTexture;
        FONT_HEIGHT = plainTexture.getHeight() / 2;
        for (int i = 0; i < 32; ++i) {
            int j = (i >> 3 & 1) * 85;
            int k = (i >> 2 & 1) * 170 + j;
            int l = (i >> 1 & 1) * 170 + j;
            int i1 = (i & 1) * 170 + j;
            if (i == 6) k += 85;
            if (Minecraft.getMinecraft().gameSettings.anaglyph) {
                int j1 = (k * 30 + l * 59 + i1 * 11) / 100;
                int k1 = (k * 30 + l * 70) / 100;
                int l1 = (k * 30 + i1 * 70) / 100;
                k = j1;
                l = k1;
                i1 = l1;
            }
            if (i >= 16) {
                k /= 4;
                l /= 4;
                i1 /= 4;
            }
            this.colorCode[i] = (k & 255) << 16 | (l & 255) << 8 | i1 & 255;
        }
    }

    private int[] getLocations() {
        if (currentTexture == boldItalicTexture)
            return locationsBoldItalic;
        if (currentTexture == boldTexture)
            return locationsBold;
        if (currentTexture == italicTexture)
            return locationsItalic;
        return locationsPlain;
    }

    private Texture createFontTexture(int type, Font font, int[] locations) {
        font = font.deriveFont(type);
        BufferedImage map = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        for (int i = 32; i < 256; i++) {
            char c = (char) i;
            final BufferedImage image = createCharImage(font, c, true);
            final BufferedImage backup = new BufferedImage(map.getWidth(), map.getHeight(), BufferedImage.TYPE_INT_ARGB);
            final Graphics2D backupGraphics = backup.createGraphics();
            backupGraphics.drawImage(map, 0, 0, null);
            backupGraphics.dispose();
            map = new BufferedImage((map.getWidth() == 1 ? 0 : map.getWidth()) + image.getWidth(), Math.max(map.getHeight(), image.getHeight()), BufferedImage.TYPE_INT_ARGB);
            final Graphics2D mapGraphics = map.createGraphics();
            mapGraphics.drawImage(backup, 0, 0, null);
            mapGraphics.drawImage(image, backup.getWidth(), 0, null);
            locations[c - 32] = backup.getWidth();
        }
        return new Texture(map, true);
    }

    public BufferedImage createCharImage(Font font, char c, boolean antiAlias) {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setFont(font);
        int charWidth = graphics.getFontMetrics().charWidth(c);
        int charHeight = graphics.getFontMetrics().getHeight();
        image = new BufferedImage(charWidth, charHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        if (antiAlias) g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(font);
        g.setPaint(Color.WHITE);
        g.drawString(String.valueOf(c), 0, g.getFontMetrics().getAscent());
        g.dispose();
        return image;
    }

    @Override
    public int drawString(String text, float x, float y, int color) {
        return drawString(text, x, y, color, false);
    }

    public void drawRainbowString(String text, int x, int y) {
        int xCharRainbow = 0;
        char[] by = text.toCharArray();
        for (int xChar = 0; xChar < text.length(); xChar++) {
            drawString(String.valueOf(by[xChar]), xCharRainbow + x, y, ColorUtil.getColor(2000, -xChar * 200).getRGB());
            xCharRainbow += getStringWidth(String.valueOf(by[xChar]));
        }
    }

    @Override
    public int drawString(String text, float x, float y, int color, boolean isShadow) {
        if (currentTexture == null || text == null || text.isEmpty()) return 0;
        char[] chars = text.toCharArray();
        boolean skipNext = false;
        int originColor = color;
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_CULL_FACE);
        for (char c0 : chars) {
            if (c0 == 167) {
                skipNext = true;
                continue;
            }
            if (skipNext) {
                if (c0 == 'r') {
                    color = originColor;
                    randomStyle = boldStyle = strikethroughStyle = underlineStyle = italicStyle = false;
                    if (currentTexture != plainTexture)
                        currentTexture = plainTexture;
                }
                try {
                    int i1 = "0123456789abcdefklmnor".indexOf(c0);
                    if (i1 < 16) {
                        randomStyle = boldStyle = strikethroughStyle = underlineStyle = italicStyle = false;
                        if (i1 < 0 || i1 > 15) i1 = 15;
                        if (isShadow) i1 += 16;
                        int l = this.colorCode[i1];
                        if (Config.isCustomColors()) l = CustomColors.getTextColor(i1, l);
                        color = l;
                    } else if (i1 == 16)
                        this.randomStyle = true;
                    else if (i1 == 17)
                        this.boldStyle = true;
                    else if (i1 == 18)
                        this.strikethroughStyle = true;
                    else if (i1 == 19)
                        this.underlineStyle = true;
                    else if (i1 == 20)
                        this.italicStyle = true;
                    if (boldStyle && italicStyle && currentTexture != boldItalicTexture)
                        currentTexture = boldItalicTexture;
                    else if (boldStyle && currentTexture != boldTexture)
                        currentTexture = boldTexture;
                    else if (italicStyle && currentTexture != italicTexture)
                        currentTexture = italicTexture;
                } catch (Exception ignored) {
                }
                skipNext = false;
                continue;
            }
            int darkerColor = (color & 16579836) >> 2 | color & -16777216;
            drawChar(c0, x, y, isShadow ? darkerColor : color);
            x += getStringWidth(Character.toString(c0));
        }
        glDisable(GL_BLEND);
        glEnable(GL_CULL_FACE);
        GlStateManager.resetColor();
        GlStateManager.bindTexture(0);
        currentTexture = plainTexture;
        randomStyle = boldStyle = strikethroughStyle = underlineStyle = italicStyle = false;
        return (int) x;
    }

    private void drawChar(char c, double x, double y, int color) {
        if (c > 256 || c < 32) {
            super.drawString(Character.toString(c), (float) x, (float) y, color, false);
            return;
        }
        if (!(c - 31 > 0 && c - 32 < getLocations().length)) return;
        final int i = getLocations()[c - 32], next = getLocations()[c - 31];
        glScaled(0.5, 0.5, 1);
        x *= 2.0;
        y *= 2.0;
        final Color color1 = new Color(color);
        glColor4f(color1.getRed() / 255.0F, color1.getGreen() / 255.0F, color1.getBlue() / 255.0F, color1.getAlpha() / 255.0F);
        final float w = (float) i / currentTexture.getWidth(), w1 = (float) next / currentTexture.getWidth();
        currentTexture.bind();
        GlStateManager.enableAlpha();
        glBegin(GL_QUADS);
        glTexCoord2f(w, 0);
        glVertex2d(x, y);
        glTexCoord2f(w1, 0);
        glVertex2d(x + (next - i), y);
        glTexCoord2f(w1, 1);
        glVertex2d(x + (next - i), y + currentTexture.getHeight());
        glTexCoord2f(w, 1);
        glVertex2d(x, y + currentTexture.getHeight());
        glEnd();
        currentTexture.unbind();
        if (strikethroughStyle) {
            glBegin(GL_QUADS);
            glVertex2d(x, y + currentTexture.getHeight() / 2.0 - 0.5);
            glVertex2d(x + (next - i), y + currentTexture.getHeight() / 2.0 - 0.5);
            glVertex2d(x + (next - i), y + currentTexture.getHeight() / 2.0 + 0.5);
            glVertex2d(x, y + currentTexture.getHeight() / 2.0 + 0.5);
            glEnd();
        }
        if (underlineStyle) {
            glBegin(GL_QUADS);
            glVertex2d(x, y + currentTexture.getHeight() - 1);
            glVertex2d(x + (next - i), y + currentTexture.getHeight() - 1);
            glVertex2d(x + (next - i), y + currentTexture.getHeight());
            glVertex2d(x, y + currentTexture.getHeight());
            glEnd();
        }
        glScaled(2, 2, 1);
        GlStateManager.disableAlpha();
        GlStateManager.resetColor();
        GlStateManager.color(1, 1, 1, 1);
    }

    public int drawStringWithShadow(String text, float x, float y, double shadowLength, int color) {
        if (text == null || text.isEmpty()) return 0;
        drawString(text, (float) (x + shadowLength), (float) (y + shadowLength), color, true);
        return drawString(text, x, y, color);
    }

    @Override
    public int drawStringWithShadow(String text, float x, float y, int color) {
        return drawStringWithShadow(text, x, y, 0.5F, color);
    }

    private static final int SPACING = 4;

    public int getStringWidth(String text) {
        int width = 0;
        for (char c0 : Objects.requireNonNull(TextFormatting.getTextWithoutFormattingCodes(text)).toCharArray()) {

            if(c0 == ' ') {
                width += 2;
                continue;
            }
             if (c0 > 256 || c0 < 32) {
                width += super.getStringWidth(Character.toString(c0));
                continue;
            }
            width += (getLocations()[c0 - 31] - getLocations()[c0 - 32]) / 2;
        }
        return (int) width;
    }

    @Override
    public String trimStringToWidth(String text, int width) {
        return this.trimStringToWidth(text, width, false);
    }

    @Override
    public String trimStringToWidth(String text, int width, boolean reverse) {
        StringBuilder stringbuilder = new StringBuilder();
        float f = 0.0F;
        int i = reverse ? text.length() - 1 : 0;
        int j = reverse ? -1 : 1;
        boolean flag = false;
        boolean flag1 = false;

        for (int k = i; k >= 0 && k < text.length() && f < (float) width; k += j) {
            final char c0 = text.charAt(k);
            if (!(c0 - 31 > 0 && c0 - 32 < getLocations().length)) continue;
            final float f1 = (getLocations()[c0 - 31] - getLocations()[c0 - 32]) / 2.0F;
            /*if (texture == null) continue;
            float f1 = texture.getWidth();*/

            if (flag) {
                flag = false;

                if (c0 != 108 && c0 != 76) {
                    if (c0 == 114 || c0 == 82) {
                        flag1 = false;
                    }
                } else {
                    flag1 = true;
                }
            } else if (f1 < 0.0F) {
                flag = true;
            } else {
                f += f1;

                if (flag1) {
                    ++f;
                }
            }

            if (f > (float) width) {
                break;
            }

            if (reverse) {
                stringbuilder.insert(0, c0);
            } else {
                stringbuilder.append(c0);
            }
        }

        return stringbuilder.toString();
    }


}