/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.texture.TextureUtil
 *  org.lwjgl.opengl.GL11
 *  vip.astroline.client.service.font.AstroFont$IntObject
 *  vip.astroline.client.storage.utils.render.render.RenderUtil
 */
package vip.astroline.client.service.font;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import org.lwjgl.opengl.GL11;
import vip.astroline.client.service.font.AstroFont;
import vip.astroline.client.storage.utils.render.render.RenderUtil;

public class AstroFont {
    public int IMAGE_WIDTH = 1024;
    public int IMAGE_HEIGHT = 1024;
    private int texID;
    private IntObject[] chars = new IntObject[2048];
    private final Font font;
    private boolean antiAlias;
    private int fontHeight = -1;
    private int charOffset = 8;

    public AstroFont(Font font, boolean antiAlias, int charOffset, boolean allChar, int yAddon) {
        this.font = font;
        this.antiAlias = antiAlias;
        this.charOffset = charOffset;
        if (charOffset == 0) {
            this.charOffset = 8;
        }
        this.setupTexture(antiAlias, allChar, yAddon);
    }

    public AstroFont(Font font, boolean antiAlias, boolean allChar, int yAddon) {
        this.font = font;
        this.antiAlias = antiAlias;
        this.charOffset = 6;
        this.setupTexture(antiAlias, allChar, yAddon);
    }

    private void setupTexture(boolean antiAlias, boolean allChar, int yAddon) {
        IntObject newIntObject;
        if (this.font.getSize() <= 15) {
            this.IMAGE_WIDTH = 256;
            this.IMAGE_HEIGHT = 256;
        }
        if (this.font.getSize() <= 43) {
            this.IMAGE_WIDTH = 512;
            this.IMAGE_HEIGHT = 512;
        } else if (this.font.getSize() <= 91) {
            this.IMAGE_WIDTH = 1024;
            this.IMAGE_HEIGHT = 1024;
        } else {
            this.IMAGE_WIDTH = 2048;
            this.IMAGE_HEIGHT = 2048;
        }
        if (allChar) {
            this.chars = new IntObject[65535];
            this.IMAGE_WIDTH = 8192;
            this.IMAGE_HEIGHT = 8192;
        }
        BufferedImage img = new BufferedImage(this.IMAGE_WIDTH, this.IMAGE_HEIGHT, 2);
        Graphics2D g = (Graphics2D)img.getGraphics();
        g.setFont(this.font);
        g.setColor(new Color(255, 255, 255, 0));
        g.fillRect(0, 0, this.IMAGE_WIDTH, this.IMAGE_HEIGHT);
        g.setColor(Color.white);
        int rowHeight = 0;
        int positionX = 0;
        int positionY = 0;
        for (int i = 0; i < this.chars.length; positionX += newIntObject.width, ++i) {
            char ch = (char)i;
            BufferedImage fontImage = this.getFontImage(ch, antiAlias, yAddon);
            newIntObject = new IntObject(null);
            newIntObject.width = fontImage.getWidth();
            newIntObject.height = fontImage.getHeight();
            if (positionX + newIntObject.width >= this.IMAGE_WIDTH) {
                positionX = 0;
                positionY += rowHeight;
                rowHeight = 0;
            }
            newIntObject.storedX = positionX;
            newIntObject.storedY = positionY;
            if (newIntObject.height > this.fontHeight) {
                this.fontHeight = newIntObject.height;
            }
            if (newIntObject.height > rowHeight) {
                rowHeight = newIntObject.height;
            }
            this.chars[i] = newIntObject;
            g.drawImage((Image)fontImage, positionX, positionY, null);
        }
        try {
            this.texID = TextureUtil.uploadTextureImageAllocate((int)TextureUtil.glGenTextures(), (BufferedImage)img, (boolean)false, (boolean)false);
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private BufferedImage getFontImage(char ch, boolean antiAlias, int yAddon) {
        int charheight;
        BufferedImage tempfontImage = new BufferedImage(1, 1, 2);
        Graphics2D g = (Graphics2D)tempfontImage.getGraphics();
        if (antiAlias) {
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        } else {
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        }
        g.setFont(this.font);
        FontMetrics fontMetrics = g.getFontMetrics();
        int charwidth = fontMetrics.charWidth(ch) + 7;
        if (charwidth <= 0) {
            charwidth = 7;
        }
        if ((charheight = fontMetrics.getHeight() + 1 + yAddon) <= 0) {
            charheight = this.font.getSize();
        }
        BufferedImage fontImage = new BufferedImage(charwidth, charheight, 2);
        Graphics2D gt = (Graphics2D)fontImage.getGraphics();
        if (antiAlias) {
            gt.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        } else {
            gt.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        }
        gt.setFont(this.font);
        gt.setColor(Color.WHITE);
        int charx = 3;
        boolean chary = true;
        gt.drawString(String.valueOf(ch), 3, 1 + fontMetrics.getAscent());
        return fontImage;
    }

    public void drawChar(char c, float x, float y) throws ArrayIndexOutOfBoundsException {
        try {
            this.drawQuad(x, y, this.chars[c].width, this.chars[c].height, this.chars[c].storedX, this.chars[c].storedY, this.chars[c].width, this.chars[c].height);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawQuad(float x, float y, float width, float height, float srcX, float srcY, float srcWidth, float srcHeight) {
        float renderSRCX = srcX / (float)this.IMAGE_WIDTH;
        float renderSRCY = srcY / (float)this.IMAGE_HEIGHT;
        float renderSRCWidth = srcWidth / (float)this.IMAGE_WIDTH;
        float renderSRCHeight = srcHeight / (float)this.IMAGE_HEIGHT;
        GL11.glBegin((int)4);
        GL11.glTexCoord2f((float)(renderSRCX + renderSRCWidth), (float)renderSRCY);
        GL11.glVertex2d((double)(x + width), (double)y);
        GL11.glTexCoord2f((float)renderSRCX, (float)renderSRCY);
        GL11.glVertex2d((double)x, (double)y);
        GL11.glTexCoord2f((float)renderSRCX, (float)(renderSRCY + renderSRCHeight));
        GL11.glVertex2d((double)x, (double)(y + height));
        GL11.glTexCoord2f((float)renderSRCX, (float)(renderSRCY + renderSRCHeight));
        GL11.glVertex2d((double)x, (double)(y + height));
        GL11.glTexCoord2f((float)(renderSRCX + renderSRCWidth), (float)(renderSRCY + renderSRCHeight));
        GL11.glVertex2d((double)(x + width), (double)(y + height));
        GL11.glTexCoord2f((float)(renderSRCX + renderSRCWidth), (float)renderSRCY);
        GL11.glVertex2d((double)(x + width), (double)y);
        GL11.glEnd();
    }

    public void drawString(String text, double x, double y, Color color, boolean shadow) {
        x -= 3.0;
        y -= 1.0;
        GL11.glPushMatrix();
        GlStateManager.bindTexture((int)this.texID);
        this.glColor(shadow ? new Color(0.05f, 0.05f, 0.05f, (float)color.getAlpha() / 255.0f) : color);
        int size = text.length();
        int indexInString = 0;
        while (true) {
            if (indexInString >= size) {
                GL11.glPopMatrix();
                return;
            }
            char character = text.charAt(indexInString);
            if (character < this.chars.length && character >= '\u0000') {
                this.drawChar(character, (float)x, (float)y);
                x += (double)(this.chars[character].width - this.charOffset);
            }
            ++indexInString;
        }
    }

    public void drawString(String text, double x, double y, int color, boolean shadow) {
        x -= 3.0;
        y -= 1.0;
        GL11.glPushMatrix();
        GlStateManager.bindTexture((int)this.texID);
        RenderUtil.color((int)color);
        int size = text.length();
        int indexInString = 0;
        while (true) {
            if (indexInString >= size) {
                GL11.glPopMatrix();
                return;
            }
            char character = text.charAt(indexInString);
            if (character < this.chars.length && character >= '\u0000') {
                this.drawChar(character, (float)x, (float)y);
                x += (double)(this.chars[character].width - this.charOffset);
            }
            ++indexInString;
        }
    }

    public void glColor(Color color) {
        float red = (float)color.getRed() / 255.0f;
        float green = (float)color.getGreen() / 255.0f;
        float blue = (float)color.getBlue() / 255.0f;
        float alpha = (float)color.getAlpha() / 255.0f;
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);
    }

    public int getHeight(String text) {
        int lines = 1;
        char[] cArray = text.toCharArray();
        int n = cArray.length;
        int n2 = 0;
        while (n2 < n) {
            char c = cArray[n2];
            if (c == '\n') {
                ++lines;
            }
            ++n2;
        }
        return this.fontHeight * lines;
    }

    public int getHeight() {
        return this.fontHeight;
    }

    public int getWidth(String text) {
        int width = 0;
        char[] cArray = text.toCharArray();
        int n = cArray.length;
        int n2 = 0;
        while (n2 < n) {
            char c = cArray[n2];
            if (c < this.chars.length && c >= '\u0000') {
                width += this.chars[c].width - this.charOffset;
            }
            ++n2;
        }
        return width;
    }

    public boolean isAntiAlias() {
        return this.antiAlias;
    }

    public void setAntiAlias(boolean antiAlias, boolean allChar, int yAddon) {
        if (this.antiAlias == antiAlias) return;
        this.antiAlias = antiAlias;
        this.setupTexture(antiAlias, allChar, yAddon);
    }

    public Font getFont() {
        return this.font;
    }
}
