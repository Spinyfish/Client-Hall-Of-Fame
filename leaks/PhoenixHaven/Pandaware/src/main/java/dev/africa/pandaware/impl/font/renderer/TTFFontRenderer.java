package dev.africa.pandaware.impl.font.renderer;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dev.africa.pandaware.Client;
import dev.africa.pandaware.impl.module.misc.StreamerModule;
import dev.africa.pandaware.utils.math.apache.ApacheMath;
import dev.africa.pandaware.utils.render.ColorUtils;
import lombok.AllArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TTFFontRenderer {
    private final Cache<String, Float> widthCache = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();
    private final Font font;
    private final CharacterData[] regularData;
    private final CharacterData[] boldData;
    private final CharacterData[] italicsData;
    private final int[] colorCodes;
    private final byte[] glyphWidth = new byte[65536];
    private final float[] charWidth = new float[256];
    private final int RANDOM_OFFSET = 1;
    public int FONT_HEIGHT = 9;
    private boolean fractionalMetrics;
    private boolean unicodeFlag;

    public TTFFontRenderer(Font font) {
        this(font, 256);
    }

    public TTFFontRenderer(Font font, int characterCount) {
        this(font, characterCount, true);
    }

    public TTFFontRenderer(Font font, int characterCount, boolean fractionalMetrics) {
        this.fractionalMetrics = false;
        this.colorCodes = new int[32];
        this.font = font;
        this.fractionalMetrics = fractionalMetrics;
        this.regularData = this.setup(new CharacterData[characterCount], 0);
        this.boldData = this.setup(new CharacterData[characterCount], 1);
        this.italicsData = this.setup(new CharacterData[characterCount], 2);
    }

    /**
     * Trims a string to fit a specified Width.
     */
    public String trimStringToWidth(String text, int width) {
        return this.trimStringToWidth(text, width, false);
    }

    public String trimStringToWidth(String text, int width, boolean reverse) {
        StringBuilder stringbuilder = new StringBuilder();
        float f = 0.0F;
        int i = reverse ? text.length() - 1 : 0;
        int j = reverse ? -1 : 1;
        boolean flag = false;
        boolean flag1 = false;

        for (int k = i; k >= 0 && k < text.length() && f < (float) width; k += j) {
            char c0 = text.charAt(k);
            float f1 = this.getCharWidthFloat(c0);

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

    public void drawRainbowStringWithShadow(String text, double x, double y, int fade, double speed) {
        double spacing = 0;
        for (char ch : text.toCharArray()) {
            drawStringWithShadow(String.valueOf(ch), (float) (spacing + x), (float) y, ColorUtils.rainbow((int) (spacing * fade), 1, speed).getRGB());
            spacing += getStringWidth(String.valueOf(ch)) - 2;
        }
    }

    public void drawRainbowString(String text, double x, double y, int fade, double speed) {
        double spacing = 0;
        for (char ch : text.toCharArray()) {
            drawString(String.valueOf(ch), (float) (spacing + x), (float) y, ColorUtils.rainbow((int) (spacing * fade), 1, speed).getRGB());
            spacing += getStringWidth(String.valueOf(ch)) - 2;
        }
    }

    public void drawCustomColorStringWithShadow(String text, double x, double y, Color firstColor, Color secondColor, float time, long timePerIndex, double speed) {
        double spacing = 0;
        for (char ch : text.toCharArray()) {
            drawStringWithShadow(String.valueOf(ch), (float) (spacing + x), (float) y, ColorUtils.getColorSwitch(firstColor, secondColor, time, (int) spacing, timePerIndex, speed).getRGB());
            spacing += getStringWidth(String.valueOf(ch)) - 2;
        }
    }

    public void drawCustomColorString(String text, double x, double y, Color firstColor, Color secondColor, float time, long timePerIndex, double speed) {
        double spacing = 0;
        for (char ch : text.toCharArray()) {
            drawString(String.valueOf(ch), (float) (spacing + x), (float) y, ColorUtils.getColorSwitch(firstColor, secondColor, time, (int) spacing, timePerIndex, speed).getRGB());
            spacing += getStringWidth(String.valueOf(ch)) - 2;
        }
    }

    public void setUnicodeFlag(boolean unicodeFlag) {
        this.unicodeFlag = unicodeFlag;
    }

    private CharacterData[] setup(CharacterData[] characterData, int type) {
        this.generateColors();
        Font font = this.font.deriveFont(type);
        BufferedImage utilityImage = new BufferedImage(1, 1, 2);
        Graphics2D utilityGraphics = (Graphics2D) utilityImage.getGraphics();
        utilityGraphics.setFont(font);
        FontMetrics fontMetrics = utilityGraphics.getFontMetrics();
        for (int index = 0; index < characterData.length; ++index) {
            char character = (char) index;
            Rectangle2D characterBounds = fontMetrics
                    .getStringBounds(String.valueOf(character), utilityGraphics);
            float width = (float) characterBounds.getWidth() + 8.0f;
            float height = (float) characterBounds.getHeight();
            BufferedImage characterImage = new BufferedImage(MathHelper.ceiling_double_int(width), MathHelper.ceiling_double_int(height), 2);
            Graphics2D graphics = (Graphics2D) characterImage.getGraphics();
            graphics.setFont(font);
            graphics.setColor(new Color(255, 255, 255, 0));
            graphics.fillRect(0, 0, characterImage.getWidth(), characterImage.getHeight());
            graphics.setColor(Color.WHITE);
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                    this.fractionalMetrics ? RenderingHints.VALUE_FRACTIONALMETRICS_ON
                            : RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
            graphics.drawString(String.valueOf(character), 4, fontMetrics.getAscent());
            int textureId = GL11.glGenTextures();
            this.createTexture(textureId, characterImage);
            characterData[index] = new CharacterData(character, characterImage.getWidth(), characterImage.getHeight(),
                    textureId);
        }
        return characterData;
    }

    private void createTexture(int textureId, BufferedImage image) {
        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);
        for (int y = 0; y < image.getHeight(); ++y) {
            for (int x = 0; x < image.getWidth(); ++x) {
                int pixel = pixels[y * image.getWidth() + x];
                buffer.put((byte) (pixel >> 16 & 0xFF));
                buffer.put((byte) (pixel >> 8 & 0xFF));
                buffer.put((byte) (pixel & 0xFF));
                buffer.put((byte) (pixel >> 24 & 0xFF));
            }
        }
        buffer.flip();
        GlStateManager.bindTexture(textureId);
        GL11.glTexParameteri(3553, 10241, 9728);
        GL11.glTexParameteri(3553, 10240, 9728);
        GL11.glTexImage2D(3553, 0, 6408, image.getWidth(), image.getHeight(), 0, 6408, 5121, buffer);
        GlStateManager.bindTexture(0);
    }

    public void drawString(String text, double x, double y, int color) {
        renderString(text, x, y, color, false);
    }

    public String drawStringWithShadow(String text, double x, double y, int color) {
        GL11.glTranslated(0.5, 0.5, 0.0);
        renderString(text, (float) x, (float) y, color, true);
        GL11.glTranslated(-0.5, -0.5, 0.0);
        renderString(text, (float) x, (float) y, color, false);
        return text;
    }

    public void drawCenteredString(String text, int x, int y, int color) {
        drawString(text, x - getStringWidth(text) / 2, (float) y, color);
    }

    public void drawCenteredStringWithShadow(String text, float x, float y, int color) {
        drawStringWithShadow(text, x - getStringWidth(text) / 2, y, color);
    }

    public void drawCenteredRainbowStringWithShadow(String text, float x, float y, int fade, int speed) {
        drawRainbowStringWithShadow(text, x - getStringWidth(text) / 2, y, fade, speed);
    }

    private void renderString(String text, double x, double y, int color, boolean shadow) {
        text = Client.getInstance().getUserManager().replaceString(text);

        if (text.length() <= 0) {
            return;
        }

        final Minecraft mc = Minecraft.getMinecraft();

        StreamerModule streamerModule = Client.getInstance().getModuleManager().getByClass(StreamerModule.class);

        if (mc != null && mc.thePlayer != null && mc.theWorld != null && mc.thePlayer.getName() != null && text.length() > 1) {
            if (streamerModule.getData().isEnabled()) {
                text = text.replaceAll(mc.thePlayer.getName(), "Legit Player");
            }

            if (streamerModule.getData().isEnabled() && mc.theWorld != null) {
                for (final String s : streamerModule.getPLAYERS()) {
                    text = text.replaceAll(s, "Player");
                }
            }
        }

        GlStateManager.pushAttribAndMatrix();
        GlStateManager.color(1, 1, 1, 0);
        GL11.glPushMatrix();
        GlStateManager.scale(0.5, 0.5, 1.0);
        x -= 2.0f;
        y -= 2.0f;
        x += 0.5f;
        y += 0.5f;
        x *= 2.0f;
        y *= 2.0f;
        CharacterData[] characterData = regularData;
        boolean underlined = false;
        boolean strikethrough = false;
        boolean obfuscated = false;
        int length = text.length();
        float multiplier = shadow ? 4 : 1;
        float a = (color >> 24 & 0xFF) / 255.0f;
        float r = (color >> 16 & 0xFF) / 255.0f;
        float g = (color >> 8 & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        GlStateManager.color(r / multiplier, g / multiplier, b / multiplier, a);
        for (int i = 0; i < length; ++i) {
            char character = text.charAt(i);
            char previous = (i > 0) ? text.charAt(i - 1) : '.';
            if (previous != '§') {
                if (character == '§' && i < length) {
                    int index = "0123456789abcdefklmnor".indexOf(text.toLowerCase(Locale.ENGLISH).charAt(i + 1));
                    if (index < 16) {
                        obfuscated = false;
                        strikethrough = false;
                        underlined = false;
                        characterData = regularData;
                        if (index < 0 || index > 15) {
                            index = 15;
                        }
                        if (shadow) {
                            index += 16;
                        }
                        int textColor = colorCodes[index];
                        GL11.glColor4d((textColor >> 16) / 255.0, (textColor >> 8 & 0xFF) / 255.0,
                                (textColor & 0xFF) / 255.0, a);
                    } else if (index == 16) {
                        obfuscated = true;
                    } else if (index == 17) {
                        characterData = boldData;
                    } else if (index == 18) {
                        strikethrough = true;
                    } else if (index == 19) {
                        underlined = true;
                    } else if (index == 20) {
                        characterData = italicsData;
                    } else if (index == 21) {
                        obfuscated = false;
                        strikethrough = false;
                        underlined = false;
                        characterData = regularData;
                        GL11.glColor4d((shadow ? 0.25 : 1.0), (shadow ? 0.25 : 1.0), (shadow ? 0.25 : 1.0), a);
                    }
                } else if (character <= '\u00ff') {
                    if (obfuscated) {
                        character += (char) this.RANDOM_OFFSET;
                    }
                    drawChar(character, characterData, x, y);
                    CharacterData charData = characterData[character];
                    if (strikethrough) {
                        drawLine(new Vector2f(0.0f, charData.height / 2.0f),
                                new Vector2f(charData.width, charData.height / 2.0f), 3.0f);
                    }
                    if (underlined) {
                        drawLine(new Vector2f(0.0f, charData.height - 15.0f),
                                new Vector2f(charData.width, charData.height - 15.0f), 3.0f);
                    }
                    x += charData.width - 8.0f;
                }
            }
        }
        GL11.glPopMatrix();
        GL11.glColor4d(1.0, 1.0, 1.0, 1.0);
        GlStateManager.bindTexture(0);
        GlStateManager.popAttribAndMatrix();
    }

    public float getHeight(String text) {
        float height = 0.0f;
        CharacterData[] characterData = this.regularData;
        for (int length = text.length(), i = 0; i < length; ++i) {
            char character = text.charAt(i);
            char previous = (i > 0) ? text.charAt(i - 1) : '.';
            if (previous != '§') {
                if (character == '§' && i < length) {
                    int index = "0123456789abcdefklmnor".indexOf(text.toLowerCase(Locale.ENGLISH).charAt(i + 1));
                    if (index == 17) {
                        characterData = this.boldData;
                    } else if (index == 20) {
                        characterData = this.italicsData;
                    } else if (index == 21) {
                        characterData = this.regularData;
                    }
                } else if (character <= '\u00ff') {
                    CharacterData charData = characterData[character];
                    height = ApacheMath.max(height, charData.height);
                }
            }
        }
        return height / 2.0f - 2.0f;
    }

    public float getStringWidth(String text) {
        text = Client.getInstance().getUserManager().replaceString(text);

        if (this.widthCache.asMap().containsKey(text) && this.widthCache.asMap() != null &&
                this.widthCache.asMap().get(text) != null) {
            return this.widthCache.asMap().get(text);
        }
        float width = 0.0f;
        CharacterData[] characterData = regularData;
        for (int length = text.length(), i = 0; i < length; ++i) {
            char character = text.charAt(i);
            char previous = (i > 0) ? text.charAt(i - 1) : '.';
            if (previous != '§') {
                if (character == '§' && i < length) {
                    int index = "0123456789abcdefklmnor".indexOf(text.toLowerCase(Locale.ENGLISH).charAt(i + 1));
                    if (index == 17) {
                        characterData = boldData;
                    } else if (index == 20) {
                        characterData = italicsData;
                    } else if (index == 21) {
                        characterData = regularData;
                    }
                } else if (character <= '\u00ff') {
                    CharacterData charData = characterData[character];
                    width += (charData.width - 8.0f) / 2.0f;
                }
            }
        }
        float length = width + 2f;

        this.widthCache.put(text, length);

        return length;
    }

    private float getCharWidthFloat(char p_78263_1_) {
        if (p_78263_1_ == 167) {
            return -1.0F;
        } else if (p_78263_1_ == 32) {
            return charWidth[32];
        } else {
            int var2 = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8\u00a3\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1\u00aa\u00ba\u00bf\u00ae\u00ac\u00bd\u00bc\u00a1\u00ab\u00bb\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261\u00b1\u2265\u2264\u2320\u2321\u00f7\u2248\u00b0\u2219\u00b7\u221a\u207f\u00b2\u25a0\u0000"
                    .indexOf(p_78263_1_);

            if (p_78263_1_ > 0 && var2 != -1 && !unicodeFlag) {
                return charWidth[var2];
            } else if (glyphWidth[p_78263_1_] != 0) {
                int var3 = glyphWidth[p_78263_1_] >>> 4;
                int var4 = glyphWidth[p_78263_1_] & 15;
                var3 &= 15;
                ++var4;
                return (var4 - var3) / 2 + 1;
            } else {
                return 0.0F;
            }
        }
    }

    private void drawChar(char character, CharacterData[] characterData, double x, double y) {
        CharacterData charData = characterData[character];
        charData.bind();
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex2d(x, y);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex2d(x, y + charData.height);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex2d(x + charData.width, y + charData.height);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex2d(x + charData.width, y);
        GL11.glEnd();
        GL11.glDisable(3042);
        GL11.glPopMatrix();
        GL11.glBindTexture(3553, 0);
    }

    private void drawLine(Vector2f start, Vector2f end, float width) {
        GL11.glDisable(3553);
        GL11.glLineWidth(width);
        GL11.glBegin(1);
        GL11.glVertex2f(start.x, start.y);
        GL11.glVertex2f(end.x, end.y);
        GL11.glEnd();
        GL11.glEnable(3553);
    }

    private void generateColors() {
        for (int i = 0; i < 32; ++i) {
            int thingy = (i >> 3 & 0x1) * 85;
            int red = (i >> 2 & 0x1) * 170 + thingy;
            int green = (i >> 1 & 0x1) * 170 + thingy;
            int blue = (i & 0x1) * 170 + thingy;
            if (i == 6) {
                red += 85;
            }
            if (i >= 16) {
                red /= 4;
                green /= 4;
                blue /= 4;
            }
            this.colorCodes[i] = ((red & 0xFF) << 16 | (green & 0xFF) << 8 | (blue & 0xFF));
        }
    }

    public Font getFont() {
        return this.font;
    }

    @AllArgsConstructor
    private static class CharacterData {
        public char character;
        public float width;
        public float height;
        private final int textureId;

        public void bind() {
            GL11.glBindTexture(3553, this.textureId);
        }
    }
}