/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package wtf.monsoon.api.util.font.impl;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import net.minecraft.client.renderer.texture.TextureUtil;
import org.lwjgl.opengl.GL11;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.render.ColorUtil;

public class Image {
    private final Font font;
    private final CharLocation[] charLocations;
    private int fontHeight = -1;
    private int textureID = 0;
    private int textureWidth = 0;
    private int textureHeight = 0;

    public Image(Font font, int startChar, int endChar) {
        this.font = font;
        this.charLocations = new CharLocation[endChar];
        this.renderBitmap(startChar, endChar);
    }

    public void drawString(String text, double x, double y, Color colour) {
        GL11.glPushMatrix();
        GL11.glScaled((double)0.25, (double)0.25, (double)0.0);
        GL11.glTranslated((double)(x * 2.0), (double)(y * 2.0), (double)0.0);
        GL11.glBindTexture((int)3553, (int)this.textureID);
        ColorUtil.glColor(colour.getRGB());
        double currX = 0.0;
        GL11.glBegin((int)7);
        for (char ch : text.toCharArray()) {
            CharLocation fontChar;
            if (Character.getNumericValue(ch) >= this.charLocations.length) {
                GL11.glEnd();
                GL11.glScaled((double)4.0, (double)4.0, (double)4.0);
                Wrapper.getMinecraft().fontRendererObj.drawString(String.valueOf(ch), (float)currX * 0.25f, 0.0f, colour.getRGB(), false);
                currX += (double)Wrapper.getMinecraft().fontRendererObj.getStringWidth(String.valueOf(ch)) * 4.0;
                GL11.glScaled((double)0.25, (double)0.25, (double)0.0);
                GL11.glBindTexture((int)3553, (int)this.textureID);
                ColorUtil.glColor(colour.getRGB());
                GL11.glBegin((int)7);
                continue;
            }
            if (this.charLocations.length <= ch || (fontChar = this.charLocations[ch]) == null) continue;
            this.drawChar(fontChar, (float)currX, 0.0f);
            currX += (double)fontChar.width - 8.0;
        }
        GL11.glEnd();
        GL11.glBindTexture((int)3553, (int)0);
        GL11.glPopMatrix();
    }

    private void drawChar(CharLocation ch, float x, float y) {
        float width = ch.width;
        float height = ch.height;
        float renderX = (float)ch.x / (float)this.textureWidth;
        float renderY = (float)ch.y / (float)this.textureHeight;
        float renderWidth = width / (float)this.textureWidth;
        float renderHeight = height / (float)this.textureHeight;
        GL11.glTexCoord2f((float)renderX, (float)renderY);
        GL11.glVertex2f((float)x, (float)y);
        GL11.glTexCoord2f((float)renderX, (float)(renderY + renderHeight));
        GL11.glVertex2f((float)x, (float)(y + height));
        GL11.glTexCoord2f((float)(renderX + renderWidth), (float)(renderY + renderHeight));
        GL11.glVertex2f((float)(x + width), (float)(y + height));
        GL11.glTexCoord2f((float)(renderX + renderWidth), (float)renderY);
        GL11.glVertex2f((float)(x + width), (float)y);
    }

    private void renderBitmap(int startChar, int endChar) {
        BufferedImage[] images = new BufferedImage[endChar];
        int row = 0;
        int charX = 0;
        int charY = 0;
        for (int targetChar = startChar; targetChar < endChar; ++targetChar) {
            BufferedImage fontImage = this.drawCharToImage((char)targetChar);
            CharLocation fontChar = new CharLocation(charX, charY, fontImage.getWidth(), fontImage.getHeight());
            if (fontChar.height > this.fontHeight) {
                this.fontHeight = fontChar.height;
            }
            if (fontChar.height > row) {
                row = fontChar.height;
            }
            if (this.charLocations.length <= targetChar) continue;
            this.charLocations[targetChar] = fontChar;
            images[targetChar] = fontImage;
            if ((charX += fontChar.width) <= 2048) continue;
            if (charX > this.textureWidth) {
                this.textureWidth = charX;
            }
            charX = 0;
            charY += row;
            row = 0;
        }
        this.textureHeight = charY + row;
        BufferedImage image = new BufferedImage(this.textureWidth, this.textureHeight, 2);
        Graphics2D graphics = (Graphics2D)image.getGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setFont(this.font);
        graphics.setColor(new Color(255, 255, 255, 0));
        graphics.fillRect(0, 0, this.textureWidth, this.textureHeight);
        graphics.setColor(Color.WHITE);
        for (int targetChar = startChar; targetChar < endChar; ++targetChar) {
            if (images[targetChar] == null || this.charLocations[targetChar] == null) continue;
            graphics.drawImage((java.awt.Image)images[targetChar], this.charLocations[targetChar].x, this.charLocations[targetChar].y, null);
        }
        this.textureID = TextureUtil.uploadTextureImageAllocate(GL11.glGenTextures(), image, true, true);
    }

    private BufferedImage drawCharToImage(char ch) {
        Graphics2D graphics = (Graphics2D)new BufferedImage(1, 1, 2).getGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setFont(this.font);
        FontMetrics metrics = graphics.getFontMetrics();
        int width = metrics.charWidth(ch) + 8;
        int height = metrics.getHeight() + 3;
        if (width <= 8) {
            width = 7;
        }
        if (height <= 0) {
            height = this.font.getSize();
        }
        BufferedImage fontImage = new BufferedImage(width, height, 2);
        graphics = (Graphics2D)fontImage.getGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setFont(this.font);
        graphics.setColor(Color.WHITE);
        graphics.drawString(String.valueOf(ch), 0, metrics.getAscent());
        return fontImage;
    }

    public int getStringWidth(String text) {
        int width = 0;
        for (int n : text.toCharArray()) {
            CharLocation fontChar;
            int index;
            int n2 = index = n < this.charLocations.length ? n : 3;
            if (this.charLocations.length <= index || (fontChar = this.charLocations[index]) == null) {
                width = (int)((double)width + (double)Wrapper.getMinecraft().fontRendererObj.getStringWidth(String.valueOf(n)) / 4.0);
                continue;
            }
            width += fontChar.width - 8;
        }
        return width / 2;
    }

    public float getHeight() {
        return ((float)this.fontHeight - 8.0f) / 2.0f;
    }

    public Font getFont() {
        return this.font;
    }

    private static class CharLocation {
        private final int x;
        private final int y;
        private final int width;
        private final int height;

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof CharLocation)) {
                return false;
            }
            CharLocation other = (CharLocation)o;
            if (!other.canEqual(this)) {
                return false;
            }
            if (this.getX() != other.getX()) {
                return false;
            }
            if (this.getY() != other.getY()) {
                return false;
            }
            if (this.getWidth() != other.getWidth()) {
                return false;
            }
            return this.getHeight() == other.getHeight();
        }

        protected boolean canEqual(Object other) {
            return other instanceof CharLocation;
        }

        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            result = result * 59 + this.getX();
            result = result * 59 + this.getY();
            result = result * 59 + this.getWidth();
            result = result * 59 + this.getHeight();
            return result;
        }

        public String toString() {
            return "Image.CharLocation(x=" + this.getX() + ", y=" + this.getY() + ", width=" + this.getWidth() + ", height=" + this.getHeight() + ")";
        }

        public CharLocation(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }
    }
}

