package de.verschwiegener.atero.util;

import de.verschwiegener.atero.util.chat.ChatFontRenderer;
import de.verschwiegener.atero.util.render.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.nio.ByteBuffer;

import static net.minecraft.client.gui.GuiScreen.*;
import static org.lwjgl.opengl.GL11.*;
import static net.minecraft.client.renderer.GlStateManager.*;

/**
 * @author kroko
 * @created on 20.02.2021 : 19:42
 */
public class ColorPicker {

    final Type type;
    int x, y, width, height, color = new Color(0, 161, 249).getRGB();

    public ColorPicker(Type type) {
	this.type = type;
    }

    public Color getHoverColor() {
	final ByteBuffer rgb = BufferUtils.createByteBuffer(100);
	GL11.glReadPixels(Mouse.getX(), Mouse.getY(), 1, 1, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, rgb);
	final Color readColor = new Color(rgb.get(0) & 0xFF, rgb.get(1) & 0xFF, rgb.get(2) & 0xFF);
	return readColor;
    }

    public void draw(int x, int y, int width, int height, int mouseX, int mouseY, Color currentColor) {
	final FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
	this.x = x;
	this.y = y;
	this.width = width;
	this.height = height;
	final float f = (float) (color >> 16 & 255) / 255.0F;
	final float f1 = (float) (color >> 8 & 255) / 255.0F;
	final float f2 = (float) (color & 255) / 255.0F;

	final double h = 1;
	for (int i = 0; i < height; i++) {
	    drawRect(x + width + 10, (int) (y + (h * i)), x + width + 20, (int) (y + (h * (i + 1))),
		    Color.HSBtoRGB((float) i / height, 1, 1));

	    if (Mouse.isButtonDown(0) && mouseX >= x + width + 10 && mouseX <= x + width + 20 && mouseY >= y + (h * i)
		    && mouseY <= y + (h * (i + 1))) {
		color = Color.HSBtoRGB((float) i / height, 1, 1);
	    }
	}

	for (int i = 0; i < height; i++) {
	    if (color == Color.HSBtoRGB((float) i / height, 1, 1)) {
		drawRect(x + width + 10, (int) (y + (h * i) + 1), x + width + 20, (int) (y + (h * (i + 1)) + 2),
			Color.black.getRGB());
		drawRect(x + width + 10, (int) (y + (h * i) - 2), x + width + 20, (int) (y + (h * (i + 1)) - 1),
			Color.black.getRGB());
	    }
	}
	drawRect(x, y + height + 5, x + width / 2, y + height + 25, currentColor.getRGB());
	GL11.glColor3d(1, 1, 1);
	ChatFontRenderer.drawString("#" + Integer.toHexString(currentColor.getRGB()).substring(2),  x + width / 2, y + height + 25 / 2 - fr.FONT_HEIGHT / 2, new Color(-1));
	//fr.drawString("#" + Integer.toHexString(currentColor.getRGB()).substring(2), x + width / 2 + 5,
		//y + height + 30 / 2 - fr.FONT_HEIGHT / 2, -1);

	switch (type) {
	case QUAD:
	    glEnable(GL_BLEND);
	    glShadeModel(GL_SMOOTH);
	    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	    glDisable(GL_TEXTURE_2D);
	    GL11.glBegin(GL_QUADS);
	    glColor3d(f, f1, f2);
	    glVertex2d(x + width, y);
	    glColor3d(1, 1, 1);
	    glVertex2d(x, y);
	    glColor3d(0, 0, 0);
	    glVertex2d(x, y + height);
	    glColor3d(0, 0, 0);
	    glVertex2d(x + width, y + height);
	    GL11.glEnd();

	    glEnable(GL_TEXTURE_2D);
	    glDisable(GL_BLEND);
	    break;
	}
    }

    public boolean isHover(int mouseX, int mouseY) {
	return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public enum Type {
	QUAD;
    }
}