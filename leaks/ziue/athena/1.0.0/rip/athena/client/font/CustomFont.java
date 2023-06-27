package rip.athena.client.font;

import java.awt.image.*;
import net.minecraft.client.renderer.texture.*;
import java.awt.*;
import org.lwjgl.opengl.*;
import net.minecraft.client.renderer.*;
import rip.athena.client.utils.render.*;

public class CustomFont
{
    public int IMAGE_WIDTH;
    public int IMAGE_HEIGHT;
    private int texID;
    private IntObject[] chars;
    private final Font font;
    private boolean antiAlias;
    private int fontHeight;
    private int charOffset;
    
    public CustomFont(final Font font, final boolean antiAlias, final int charOffset, final boolean allChar, final int yAddon) {
        this.IMAGE_WIDTH = 1024;
        this.IMAGE_HEIGHT = 1024;
        this.chars = new IntObject[2048];
        this.fontHeight = -1;
        this.charOffset = 8;
        this.font = font;
        this.antiAlias = antiAlias;
        this.charOffset = charOffset;
        if (charOffset == 0) {
            this.charOffset = 8;
        }
        this.setupTexture(antiAlias, allChar, yAddon);
    }
    
    public CustomFont(final Font font, final boolean antiAlias, final boolean allChar, final int yAddon) {
        this.IMAGE_WIDTH = 1024;
        this.IMAGE_HEIGHT = 1024;
        this.chars = new IntObject[2048];
        this.fontHeight = -1;
        this.charOffset = 8;
        this.font = font;
        this.antiAlias = antiAlias;
        this.charOffset = 6;
        this.setupTexture(antiAlias, allChar, yAddon);
    }
    
    private void setupTexture(final boolean antiAlias, final boolean allChar, final int yAddon) {
        if (this.font.getSize() <= 15) {
            this.IMAGE_WIDTH = 256;
            this.IMAGE_HEIGHT = 256;
        }
        if (this.font.getSize() <= 43) {
            this.IMAGE_WIDTH = 512;
            this.IMAGE_HEIGHT = 512;
        }
        else if (this.font.getSize() <= 91) {
            this.IMAGE_WIDTH = 1024;
            this.IMAGE_HEIGHT = 1024;
        }
        else {
            this.IMAGE_WIDTH = 2048;
            this.IMAGE_HEIGHT = 2048;
        }
        if (allChar) {
            this.chars = new IntObject[65535];
            this.IMAGE_WIDTH = 8192;
            this.IMAGE_HEIGHT = 8192;
        }
        final BufferedImage img = new BufferedImage(this.IMAGE_WIDTH, this.IMAGE_HEIGHT, 2);
        final Graphics2D g = (Graphics2D)img.getGraphics();
        g.setFont(this.font);
        g.setColor(new Color(255, 255, 255, 0));
        g.fillRect(0, 0, this.IMAGE_WIDTH, this.IMAGE_HEIGHT);
        g.setColor(Color.white);
        int rowHeight = 0;
        int positionX = 0;
        int positionY = 0;
        for (int i = 0; i < this.chars.length; ++i) {
            final char ch = (char)i;
            final BufferedImage fontImage = this.getFontImage(ch, antiAlias, yAddon);
            final IntObject newIntObject = new IntObject();
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
            g.drawImage(fontImage, positionX, positionY, null);
            positionX += newIntObject.width;
        }
        try {
            this.texID = TextureUtil.uploadTextureImageAllocate(TextureUtil.glGenTextures(), img, false, false);
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
    
    private BufferedImage getFontImage(final char ch, final boolean antiAlias, final int yAddon) {
        final BufferedImage tempfontImage = new BufferedImage(1, 1, 2);
        final Graphics2D g = (Graphics2D)tempfontImage.getGraphics();
        if (antiAlias) {
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }
        else {
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        }
        g.setFont(this.font);
        final FontMetrics fontMetrics = g.getFontMetrics();
        int charwidth = fontMetrics.charWidth(ch) + 7;
        if (charwidth <= 0) {
            charwidth = 7;
        }
        int charheight = fontMetrics.getHeight() + 1 + yAddon;
        if (charheight <= 0) {
            charheight = this.font.getSize();
        }
        final BufferedImage fontImage = new BufferedImage(charwidth, charheight, 2);
        final Graphics2D gt = (Graphics2D)fontImage.getGraphics();
        if (antiAlias) {
            gt.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }
        else {
            gt.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        }
        gt.setFont(this.font);
        gt.setColor(Color.WHITE);
        final int charx = 3;
        final int chary = 1;
        gt.drawString(String.valueOf(ch), 3, 1 + fontMetrics.getAscent());
        return fontImage;
    }
    
    public void drawChar(final char c, final float x, final float y) throws ArrayIndexOutOfBoundsException {
        try {
            this.drawQuad(x, y, (float)this.chars[c].width, (float)this.chars[c].height, (float)this.chars[c].storedX, (float)this.chars[c].storedY, (float)this.chars[c].width, (float)this.chars[c].height);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void drawQuad(final float x, final float y, final float width, final float height, final float srcX, final float srcY, final float srcWidth, final float srcHeight) {
        final float renderSRCX = srcX / this.IMAGE_WIDTH;
        final float renderSRCY = srcY / this.IMAGE_HEIGHT;
        final float renderSRCWidth = srcWidth / this.IMAGE_WIDTH;
        final float renderSRCHeight = srcHeight / this.IMAGE_HEIGHT;
        GL11.glBegin(4);
        GL11.glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY);
        GL11.glVertex2d((double)(x + width), (double)y);
        GL11.glTexCoord2f(renderSRCX, renderSRCY);
        GL11.glVertex2d((double)x, (double)y);
        GL11.glTexCoord2f(renderSRCX, renderSRCY + renderSRCHeight);
        GL11.glVertex2d((double)x, (double)(y + height));
        GL11.glTexCoord2f(renderSRCX, renderSRCY + renderSRCHeight);
        GL11.glVertex2d((double)x, (double)(y + height));
        GL11.glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY + renderSRCHeight);
        GL11.glVertex2d((double)(x + width), (double)(y + height));
        GL11.glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY);
        GL11.glVertex2d((double)(x + width), (double)y);
        GL11.glEnd();
    }
    
    public void drawString(final String text, double x, double y, final Color color, final boolean shadow) {
        x -= 3.0;
        --y;
        GL11.glPushMatrix();
        GlStateManager.bindTexture(this.texID);
        this.glColor(shadow ? new Color(0.05f, 0.05f, 0.05f, color.getAlpha() / 255.0f) : color);
        for (int size = text.length(), indexInString = 0; indexInString < size; ++indexInString) {
            final char character = text.charAt(indexInString);
            if (character < this.chars.length && character >= '\0') {
                this.drawChar(character, (float)x, (float)y);
                x += this.chars[character].width - this.charOffset;
            }
        }
        GL11.glPopMatrix();
    }
    
    public void drawString(final String text, double x, double y, final int color, final boolean shadow) {
        x -= 3.0;
        --y;
        GL11.glPushMatrix();
        GlStateManager.bindTexture(this.texID);
        DrawUtils.color(color);
        for (int size = text.length(), indexInString = 0; indexInString < size; ++indexInString) {
            final char character = text.charAt(indexInString);
            if (character < this.chars.length && character >= '\0') {
                this.drawChar(character, (float)x, (float)y);
                x += this.chars[character].width - this.charOffset;
            }
        }
        GL11.glPopMatrix();
    }
    
    public void glColor(final Color color) {
        final float red = color.getRed() / 255.0f;
        final float green = color.getGreen() / 255.0f;
        final float blue = color.getBlue() / 255.0f;
        final float alpha = color.getAlpha() / 255.0f;
        GL11.glColor4f(red, green, blue, alpha);
    }
    
    public int getHeight(final String text) {
        int lines = 1;
        for (final char c : text.toCharArray()) {
            if (c == '\n') {
                ++lines;
            }
        }
        return this.fontHeight * lines;
    }
    
    public int getHeight() {
        return this.fontHeight;
    }
    
    public int getWidth(final String text) {
        int width = 0;
        for (final char c : text.toCharArray()) {
            if (c < this.chars.length && c >= '\0') {
                width += this.chars[c].width - this.charOffset;
            }
        }
        return width;
    }
    
    public boolean isAntiAlias() {
        return this.antiAlias;
    }
    
    public void setAntiAlias(final boolean antiAlias, final boolean allChar, final int yAddon) {
        if (this.antiAlias != antiAlias) {
            this.setupTexture(this.antiAlias = antiAlias, allChar, yAddon);
        }
    }
    
    public Font getFont() {
        return this.font;
    }
    
    private static class IntObject
    {
        public int width;
        public int height;
        public int storedX;
        public int storedY;
    }
}
