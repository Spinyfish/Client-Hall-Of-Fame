package com.thunderware.utils;

import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import javax.vecmath.Vector3d;
import java.awt.*;

public class RenderUtils {
	public static void prepareScissorBox(float x, float y, float x2, float y2) {
        ScaledResolution scale = new ScaledResolution(Minecraft.getMinecraft());
        int factor = scale.getScaleFactor();
        GL11.glScissor((int)(x * (float)factor), (int)(((float)scale.getScaledHeight() - y2) * (float)factor), (int)((x2 - x) * (float)factor), (int)((y2 - y) * (float)factor));
     }

    public static void drawBorderedRect(double left, double top, double right, double bottom, double borderWidth, int insideColor, int borderColor, boolean borderIncludedInBounds) {
        Gui.drawRect(left - (!borderIncludedInBounds ? borderWidth : 0.0D), top - (!borderIncludedInBounds ? borderWidth : 0.0D), right + (!borderIncludedInBounds ? borderWidth : 0.0D), bottom + (!borderIncludedInBounds ? borderWidth : 0.0D), borderColor);
        Gui.drawRect(left + (borderIncludedInBounds ? borderWidth : 0.0D), top + (borderIncludedInBounds ? borderWidth : 0.0D), right - (borderIncludedInBounds ? borderWidth : 0.0D), bottom - (borderIncludedInBounds ? borderWidth : 0.0D), insideColor);
    }

    public static void drawOutline(double x, double y, double width, double height, double lineWidth, int color) {
        Gui.drawRect(x, y, x + width, y + lineWidth, color);
        Gui.drawRect(x, y, x + lineWidth, y + height, color);
        Gui.drawRect(x, y + height - lineWidth, x + width, y + height, color);
        Gui.drawRect(x + width - lineWidth, y, x + width, y + height, color);
    }

    public static void renderRoundedQuad(Vector3d from, Vector3d to, int rad, int color) {
    	float f3 = (float)(color >> 24 & 255) / 255.0F;
        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glColor4f(f,f1,f2,f3);
        GL11.glBegin(GL11.GL_POLYGON);
        {
            int initial = -90;
            double[][] map = new double[][]{
                    new double[]{to.x,to.y},
                    new double[]{to.x,from.y},
                    new double[]{from.x,from.y},
                    new double[]{from.x,to.y}
            };
            for(int i = 0;i<4;i++) {
                double[] current = map[i];
                initial += 90;
                for(int r = initial;r<(360/4+initial);r++) {
                    double rad1 = Math.toRadians(r);
                    double sin = Math.sin(rad1)*rad;
                    double cos = Math.cos(rad1)*rad;
                    GL11.glVertex2d(current[0]+sin,current[1]+cos);
                }
            }
        }
        GL11.glEnd();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glPopMatrix();
    }
}
