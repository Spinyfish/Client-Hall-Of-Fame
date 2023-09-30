package igbt.astolfy.utils;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_FLAT;
import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glShadeModel;
import static org.lwjgl.opengl.GL11.glVertex2d;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;

public class RenderUtils {
	public static void prepareScissorBox(float x, float y, float x2, float y2) {
        ScaledResolution scale = new ScaledResolution(Minecraft.getMinecraft());
        int factor = scale.getScaleFactor();
        GL11.glScissor((int)(x * (float)factor), (int)(((float)scale.getScaledHeight() - y2) * (float)factor), (int)((x2 - x) * (float)factor), (int)((y2 - y) * (float)factor));
     }

     public static void drawDrop(double x, double y, double x2, double height) {
         if (y < y + height) {
             Gui.drawGradientRect(x, y, x2, y + height, 0x80000000, 0x00000000);
         } else
             Gui.drawGradientRect(x, y + height, x2, y, 0x00000000 , 0x80000000);
     }

	public static boolean isHovered(int mouseX, int mouseY,double x, double y, double x2, double y2) {
		if(x <= mouseX && x2 >= mouseX
				&& y <= mouseY && y2 >= mouseY)
			return true;
		
		return false;
	}
	
	
	public static void drawOutlineRect(double x, double y, double x2, double y2, double scale, int color) {
		Gui.drawRect(x, y, x + scale, y2, color);
		Gui.drawRect(x, y, x2, y + scale, color);
		Gui.drawRect(x2, y, x2 + scale, y2, color);
		Gui.drawRect(x, y2, x2 + scale, y2 + scale, color);
	}
	public static void color(int color) {
        float[] rgba = convertRGB(color);
        GL11.glColor4f(rgba[0], rgba[1], rgba[2], rgba[3]);
    }
    public static void color(Color color) {
        GL11.glColor4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
    }    
    public static float[] convertRGB(int rgb) {
        float a = (rgb >> 24 & 0xFF) / 255.0f;
        float r = (rgb >> 16 & 0xFF) / 255.0f;
        float g = (rgb >> 8 & 0xFF) / 255.0f;
        float b = (rgb & 0xFF) / 255.0f;
        return new float[]{r, g, b, a};
    }
	private static void gradientSideways(double x, double y, double width, double height, boolean filled, Color color1, Color color2) {
        start();
        glShadeModel(GL_SMOOTH);
        GlStateManager.disableAlpha();
        if (color1 != null)
            color(color1);
        glBegin(filled ? GL_TRIANGLE_FAN : GL_LINES);
        {
            glVertex2d(x, y);
            glVertex2d(x, y + height);
            if (color2 != null)
                color(color2);
            glVertex2d(x + width, y + height);
            glVertex2d(x + width, y);
        }
        glEnd();
        GlStateManager.enableAlpha();
        glShadeModel(GL_FLAT);
        stop();
    }
	public static void start() {
        GL11.glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL_TEXTURE_2D);
        GL11.glDisable(GL_CULL_FACE);
        GlStateManager.disableAlpha();
    }

    public static void stop() {
        GlStateManager.enableAlpha();
        GL11.glEnable(GL_CULL_FACE);
        GL11.glEnable(GL_TEXTURE_2D);
        GL11.glDisable(GL_BLEND);
        color(Color.white);
    }


    public static void gradientSideways(double x, double y, double width, double height, Color color1, Color color2) {
        gradientSideways(x, y, width, height, true, color1, color2);
    }

    public static float interpolate(double current, double old, double scale) {
        return (float) (old + (current - old) * scale);
    }
}
