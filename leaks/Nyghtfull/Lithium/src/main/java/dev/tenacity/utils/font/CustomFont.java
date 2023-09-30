package dev.tenacity.utils.font;

import dev.tenacity.utils.tuples.mutable.MutablePair;
import dev.tenacity.module.impl.render.Streamer;
import dev.tenacity.utils.misc.MathUtils;
import dev.tenacity.utils.render.GLUtil;
import dev.tenacity.utils.render.RenderUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author cedo
 * @since 05/12/2022
 */
public class CustomFont implements AbstractFontRenderer {
    private static int[] colorCode;
    private static final String colorcodeIdentifiers = "0123456789abcdefklmnor";
    private final Font font;
    @Setter
    @Getter
    private CustomFont boldFont;
    private final FontData regular = new FontData(Font.PLAIN), italic = new FontData(Font.ITALIC);

    private int fontHeight;
    private static final float KERNING = 8.2f;

    public CustomFont(Font font) {
        generateColorCodes();
        this.font = font;
        setupTexture(regular);
        setupTexture(italic);
    }

    private void setupTexture(FontData fontData) {
        //Set to 1 because we don't know the width and height of the map yet
        BufferedImage fakeImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = (Graphics2D) fakeImage.getGraphics();

        //Don't re-derive the font if it is plain
        Font currentFont = fontData.textType == 0 ? font : font.deriveFont(fontData.textType);
        graphics.setFont(currentFont);
        handleSprites(fontData, currentFont, graphics);
    }

    public void drawSmoothString(String text, double x2, float y2, int color) {
        this.drawString(text, x2, y2, color, false, KERNING, true);
    }

    public void drawSmoothStringWithShadow(String text, double x2, float y2, int color) {
        this.drawString(text, x2 + 0.5f, y2 + 0.5f, color, true, KERNING, true);

        this.drawString(text, x2, y2, color, false, KERNING, true);
    }

    @Override
    public int drawCenteredString(String name, float x, float y, int color) {
        return drawString(name, x - (getStringWidth(name) / 2), y, color);
    }

    @Override
    public void drawCenteredString(String name, float x, float y, Color color) {
        drawCenteredString(name, x, y, color.getRGB());
    }

    public void drawCenteredStringWithShadow(String text, float x, float y, int color) {
        this.drawStringWithShadow(text, x - (this.getStringWidth(text) / 2), y, color);
    }

    @Override
    public int drawStringWithShadow(String name, float x, float y, int color) {
        drawString(name, x + .5f, y + .5f, color, true, KERNING, false);
        return (int) drawString(name, x, y, color, false, KERNING, false);
    }

    @Override
    public void drawStringWithShadow(String name, float x, float y, Color color) {
        drawStringWithShadow(name, x, y, color.getRGB());
    }

    @Override
    public int drawString(String text, float x, float y, int color, boolean shadow) {
        if (shadow) return this.drawStringWithShadow(text, x, y, color);
        return (int) this.drawString(text, x, y, color, false, KERNING, false);
    }

    @Override
    public int drawString(String name, float x, float y, int color) {
        return drawString(name, x, y, color, false);
    }

    @Override
    public void drawString(String name, float x, float y, Color color) {
        drawString(name, x, y, color.getRGB(), false);
    }

    public float drawString(String text, double x, double y, int color, boolean shadow, float kerning, boolean smooth) {
        if (text == null) {
            return 0;
        }

        text = Streamer.filter(text);


        if (shadow) {
            color = (color & 0xFCFCFC) >> 2 | color & 0xFF000000;
        }

        GlStateManager.pushMatrix();
        GlStateManager.scale(.5, .5, .5);
        GLUtil.startBlend();
        RenderUtil.resetColor();
        RenderUtil.color(color);
        GlStateManager.enableTexture2D();
        GlStateManager.bindTexture(this.regular.texture.getGlTextureId());
        if (smooth) {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        } else {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        }

        float returnVal = drawCustomChars(text, x, y, kerning, color, shadow);

        GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_DONT_CARE);
        GlStateManager.popMatrix();
        RenderUtil.resetColor();
        GlStateManager.bindTexture(0);
        return returnVal;
    }


    private float drawCustomChars(String text, double x, double y, float kerning, int color, boolean shadow) {
        x = (x - 1) * 2;
        y = (y - 3) * 2;
        FontData currentData = this.regular;
        float alpha = (float) (color >> 24 & 255) / 255f;

        boolean bold = false, italic = false, strikethrough = false, underline = false;

        for (int index = 0; index < text.length(); index++) {
            char character = text.charAt(index);

            // If you need to apply color codes
            if (character == '§') {
                int colorIndex = 21;

                try {
                    colorIndex = colorcodeIdentifiers.indexOf(text.charAt(index + 1));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //If it's a color code
                if (colorIndex < 16) {
                    bold = false;
                    italic = false;
                    underline = false;
                    strikethrough = false;
                    GlStateManager.bindTexture(this.regular.texture.getGlTextureId());
                    currentData = this.regular;

                    if (colorIndex < 0) {
                        colorIndex = 15;
                    }

                    if (shadow) {
                        colorIndex += 16;
                    }

                    RenderUtil.color(colorCode[colorIndex], alpha);
                } else {
                    switch (colorIndex) {
                        case 17:
                            if (hasBoldFont()) {
                                bold = true;
                                if (italic) {
                                    GlStateManager.bindTexture(this.boldFont.italic.texture.getGlTextureId());
                                    currentData = this.boldFont.italic;
                                } else {
                                    GlStateManager.bindTexture(this.boldFont.regular.texture.getGlTextureId());
                                    currentData = this.boldFont.regular;
                                }
                            }
                            break;
                        case 18:
                            strikethrough = true;
                            break;
                        case 19:
                            underline = true;
                            break;
                        case 20:
                            italic = true;
                            if (bold && hasBoldFont()) {
                                GlStateManager.bindTexture(this.boldFont.italic.texture.getGlTextureId());
                                currentData = this.boldFont.italic;
                            } else {
                                GlStateManager.bindTexture(this.italic.texture.getGlTextureId());
                                currentData = this.italic;
                            }
                            break;
                        default:
                            bold = false;
                            italic = false;
                            underline = false;
                            strikethrough = false;
                            RenderUtil.color(color);
                            GlStateManager.bindTexture(this.regular.texture.getGlTextureId());
                            currentData = this.regular;
                            break;
                    }
                }

                ++index;
            } else if (character < currentData.chars.length) {

                drawLetter(x, y, currentData, strikethrough, underline, character);
                x += MathUtils.roundToHalf(currentData.chars[character].width - KERNING);
            }
        }
        return (float) (x / 2);
    }

    public void drawLetter(double x, double y, FontData currentData, boolean strikethrough, boolean underline, char character) {
        GL11.glBegin(GL11.GL_TRIANGLES);
        CharData charData = currentData.chars[character];
        drawQuad((float) x, (float) y, charData.width, charData.height, charData.storedX, charData.storedY, currentData.imageSize.getFirst(), currentData.imageSize.getSecond());
        GL11.glEnd();

        if (strikethrough) {
            this.drawLine(x, y + (double) (charData.height / 2), x + (double) charData.width - 8, y + (double) (charData.height / 2));
        }
        if (underline) {
            this.drawLine(x + 2.5f, y + (double) charData.height - 1, x + charData.width - 6, y + (double) charData.height - 1);
        }
    }

    protected void drawQuad(float x2, float y2, float width, float height, float srcX, float srcY, float imgWidth, float imgHeight) {
        float renderSRCX = srcX / imgWidth;
        float renderSRCY = srcY / imgHeight;
        float renderSRCWidth = width / imgWidth;
        float renderSRCHeight = height / imgHeight;
        GL11.glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY);
        GL11.glVertex2d(x2 + width, y2);
        GL11.glTexCoord2f(renderSRCX, renderSRCY);
        GL11.glVertex2d(x2, y2);
        GL11.glTexCoord2f(renderSRCX, renderSRCY + renderSRCHeight);
        GL11.glVertex2d(x2, y2 + height);
        GL11.glTexCoord2f(renderSRCX, renderSRCY + renderSRCHeight);
        GL11.glVertex2d(x2, y2 + height);
        GL11.glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY + renderSRCHeight);
        GL11.glVertex2d(x2 + width, y2 + height);
        GL11.glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY);
        GL11.glVertex2d(x2 + width, y2);
    }

    @Override
    public float getMiddleOfBox(float height) {
        return height / 2f - getHeight() / 2f;
    }

    @Override
    public String trimStringToWidth(String text, int width) {
        return this.trimStringToWidth(text, width, false);
    }

    @Override
    public String trimStringToWidth(String text, int width, boolean reverse) {
        if (text == null) return "";
        StringBuilder buffer = new StringBuilder();
        float lineWidth = 0.0F;
        int offset = reverse ? text.length() - 1 : 0;
        int increment = reverse ? -1 : 1;
        boolean var8 = false;
        boolean var9 = false;


        for (int index = offset; index >= 0 && index < text.length() && lineWidth < (float) width; index += increment) {
            char character = text.charAt(index);
            float charWidth = this.getCharWidthFloat(character);

            if (var8) {
                var8 = false;

                if (character != 108 && character != 76) {
                    if (character == 114 || character == 82) {
                        var9 = false;
                    }
                } else {
                    var9 = true;
                }
            } else if (charWidth < 0) {
                var8 = true;
            } else {
                lineWidth += charWidth;

                if (var9) {
                    ++lineWidth;
                }
            }

            if (lineWidth > (float) width) {
                break;
            }

            if (reverse) {
                buffer.insert(0, character);
            } else {
                buffer.append(character);
            }
        }

        return buffer.toString();
    }

    private float getCharWidthFloat(char c) {
        if (c == 167) {
            return -1;
        } else if (c == 32) {
            return 2;
        } else {
            int var2 = ("\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000").indexOf(c);

            if (c > 0 && var2 != -1) {
                return ((regular.chars[var2].width / 2.f) - 4.f);
            } else if (c < regular.chars.length && ((regular.chars[c].width / 2.f) - 4.f) != 0) {
                int var3 = ((int) ((regular.chars[c].width / 2.f) - 4.f)) >>> 4;
                int var4 = ((int) ((regular.chars[c].width / 2.f) - 4.f)) & 15;
                var3 &= 15;
                ++var4;
                return (float) ((var4 - var3) / 2 + 1);
            } else {
                return 0;
            }
        }
    }

    @Override
    public int getHeight() {
        return (this.fontHeight - 8) / 2;
    }

    @Override
    public float getStringWidth(String text) {
        return (float) getStringWidth(text, KERNING);
    }

    public double getStringWidth(String text, float kerning) {
        if (text == null) {
            return 0;
        }

        text = Streamer.filter(text);

        float width = 0;
        CharData[] currentData = regular.chars;
        for (int index = 0; index < text.length(); index++) {
            char character = text.charAt(index);

            if (character == '§') {
                int colorIndex = colorcodeIdentifiers.indexOf(text.charAt(index + 1));
                switch (colorIndex) {
                    case 17:
                        if (hasBoldFont()) {
                            currentData = this.boldFont.regular.chars;
                        }
                        break;
                    case 20:
                        currentData = this.regular.chars;
                        break;
                    default:
                        currentData = regular.chars;
                        break;
                }
                ++index;
            } else if (character < currentData.length) {
                width += currentData[character].width - kerning;
            }
        }

        return width / 2;

    }


    public boolean hasBoldFont() {
        return this.boldFont != null;
    }

    private final List<String> lines = new ArrayList<>();

    //Lines list and height
    public List<String> getWrappedLines(String text, float x, float width, float heightIncrement) {
        wrapTextToLines(text, x, width);
        return lines;
    }

    public float drawWrappedText(String text, float x, float y, int color, float width, float heightIncrement) {
        wrapTextToLines(text, x, width);

        float newY = y;
        for (String s : lines) {
            RenderUtil.resetColor();
            drawString(s, x, newY, color);
            newY += getHeight() + heightIncrement;
        }
        return newY - y;
    }

    public MutablePair<Float, Float> drawNewLineText(String text, float x, float y, int color, float heightIncrement) {
        wrapTextToNewLine(text, x);

        String longest = "";
        float newY = y;
        for (String s : lines) {
            if (getStringWidth(s) > getStringWidth(longest)) {
                longest = s;
            }
            RenderUtil.resetColor();
            drawString(s, x, newY, color);
            newY += getHeight() + heightIncrement;
        }

        return MutablePair.of(getStringWidth(longest), newY - y);
    }

    private void wrapTextToNewLine(String text, float x) {
        lines.clear();
        lines.addAll(Arrays.asList(text.trim().split("\n")));
    }

    private void wrapTextToLines(String text, float x, float width) {
        lines.clear();
        String[] words = text.trim().split(" ");
        StringBuilder line = new StringBuilder();
        for (String word : words) {
            float totalWidth = getStringWidth(line + " " + word);

            if (x + totalWidth >= x + width) {
                lines.add(line.toString());
                line = new StringBuilder(word).append(" ");
                continue;
            }

            line.append(word).append(" ");
        }
        lines.add(line.toString());
    }

    private void drawLine(double x2, double y2, double x1, double y1) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glLineWidth((float) 1);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2d(x2, y2);
        GL11.glVertex2d(x1, y1);
        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }


    private static class FontData {
        private final CharData[] chars = new CharData[256];
        private final int textType;
        private DynamicTexture texture;
        private final MutablePair<Integer, Integer> imageSize = MutablePair.of(512, 0);

        public FontData(int textType) {
            this.textType = textType;
        }
    }

    private static class CharData {
        private float width;
        private int height, storedX, storedY;
    }

    private void generateColorCodes() {
        if (colorCode == null) {
            colorCode = new int[32];
            for (int i = 0; i < 32; ++i) {
                final int noClue = (i >> 3 & 0x1) * 85;
                int red = (i >> 2 & 0x1) * 170 + noClue;
                int green = (i >> 1 & 0x1) * 170 + noClue;
                int blue = (i & 0x1) * 170 + noClue;
                if (i == 6) {
                    red += 85;
                }
                if (i >= 16) {
                    red /= 4;
                    green /= 4;
                    blue /= 4;
                }
                colorCode[i] = ((red & 0xFF) << 16 | (green & 0xFF) << 8 | (blue & 0xFF));
            }
        }
    }


    private void handleSprites(FontData fontData, Font currentFont, Graphics2D graphics2D) {
        handleSprites(fontData, currentFont, graphics2D, false);
    }

    private void handleSprites(FontData fontData, Font currentFont, Graphics2D graphics2D, boolean drawString) {
        int charHeight = 0;
        int positionX = 0;
        int positionY = 1;
        int index = 0;
        FontMetrics fontMetrics = graphics2D.getFontMetrics();

        if (drawString) {
            BufferedImage image = new BufferedImage(fontData.imageSize.getFirst(), fontData.imageSize.getSecond(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = (Graphics2D) image.getGraphics();

            graphics.setFont(currentFont);
            graphics.setColor(new Color(255, 255, 255, 0));
            graphics.fillRect(0, 0, fontData.imageSize.getFirst(), fontData.imageSize.getSecond());
            graphics.setColor(Color.WHITE);
            graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (CharData data : fontData.chars) {
                char c = (char) index;
                graphics.drawString(String.valueOf(c), data.storedX + 2, data.storedY + fontMetrics.getAscent());
                index++;
            }

            fontData.texture = new DynamicTexture(image);
        } else {
            while (index < fontData.chars.length) {
                char c = (char) index;
                CharData charData = new CharData();
                Rectangle2D dimensions = fontMetrics.getStringBounds(String.valueOf(c), graphics2D);
                charData.width = dimensions.getBounds().width + KERNING;
                charData.height = dimensions.getBounds().height;

                if (positionX + charData.width >= fontData.imageSize.getFirst()) {
                    positionX = 0;
                    positionY += charHeight;
                    charHeight = 0;
                }

                if (charData.height > charHeight) {
                    charHeight = charData.height;
                }

                charData.storedX = positionX;
                charData.storedY = positionY;

                if (charData.height > this.fontHeight) {
                    this.fontHeight = charData.height;
                }

                fontData.chars[index] = charData;
                positionX += charData.width;
                fontData.imageSize.setSecond(positionY + fontMetrics.getAscent());
                ++index;
            }

            //Call the method again so that the image is actually drawn
            handleSprites(fontData, currentFont, graphics2D, true);
        }

    }


}
