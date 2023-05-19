package club.marsh.bloom.impl.utils.render;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/*
 * Fork of old FontRenderer.
 *
 * @author darraghd493
 */
public class FontRenderer {
    private final Font font;
    private boolean fractionalMetrics = false;
    private final CharacterData[] regularData;
    private final CharacterData[] boldData;
    private final CharacterData[] italicsData;
    private final Color[] colourCodes = new Color[32];
    private static final int MARGIN = 4, RANDOM_OFFSET = 1;
    private static final char COLOUR_INVOKER = '\247';

    public FontRenderer(final Font font) {
        this(font, 256);
    }

    public FontRenderer(final Font font, final int characterCount) {
        this(font, characterCount, true);
    }

    public FontRenderer(final Font font, final boolean fractionalMetrics) {
        this(font, 256, fractionalMetrics);
    }

    public FontRenderer(final Font font, final int characterCount, final boolean fractionalMetrics) {
        this.font = font;
        this.fractionalMetrics = fractionalMetrics;


        this.regularData = setup(new CharacterData[characterCount], Font.PLAIN);
        this.boldData = setup(new CharacterData[characterCount], Font.BOLD);
        this.italicsData = setup(new CharacterData[characterCount], Font.ITALIC);
    }

    private CharacterData[] setup(final CharacterData[] characterData, final int type) {
        generateColours();
        final Font font = this.font.deriveFont(type);

        final BufferedImage utilityImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D utilityGraphics = (Graphics2D) utilityImage.getGraphics();
        utilityGraphics.setFont(font);

        final FontMetrics fontMetrics = utilityGraphics.getFontMetrics();

        for (int index = 0; index < characterData.length; index++) {
            final char character = (char) index;

            final Rectangle2D characterBounds = fontMetrics.getStringBounds(character + "", utilityGraphics);
            final float width = (float) characterBounds.getWidth() + (2 * MARGIN);
            final float height = (float) characterBounds.getHeight();

            final BufferedImage characterImage = new BufferedImage(ceilDI(width), ceilDI(height), BufferedImage.TYPE_INT_ARGB);

            final Graphics2D graphics = (Graphics2D) characterImage.getGraphics();
            graphics.setFont(font);
            graphics.setColor(new Color(255, 255, 255, 0));
            graphics.fillRect(0, 0, characterImage.getWidth(), characterImage.getHeight());
            graphics.setColor(Color.WHITE);

            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, this.fractionalMetrics ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF);

            graphics.drawString(character + "", MARGIN, fontMetrics.getAscent());

            final int textureId = GlStateManager.generateTexture();

            createTexture(textureId, characterImage);
            characterData[index] = new CharacterData(character, characterImage.getWidth(), characterImage.getHeight(), textureId);
        }

        return characterData;
    }


    private void createTexture(final int textureId, final BufferedImage image) {
        final int[] pixels = new int[image.getWidth() * image.getHeight()];

        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
        final ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                final int pixel = pixels[y * image.getWidth() + x];

                buffer.put((byte) ((pixel >> 16) & 0xFF));
                buffer.put((byte) ((pixel >> 8) & 0xFF));
                buffer.put((byte) (pixel & 0xFF));
                buffer.put((byte) ((pixel >> 24) & 0xFF));
            }
        }

        buffer.flip();

        GlStateManager.bindTexture(textureId);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, image.getWidth(), image.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        GlStateManager.bindTexture(0);
    }
    public void drawString(final String text, final int x, final int y, int color) {
    	drawString(text,(float) (x), (float) (y), color);
    }
    public void drawString(final String text, final int x, final int y, Color color) {
    	drawString(text,(float) (x), (float) (y), color.getRGB());
    }
    public void drawString(final String text, final float x, final float y, int colour) {

        if ((colour & -67108864) == 0)
        {
            colour |= -16777216;
        }

        Color color = new Color((float)(colour >> 16 & 255) / 255.0F,(float)(colour >> 8 & 255) / 255.0F,(float)(colour & 255) / 255.0F,(float)(colour >> 24 & 255) / 255.0F);
        
        renderString(text, x, y, color, false);
    }
    
    public void drawCenteredString(final String text, final float x, final float y, final Color colour) {
        renderString(text, x - getWidth(text) / 2, y - getHeight() / 2, colour, false);
    }

    public void drawStringWithShadow(final String text, final float x, final float y, final Color colour) {
        GL11.glTranslated(0.5, 0.5, 0);
        renderString(text, x, y, colour, true);
        GL11.glTranslated(-0.5, -0.5, 0);
        renderString(text, x, y, colour, false);
    }
    
    public void drawStringWithShadow(final String text, final float x, final float y, final int colour) {
        renderString(text, x + 1, y + 1, new Color(colour, true).darker().darker().darker().darker(), false);
    	renderString(text, x, y, new Color(colour, true), false);
    }

    public void drawCenteredStringWithShadow(final String text, final float x, final float y, final Color colour) {
        GL11.glTranslated(0.5, 0.5, 0);
        renderString(text, x - getWidth(text)/2, y, colour, true);
        GL11.glTranslated(-0.5, -0.5, 0);
        renderString(text, x - getWidth(text)/2, y, colour, false);
    }

    private void renderString(final String text, float x, float y, Color colour, final boolean shadow) {
        if (text.length() == 0) return;

        GlStateManager.pushMatrix();
        GlStateManager.scale(0.5, 0.5, 0.5);

        x -= MARGIN / 2f;
        y -= MARGIN / 2f;

        x += 0.5F;
        y += 0.5F;

        x *= 2;
        y *= 2;

        CharacterData[] characterData = regularData;

        boolean underlined = false;
        boolean strikethrough = false;
        boolean obfuscated = false;
        final int length = text.length();

        if (shadow) {
            // Taken from Minecraft's FontRenderer
            colour = new Color((colour.getRGB() & 16579836) >> 2 | colour.getRGB() & -16777216);
        }
        RenderUtil.colour(colour);

        for (int i = 0; i < length; i++) {
            char character = text.charAt(i);
            final char previous = i > 0 ? text.charAt(i - 1) : '.';

            if (previous == COLOUR_INVOKER) continue;
            if (character == COLOUR_INVOKER && i < length) {
                int index = "0123456789abcdefklmnor".indexOf(text.toLowerCase(Locale.ENGLISH).charAt(i + 1));
                if (index < 16) {
                    obfuscated = false;
                    strikethrough = false;
                    underlined = false;
                    characterData = regularData;

                    if (index < 0 || index > 15) index = 15;
                    if (shadow) index += 16;

                    final Color textColour = this.colourCodes[index];
                    RenderUtil.colour(RenderUtil.alpha(textColour, colour.getAlpha()));
                } else if (index == 16)
                    obfuscated = true;
                else if (index == 17)
                    characterData = boldData;
                else if (index == 18)
                    strikethrough = true;
                else if (index == 19)
                    underlined = true;
                else if (index == 20)
                    characterData = italicsData;
                else if (index == 21) {
                    obfuscated = false;
                    strikethrough = false;
                    underlined = false;
                    characterData = regularData;
                    RenderUtil.colour(RenderUtil.alpha(RenderUtil.multiply(Color.WHITE, shadow ? (float) 0.25 : 1), colour.getAlpha()));
                }
            } else {
                if (character > 255) continue;
                if (obfuscated)
                    character = (char) (((int) character) + RANDOM_OFFSET);

                drawChar(character, characterData, x, y);
                final CharacterData charData = characterData[character];

                if (strikethrough)
                    drawLine(new Vector2f(0, charData.height / 2f), new Vector2f(charData.width, charData.height / 2f), 3);

                if (underlined)
                    drawLine(new Vector2f(0, charData.height - 15), new Vector2f(charData.width, charData.height - 15), 3);

                x += charData.width - (2 * MARGIN);
            }
        }

        GlStateManager.popMatrix();

        GlStateManager.resetColor();
        GlStateManager.bindTexture(0);
    }


    public float getWidth(final String text) {
        float width = 0;

        CharacterData[] characterData = regularData;
        final int length = text.length();

        for (int i = 0; i < length; i++) {
            final char character = text.charAt(i);
            final char previous = i > 0 ? text.charAt(i - 1) : '.';
            
            if (previous == COLOUR_INVOKER) continue;
            if (character == COLOUR_INVOKER && i < length) {
                final int index = "0123456789abcdefklmnor".indexOf(text.toLowerCase(Locale.ENGLISH).charAt(i + 1));

                if (index == 17)
                    characterData = boldData;
                else if (index == 20)
                    characterData = italicsData;
                else if (index == 21)
                    characterData = regularData;
            } else {
                if (character > 255 || character == COLOUR_INVOKER) continue;
                final CharacterData charData = characterData[character];

                width += (charData.width - (2 * MARGIN)) / 2;
            }
        }

        return width + MARGIN / 2f;
    }

    public float getHeight(final String text) {
        float height = 0;

        CharacterData[] characterData = regularData;
        final int length = text.length();

        for (int i = 0; i < length; i++) {
            final char character = text.charAt(i);
            final char previous = i > 0 ? text.charAt(i - 1) : '.';

            if (previous == COLOUR_INVOKER) continue;
            if (character == COLOUR_INVOKER && i < length) {
                final int index = "0123456789abcdefklmnor".indexOf(text.toLowerCase(Locale.ENGLISH).charAt(i + 1));

                if (index == 17)
                    characterData = boldData;
                else if (index == 20)
                    characterData = italicsData;
                else if (index == 21)
                    characterData = regularData;
            } else {
                if (character > 255) continue;
                final CharacterData charData = characterData[character];

                height = Math.max(height, charData.height);
            }
        }

        return height / 2 - MARGIN / 2f;
    }

    public float getHeight() {
        return getHeight("A");
    }

    private void drawChar(final char character, final CharacterData[] characterData, final float x, final float y) {
        final CharacterData charData = characterData[character];

        charData.bind();
        GlStateManager.pushMatrix();

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glBegin(GL11.GL_QUADS);

        GL11.glTexCoord2f(0, 0);
        GL11.glVertex2d(x, y);
        GL11.glTexCoord2f(0, 1);
        GL11.glVertex2d(x, y + charData.height);
        GL11.glTexCoord2f(1, 1);
        GL11.glVertex2d(x + charData.width, y + charData.height);
        GL11.glTexCoord2f(1, 0);
        GL11.glVertex2d(x + charData.width, y);

        GL11.glEnd();

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }


    private void drawLine(final Vector2f start, final Vector2f end, final float width) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glLineWidth(width);

        GL11.glEnable(GL11.GL_LINES);
        GL11.glVertex2f(start.x, start.y);
        GL11.glVertex2f(end.x, end.y);

        GL11.glEnd();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }


    private void generateColours() {
        for (int i = 0; i < 32; i++) {
            final int offset = (i >> 3 & 1) * 85;
            int red = (i >> 2 & 1) * 170 + offset;
            int green = (i >> 1 & 1) * 170 + offset;
            int blue = (i >> 0 & 1) * 170 + offset;

            if (i == 6) red += 85;

            if (i >= 16) {
                red /= 4;
                green /= 4;
                blue /= 4;
            }

            this.colourCodes[i] = new Color(red, green, blue);
        }
    }

    public class CharacterData {
        public char character;
        public float width, height;
        private final int textureId;

        public CharacterData(final char character, final float width, final float height, final int textureId) {
            this.character = character;
            this.width = width;
            this.height = height;
            this.textureId = textureId;
        }

        public void bind() {

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
        }
    }

    // Utility methods - used for player/tab list
    public List<String> listFormattedStringToWidth(final String str, final int wrapWidth) {
        return Arrays.asList(this.wrapFormattedStringToWidth(str, wrapWidth).split("\n"));
    }

    public String wrapFormattedStringToWidth(final String str, final int wrapWidth) {
        if (str.length() <= 1) {
            return str;
        } else {
            final int i = this.sizeStringToWidth(str, wrapWidth);
            if (str.length() <= i) {
                return str;
            } else {
                final String s = str.substring(0, i);
                final char c0 = str.charAt(i);
                final boolean flag = c0 == 32 || c0 == 10;
                final String s1 = getFormatFromString(s) + str.substring(i + (flag ? 1 : 0));
                return s + "\n" + this.wrapFormattedStringToWidth(s1, wrapWidth);
            }
        }
    }

    private int sizeStringToWidth(final String str, final int wrapWidth) {
        final int i = str.length();
        float f = 0.0F;
        int j = 0;
        int k = -1;

        for (boolean flag = false; j < i; ++j) {
            final char c0 = str.charAt(j);
            switch (c0) {
                case '\n':
                    --j;
                    break;
                case ' ':
                    k = j;
                default:
                    f += this.getWidth(String.valueOf(c0));
                    if (flag)
                        ++f;
                    break;
                case '\u00a7':
                    if (j < i - 1) {
                        ++j;
                        final char c1 = str.charAt(j);
                        if (c1 != 108 && c1 != 76) {
                            if (c1 == 114 || c1 == 82 || isFormatColor(c1))
                                flag = false;
                        } else flag = true;
                    }
            }

            if (c0 == 10) {
                ++j;
                k = j;
                break;
            }

            if (Math.round(f) > wrapWidth)
                break;
        }

        return j != i && k != -1 && k < j ? k : j;
    }

    private static boolean isFormatColor(final char colorChar) {
        return colorChar >= 48 && colorChar <= 57 || colorChar >= 97 && colorChar <= 102 || colorChar >= 65 && colorChar <= 70;
    }

    private static boolean isFormatSpecial(final char formatChar) {
        return formatChar >= 107 && formatChar <= 111 || formatChar >= 75 && formatChar <= 79 || formatChar == 114 || formatChar == 82;
    }

    public static String getFormatFromString(final String text) {
        String s = "";
        int i = -1;
        final int j = text.length();

        while ((i = text.indexOf(167, i + 1)) != -1) {
            if (i < j - 1) {
                final char c0 = text.charAt(i + 1);

                if (isFormatColor(c0)) {
                    s = "\u00a7" + c0;
                } else if (isFormatSpecial(c0)) {
                    s = s + "\u00a7" + c0;
                }
            }
        }

        return s;
    }

    public float getSplitWidth(final String str, final int length) {
        return getHeight() * this.listFormattedStringToWidth(str, length).size();
    }

    private int ceilDI(final double value) {
        final int i = (int)value;
        return value > (double)i ? i + 1 : i;
    }
}

