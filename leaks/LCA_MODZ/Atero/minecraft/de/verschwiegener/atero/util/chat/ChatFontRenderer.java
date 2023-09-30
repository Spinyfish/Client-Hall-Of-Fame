package de.verschwiegener.atero.util.chat;

import java.awt.Color;
import java.awt.Font;
import java.util.Arrays;

import org.lwjgl.opengl.GL11;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.util.GlyphMetrics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.StringUtils;

public class ChatFontRenderer {

    public static ChatFont font = Management.instance.chatfont;
    public static Minecraft mc = Minecraft.getMinecraft();
    
    
    public static int drawString(String text, int posX, int posY, Color color) {
	if(text == null || text == "")
	    return 0;
	
	GlStateManager.pushMatrix();
	boolean blend = GL11.glGetBoolean(GL11.GL_BLEND);
	GlStateManager.enableBlend();
	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	GlStateManager.enableTexture2D();
	GlStateManager.disableLighting();
	GL11.glColor4d((color.getRed() / 255.0F), (color.getGreen() / 255.0F), (color.getBlue() / 255.0F),
		(color.getAlpha() / 255.0F));
	GL11.glScaled(0.5D, 0.5D, 0.5D);
	float x = (posX * 2);
	float y = (posY * 2);
	
	char[] characters = text.toCharArray();
	
	for(char glyph : characters) {
	    if(font.glyphMap.length > glyph) {
		GlyphMetrics metric = font.glyphMap[glyph];
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, metric.getGLTextureID());
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, 10240, 9729);
		GL11.glTexParameteri(3553, 10240, 9729);
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		// Draw texture, see ShaderRenderer
		double height = metric.getHeight();
		double width = metric.getWidth();
		GL11.glBegin(7);
		GL11.glTexCoord2d(0.0D, 0.0D);
		GL11.glVertex2d(x, y);
		GL11.glTexCoord2d(0.0D, 1.0D);
		GL11.glVertex2d(x, y + height);
		GL11.glTexCoord2d(1.0D, 1.0D);
		GL11.glVertex2d(x + width, y + height);
		GL11.glTexCoord2d(1.0D, 0.0D);
		GL11.glVertex2d(x + width, y);
		GL11.glEnd();
		x += width - 8;
	    } else {
		GL11.glScaled(2.0D, 2.0D, 2.0D);
		mc.fontRendererObj.drawString(Character.toString(glyph), (int) (x / 2 + 2), posY + 4, color.getRGB());
		GL11.glScaled(0.5D, 0.5D, 0.5D);
		x += (mc.fontRendererObj.getStringWidth(Character.toString(glyph)) * 2);
	    }
	}
	
	if (!blend) {
	    GlStateManager.disableBlend();
	}
	GlStateManager.bindTexture(0);
	GlStateManager.popMatrix();
	GL11.glColor4d(1.0D, 1.0D, 1.0D, 1.0D);
	return (int) (x - posX);
    }
    public static int getStringWidthClean(String text) {
	int width = 0;
	text = StringUtils.stripControlCodes(text);
	char[] characters = text.toCharArray();
	for (char glyph : characters) {
	    if(font.glyphMap.length > glyph) {
		width += font.glyphMap[glyph].getWidth() - 8;
	    }
	}
	return width;
    }

    public static int getStringWidth(String text) {
	int width = 0;
	char[] characters = text.toCharArray();
	for (char glyph : characters) {
	    if (font.glyphMap.length > glyph) {
		width += font.glyphMap[glyph].getWidth() - 8;
	    }
	}
	return width;
    }

    public static int getStringHeight(String text) {
	double height = 0;
	for (int i = 0; i < text.length(); i++) {
	    char glyph = text.charAt(i);
	    height += font.heightMap[glyph];
	}
	return (int) (height / text.length());
    }

}
