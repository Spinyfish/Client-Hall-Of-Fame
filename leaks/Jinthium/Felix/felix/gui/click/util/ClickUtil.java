package felix.gui.click.util;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

/**
 * @author sendQueue <Vinii>
 *
 *         Further info at Vinii.de or github@vinii.de, file created at 11.11.2020. 
 *         Use is only authorized if given credit!
 * 
 */
public final class ClickUtil {

	/**
	 * <h1>CAUTION:</h1> Your version might not support the default method.
	 * <p>
	 * If so, feel free to use the use the replacement method from <A href=
	 * "https://github.com/sendQueue/LWJGLUtil">https://github.com/sendQueue/LWJGLUtil</A>.
	 * 
	 * 
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 * @param color
	 */
	public static void drawRect(int left, int top, int right, int bottom, int color) {
		// default
		Gui.drawRect(left, top, right, bottom, color);

		// replacement
//		LWJGLUtil.drawRect(left, top, right - left, bottom - top, color);
	}

	public static void drawHorizontalLine(int startX, int endX, int y, int color) {
		if (endX < startX) {
			int i = startX;
			startX = endX;
			endX = i;
		}

		drawRect(startX, y, endX + 1, y + 1, color);
	}
	public static void drawVerticalLine(int x, int startY, int endY, int color) {
		if (endY < startY) {
			int i = startY;
			startY = endY;
			endY = i;
		}

		drawRect(x, startY + 1, x + 1, endY, color);
	}
	
	/**
	 * Checks if mouse is over rectangle.
	 * 
	 * @param x
	 * @param y
	 * @param width RELATIVE to x.
	 * @param height RELATIVE to y.
	 * @param mouseX
	 * @param mouseY
	 * @return true if rectangle is hovered
	 */
	public static boolean isHovered(int x, int y, int width, int height, int mouseX, int mouseY) {
		return (mouseX >= x) && (mouseX <= x + width) && (mouseY >= y) && (mouseY < y + height);
	}

	public static void drawFilledCircle(float x, float y, float r, int c) {
        float f = (c >> 24 & 0xFF) / 255.0f;
        float f2 = (c >> 16 & 0xFF) / 255.0f;
        float f3 = (c >> 8 & 0xFF) / 255.0f;
        float f4 = (c & 0xFF) / 255.0f;
        GL11.glPushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GL11.glEnable(2848);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glColor4f(f2, f3, f4, f);
        GL11.glBegin(6);
        for (int i = 0; i <= 360; ++i) {
            double x2 = Math.sin(i * Math.PI / 180.0) * (r / 2);
            double y2 = Math.cos(i * Math.PI / 180.0) * (r / 2);
            GL11.glVertex2d(x + r / 2 + x2, y + r / 2 + y2);
        }
        GL11.glEnd();
        GL11.glBegin(GL11.GL_LINE_LOOP);
        for (int i = 0; i <= 360; ++i) {
            double x2 = Math.sin(i * Math.PI / 180.0) * ((r / 2));
            double y2 = Math.cos(i * Math.PI / 180.0) * ((r / 2));
            GL11.glVertex2d(x + ((r / 2)) + x2, y + ((r / 2)) + y2);
        }
        GL11.glEnd();
        GL11.glDisable(2848);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GL11.glPopMatrix();
    }

	public static void drawUnfilledCircle(float x, float y, float r, int c) {
        float f = (c >> 24 & 0xFF) / 255.0f;
        float f2 = (c >> 16 & 0xFF) / 255.0f;
        float f3 = (c >> 8 & 0xFF) / 255.0f;
        float f4 = (c & 0xFF) / 255.0f;
        GL11.glPushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glColor4f(f2, f3, f4, f);
        GL11.glLineWidth(1);
        GL11.glBegin(2);
        for (int i = 0; i <= 360; ++i) {
            double x2 = Math.sin(i * Math.PI / 180.0) * (r / 2);
            double y2 = Math.cos(i * Math.PI / 180.0) * (r / 2);
            GL11.glVertex2d(x + r / 2 + x2, y + r / 2 + y2);
        }
        GL11.glEnd();
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GL11.glPopMatrix();
    }
}
