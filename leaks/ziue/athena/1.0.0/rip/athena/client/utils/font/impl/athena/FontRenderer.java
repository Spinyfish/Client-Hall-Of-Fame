package rip.athena.client.utils.font.impl.athena;

import java.awt.font.*;
import java.awt.image.*;
import net.minecraft.util.*;
import org.lwjgl.opengl.*;
import java.awt.geom.*;
import java.awt.*;
import org.lwjgl.*;
import net.minecraft.client.renderer.*;
import java.nio.*;
import rip.athena.client.utils.font.*;
import rip.athena.client.utils.render.*;

public class FontRenderer extends Font
{
    private static final String ALPHABET = "ABCDEFGHOKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String COLOR_CODE_CHARACTERS = "0123456789abcdefklmnor";
    private static final Color TRANSPARENT_COLOR;
    private static final float SCALE = 0.5f;
    private static final float SCALE_INVERSE = 2.0f;
    private static final char COLOR_INVOKER = '§';
    private static final int[] COLOR_CODES;
    private static final int LATIN_MAX_AMOUNT = 256;
    private static final int INTERNATIONAL_MAX_AMOUNT = 65535;
    private static final int MARGIN_WIDTH = 4;
    private static final int MASK = 255;
    private final java.awt.Font font;
    private final boolean fractionalMetrics;
    private final float fontHeight;
    private final FontCharacter[] defaultCharacters;
    private final FontCharacter[] internationalCharacters;
    private final FontCharacter[] boldCharacters;
    private boolean antialiasing;
    private boolean international;
    
    public FontRenderer(final java.awt.Font font, final boolean fractionalMetrics, final boolean antialiasing, final boolean international) {
        this.defaultCharacters = new FontCharacter[256];
        this.internationalCharacters = new FontCharacter[65535];
        this.boldCharacters = new FontCharacter[256];
        this.antialiasing = true;
        this.international = false;
        this.antialiasing = antialiasing;
        this.font = font;
        this.fractionalMetrics = fractionalMetrics;
        this.fontHeight = (float)(font.getStringBounds("ABCDEFGHOKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz", new FontRenderContext(new AffineTransform(), antialiasing, fractionalMetrics)).getHeight() / 2.0);
        this.fillCharacters(this.defaultCharacters, 0);
        this.fillCharacters(this.boldCharacters, 1);
        this.international = international;
        if (this.international) {
            this.fillCharacters(this.internationalCharacters, 0);
        }
    }
    
    public FontRenderer(final java.awt.Font font, final boolean fractionalMetrics, final boolean antialiasing) {
        this.defaultCharacters = new FontCharacter[256];
        this.internationalCharacters = new FontCharacter[65535];
        this.boldCharacters = new FontCharacter[256];
        this.antialiasing = true;
        this.international = false;
        this.antialiasing = antialiasing;
        this.font = font;
        this.fractionalMetrics = fractionalMetrics;
        this.fontHeight = (float)(font.getStringBounds("ABCDEFGHOKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz", new FontRenderContext(new AffineTransform(), antialiasing, fractionalMetrics)).getHeight() / 2.0);
        this.fillCharacters(this.defaultCharacters, 0);
        this.fillCharacters(this.boldCharacters, 1);
    }
    
    public FontRenderer(final java.awt.Font font, final boolean fractionalMetrics) {
        this.defaultCharacters = new FontCharacter[256];
        this.internationalCharacters = new FontCharacter[65535];
        this.boldCharacters = new FontCharacter[256];
        this.antialiasing = true;
        this.international = false;
        this.font = font;
        this.fractionalMetrics = fractionalMetrics;
        this.fontHeight = (float)(font.getStringBounds("ABCDEFGHOKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz", new FontRenderContext(new AffineTransform(), true, fractionalMetrics)).getHeight() / 2.0);
        this.fillCharacters(this.defaultCharacters, 0);
        this.fillCharacters(this.boldCharacters, 1);
    }
    
    public float getMiddleOfBox(final float height) {
        return height / 2.0f - (this.fontHeight - 8.0f) / 2.0f / 2.0f;
    }
    
    public static void calculateColorCodes() {
        for (int i = 0; i < 32; ++i) {
            final int amplifier = (i >> 3 & 0x1) * 85;
            int red = (i >> 2 & 0x1) * 170 + amplifier;
            int green = (i >> 1 & 0x1) * 170 + amplifier;
            int blue = (i & 0x1) * 170 + amplifier;
            if (i == 6) {
                red += 85;
            }
            if (i >= 16) {
                red /= 4;
                green /= 4;
                blue /= 4;
            }
            FontRenderer.COLOR_CODES[i] = ((red & 0xFF) << 16 | (green & 0xFF) << 8 | (blue & 0xFF));
        }
    }
    
    public void fillCharacters(final FontCharacter[] characters, final int style) {
        final java.awt.Font font = this.font.deriveFont(style);
        final BufferedImage fontImage = new BufferedImage(1, 1, 2);
        final Graphics2D fontGraphics = (Graphics2D)fontImage.getGraphics();
        final FontMetrics fontMetrics = fontGraphics.getFontMetrics(font);
        for (int i = 0; i < characters.length; ++i) {
            final char character = (char)i;
            final Rectangle2D charRectangle = fontMetrics.getStringBounds(character + "", fontGraphics);
            final BufferedImage charImage = new BufferedImage(MathHelper.ceiling_float_int((float)charRectangle.getWidth()) + 8, MathHelper.ceiling_float_int((float)charRectangle.getHeight()), 2);
            final Graphics2D charGraphics = (Graphics2D)charImage.getGraphics();
            charGraphics.setFont(font);
            final int width = charImage.getWidth();
            final int height = charImage.getHeight();
            charGraphics.setColor(FontRenderer.TRANSPARENT_COLOR);
            charGraphics.fillRect(0, 0, width, height);
            this.setRenderHints(charGraphics);
            charGraphics.drawString(character + "", 4, font.getSize());
            final int charTexture = GL11.glGenTextures();
            this.uploadTexture(charTexture, charImage, width, height);
            characters[i] = new FontCharacter(charTexture, (float)width, (float)height);
        }
    }
    
    public void setRenderHints(final Graphics2D graphics) {
        graphics.setColor(Color.WHITE);
        if (this.antialiasing) {
            graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, this.fractionalMetrics ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
    }
    
    public void uploadTexture(final int texture, final BufferedImage image, final int width, final int height) {
        final int[] pixels = image.getRGB(0, 0, width, height, new int[width * height], 0, width);
        final ByteBuffer byteBuffer = BufferUtils.createByteBuffer(width * height * 4);
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                final int pixel = pixels[x + y * width];
                byteBuffer.put((byte)(pixel >> 16 & 0xFF));
                byteBuffer.put((byte)(pixel >> 8 & 0xFF));
                byteBuffer.put((byte)(pixel & 0xFF));
                byteBuffer.put((byte)(pixel >> 24 & 0xFF));
            }
        }
        byteBuffer.flip();
        GlStateManager.bindTexture(texture);
        GL11.glTexParameteri(3553, 10241, 9728);
        GL11.glTexParameteri(3553, 10240, 9728);
        GL11.glTexImage2D(3553, 0, 6408, width, height, 0, 6408, 5121, byteBuffer);
    }
    
    @Override
    public int drawString(final String text, final double x, final double y, final int color) {
        return this.drawString(text, x, y, color, false);
    }
    
    @Override
    public int drawCenteredString(final String text, final double x, final double y, final int color) {
        return this.drawString(text, x - (this.width(text) >> 1), y, color, false);
    }
    
    @Override
    public int drawRightString(final String text, final double x, final double y, final int color) {
        return this.drawString(text, x - this.width(text), y, color, false);
    }
    
    @Override
    public int drawStringWithShadow(final String text, final double x, final double y, final int color) {
        this.drawString(text, x + 0.25, y + 0.25, color, true);
        return this.drawString(text, x, y, color, false);
    }
    
    public void drawCenteredStringWithShadow(final String text, final float x, final float y, final int color) {
        this.drawString(text, x - (this.width(text) >> 1) + 0.25, y + 0.25, new Color(color, true).getRGB(), true);
        this.drawString(text, x - (this.width(text) >> 1), y, color, false);
    }
    
    @Override
    public int drawString(final String text, double x, double y, final int color, final boolean shadow) {
        if (!this.international && this.requiresInternationalFont(text)) {
            return FontManager.getInternational(this.font.getSize() - 1).drawString(text, x, y, color);
        }
        final FontCharacter[] characterSet = this.international ? this.internationalCharacters : this.defaultCharacters;
        final double givenX = x;
        GL11.glPushMatrix();
        GL11.glPushAttrib(1048575);
        GL11.glEnable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        x -= 2.0;
        y -= 2.0;
        x *= 2.0;
        y *= 2.0;
        y -= this.fontHeight / 5.0f;
        final double startX = x;
        final int length = text.length();
        DrawUtils.glColor(shadow ? 50 : color);
        for (int i = 0; i < length; ++i) {
            final char character = text.charAt(i);
            try {
                if (character == '\n') {
                    x = startX;
                    y += this.height() * 2.0f;
                }
                else {
                    final FontCharacter fontCharacter = characterSet[character];
                    fontCharacter.render((float)x, (float)y);
                    x += fontCharacter.getWidth() - 8.0f;
                }
            }
            catch (Exception exception) {
                System.out.println("Character \"" + character + "\" was out of bounds (" + (int)character + " out of bounds for " + characterSet.length + ")");
                exception.printStackTrace();
            }
        }
        GL11.glDisable(3042);
        GL11.glDisable(3553);
        GlStateManager.bindTexture(0);
        GL11.glPopAttrib();
        GL11.glPopMatrix();
        return (int)(x - givenX);
    }
    
    @Override
    public int width(final String text) {
        if (!this.international && this.requiresInternationalFont(text)) {
            return FontManager.getInternational(this.font.getSize()).width(text);
        }
        final FontCharacter[] characterSet = this.international ? this.internationalCharacters : this.defaultCharacters;
        final int length = text.length();
        char previousCharacter = '.';
        int width = 0;
        for (int i = 0; i < length; ++i) {
            final char character = text.charAt(i);
            width += (int)(characterSet[character].getWidth() - 8.0f);
            previousCharacter = character;
        }
        return width / 2;
    }
    
    @Override
    public float height() {
        return this.fontHeight;
    }
    
    private boolean requiresInternationalFont(final String text) {
        int highest = 0;
        for (int i = 0; i < text.length(); ++i) {
            if (text.charAt(i) > highest) {
                highest = text.charAt(i);
            }
        }
        return highest >= 256;
    }
    
    static {
        TRANSPARENT_COLOR = new Color(255, 255, 255, 0);
        COLOR_CODES = new int[32];
    }
}
