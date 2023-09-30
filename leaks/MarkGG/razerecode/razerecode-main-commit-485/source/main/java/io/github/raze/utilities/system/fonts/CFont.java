/*
 * Copyright (c) 2023. Syncinus / Liticane
 * You cannot redistribute these files
 * without my written permission.
 */

package io.github.raze.utilities.system.fonts;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class CFont {

    protected static class CharacterData {
        public int width, height;
        public int storedX, storedY;
    }

    private final float imgSize = 1048;

    protected int fontHeight = 0;
    protected int charOffset = 0;

    protected boolean antiAliasing;
    protected boolean fractionalMetrics;

    protected Font font;

    protected CharacterData[] characterData = new CharacterData[256];
    protected DynamicTexture texture;

    public CFont(ResourceLocation resourceLocation, float size) {
        Font temporary;

        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation).getInputStream();
            temporary = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(size);
        } catch (IOException | FontFormatException e) {
            temporary = new Font("Arial", Font.PLAIN, (int) size);
            e.printStackTrace();
        }

        font = temporary;
        antiAliasing = true;
        fractionalMetrics = true;
        texture = setupTexture(font, true, true, characterData);
    }

    protected DynamicTexture setupTexture(Font font, boolean antiAlias, boolean fractionalMetrics, CharacterData[] chars) {
        BufferedImage img = generateFontImage(font, antiAlias, fractionalMetrics, chars);

        try {
            return new DynamicTexture(img);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private BufferedImage generateFontImage(Font font, boolean antiAlias, boolean fractionalMetrics, CharacterData[] characters) {
        int size = (int) imgSize;
        BufferedImage bufferedImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics2D = (Graphics2D) bufferedImage.getGraphics();

        graphics2D.setFont(font);

        graphics2D.setColor(new Color(255, 255, 255, 0));
        graphics2D.fillRect(0, 0, size, size);
        graphics2D.setColor(Color.WHITE);

        graphics2D.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, fractionalMetrics ? RenderingHints.VALUE_FRACTIONALMETRICS_ON : RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, antiAlias ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAlias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);

        FontMetrics fontMetrics = graphics2D.getFontMetrics();

        int charHeight = 0;
        int positionX = 0;
        int positionY = 1;

        for (int index = 0; index < characters.length; index += 1) {
            char character = (char) index;

            CharacterData characterData = new CharacterData();
            Rectangle2D dimensions = fontMetrics.getStringBounds(String.valueOf(character), graphics2D);

            characterData.width = (dimensions.getBounds().width + 8);
            characterData.height = dimensions.getBounds().height;

            if (positionX + characterData.width >= size) {
                positionX = 0;
                positionY += charHeight;
                charHeight = 0;
            }

            if (characterData.height > charHeight) {
                charHeight = characterData.height;
            }

            characterData.storedX = positionX;
            characterData.storedY = positionY;

            if (characterData.height > fontHeight) {
                fontHeight = characterData.height;
            }

            characters[index] = characterData;

            graphics2D.drawString(String.valueOf(character), positionX + 2, positionY + fontMetrics.getAscent());

            positionX += characterData.width;
        }

        return bufferedImage;
    }

    protected void drawChar(CharacterData[] chars, char c, float x, float y) throws ArrayIndexOutOfBoundsException {
        try {
            drawQuad(x, y, chars[c].width, chars[c].height, chars[c].storedX, chars[c].storedY, chars[c].width, chars[c].height);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void drawQuad(float x, float y, float width, float height, float srcX, float srcY, float srcWidth, float srcHeight) {

        float renderSRCX = srcX / imgSize;
        float renderSRCY = srcY / imgSize;
        float renderSRCWidth = srcWidth / imgSize;
        float renderSRCHeight = srcHeight / imgSize;

        GL11.glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY);
        GL11.glVertex2d(x + width, y);

        GL11.glTexCoord2f(renderSRCX, renderSRCY);
        GL11.glVertex2d(x, y);

        GL11.glTexCoord2f(renderSRCX, renderSRCY + renderSRCHeight);
        GL11.glVertex2d(x, y + height);

        GL11.glTexCoord2f(renderSRCX, renderSRCY + renderSRCHeight);
        GL11.glVertex2d(x, y + height);

        GL11.glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY + renderSRCHeight);
        GL11.glVertex2d(x + width, y + height);

        GL11.glTexCoord2f(renderSRCX + renderSRCWidth, renderSRCY);
        GL11.glVertex2d(x + width, y);
    }

    public int getHeight() {
        return (this.fontHeight - 8) / 2;
    }

    public int getStringWidth(String text) {
        int width = 0;

        for (char c : text.toCharArray()) {
            if (c < characterData.length) {
                width += characterData[c].width - 8 + charOffset;
            }
        }

        return width / 2;
    }

    public boolean isAntiAliasing() {
        return this.antiAliasing;
    }

    public void setAntiAliasing(boolean antiAliasing) {
        if (this.antiAliasing != antiAliasing) {
            this.antiAliasing = antiAliasing;
            texture = setupTexture(font, antiAliasing, fractionalMetrics, characterData);
        }
    }

    public boolean isFractionalMetrics() {
        return fractionalMetrics;
    }

    public void setFractionalMetrics(boolean fractionalMetrics) {
        if (this.fractionalMetrics != fractionalMetrics) {
            this.fractionalMetrics = fractionalMetrics;
            texture = setupTexture(font, antiAliasing, fractionalMetrics, characterData);
        }
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
        texture = setupTexture(font, antiAliasing, fractionalMetrics, characterData);
    }

}
