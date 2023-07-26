package com.craftworks.pearclient.util;

import com.craftworks.pearclient.util.math.MathUtil;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.optifine.util.MathUtils;

public class GLRectUtils
{

public static void drawRoundedRect(int x, int y, int width, int height, int cornerRadius, Color color) {
Gui.drawRect(x, y + cornerRadius, x + cornerRadius, y + height - cornerRadius, color.getRGB());
Gui.drawRect(x + cornerRadius, y, x + width - cornerRadius, y + height, color.getRGB());
Gui.drawRect(x + width - cornerRadius, y + cornerRadius, x + width, y + height - cornerRadius, color.getRGB());

drawArc(x + cornerRadius, y + cornerRadius, cornerRadius, 0, 90, color);
drawArc(x + width - cornerRadius, y + cornerRadius, cornerRadius, 270, 360, color);
drawArc(x + width - cornerRadius, y + height - cornerRadius, cornerRadius, 180, 270, color);
drawArc(x + cornerRadius, y + height - cornerRadius, cornerRadius, 90, 180, color);
}

public static void drawRoundedRect(float x, float y, float width, float height, int cornerRadius, Color color) {
drawRect(x, y + cornerRadius, x + cornerRadius, y + height - cornerRadius, color.getRGB());
drawRect(x + cornerRadius, y, x + width - cornerRadius, y + height, color.getRGB());
drawRect(x + width - cornerRadius, y + cornerRadius, x + width, y + height - cornerRadius, color.getRGB());

drawArc(x + cornerRadius, y + cornerRadius, cornerRadius, 0, 90, color);
drawArc(x + width - cornerRadius, y + cornerRadius, cornerRadius, 270, 360, color);
drawArc(x + width - cornerRadius, y + height - cornerRadius, cornerRadius, 180, 270, color);
drawArc(x + cornerRadius, y + height - cornerRadius, cornerRadius, 90, 180, color);
}

public static void drawRect(float left, float top, float right, float bottom, int color)
{
    if (left < right)
    {
    	float i = left;
        left = right;
        right = i;
    }

    if (top < bottom)
    {
    	float j = top;
        top = bottom;
        bottom = j;
    }

    float f3 = (float)(color >> 24 & 255) / 255.0F;
    float f = (float)(color >> 16 & 255) / 255.0F;
    float f1 = (float)(color >> 8 & 255) / 255.0F;
    float f2 = (float)(color & 255) / 255.0F;
    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();
    GlStateManager.enableBlend();
    GlStateManager.disableTexture2D();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    GlStateManager.color(f, f1, f2, f3);
    worldrenderer.begin(7, DefaultVertexFormats.POSITION);
    worldrenderer.pos((double)left, (double)bottom, 0.0D).endVertex();
    worldrenderer.pos((double)right, (double)bottom, 0.0D).endVertex();
    worldrenderer.pos((double)right, (double)top, 0.0D).endVertex();
    worldrenderer.pos((double)left, (double)top, 0.0D).endVertex();
    tessellator.draw();
    GlStateManager.enableTexture2D();
    GlStateManager.disableBlend();
}

public static void drawArc(float x, float y, float radius, float startAngle, float endAngle, Color color) {

GL11.glPushMatrix();
GL11.glEnable(3042);
GL11.glDisable(3553);
GL11.glBlendFunc(770, 771);
GL11.glColor4f((float) color.getRed() / 255, (float) color.getGreen() / 255, (float) color.getBlue() / 255, (float) color.getAlpha() / 255);

WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();

worldRenderer.begin(6, DefaultVertexFormats.POSITION);
worldRenderer.pos(x, y, 0).endVertex();

for (int i = (int) (startAngle / 360.0 * 100); i <= (int) (endAngle / 360.0 * 100); i++) {
double angle = (Math.PI * 2 * i / 100) + Math.toRadians(180);
worldRenderer.pos(x + Math.sin(angle) * radius, y + Math.cos(angle) * radius, 0).endVertex();
	}
}

public static void drawArc(int x, int y, int radius, int startAngle, int endAngle, Color color) {

GL11.glPushMatrix();
GL11.glEnable(3042);
GL11.glDisable(3553);
GL11.glBlendFunc(770, 771);
GL11.glColor4f((float) color.getRed() / 255, (float) color.getGreen() / 255, (float) color.getBlue() / 255, (float) color.getAlpha() / 255);

WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();

worldRenderer.begin(6, DefaultVertexFormats.POSITION);
worldRenderer.pos(x, y, 0).endVertex();

for (int i = (int) (startAngle / 360.0 * 100); i <= (int) (endAngle / 360.0 * 100); i++) {
double angle = (Math.PI * 2 * i / 100) + Math.toRadians(180);
worldRenderer.pos(x + Math.sin(angle) * radius, y + Math.cos(angle) * radius, 0).endVertex();
}

Tessellator.getInstance().draw();

GL11.glEnable(3553);
GL11.glDisable(3042);
GL11.glPopMatrix();
}

public static void drawRoundedOutline(int x, int y, int x2, int y2, float radius, float width, int color) {
float f1 = (color >> 24 & 0xFF) / 255.0F;
float f2 = (color >> 16 & 0xFF) / 255.0F;
float f3 = (color >> 8 & 0xFF) / 255.0F;
float f4 = (color & 0xFF) / 255.0F;
GL11.glColor4f(f2, f3, f4, f1);
drawRoundedOutline(x, y, x2, y2, radius, width);
}

public static void drawRoundedOutline(float x, float y, float x2, float y2, float radius, float width, int color) {
float f1 = (color >> 24 & 0xFF) / 255.0F;
float f2 = (color >> 16 & 0xFF) / 255.0F;
float f3 = (color >> 8 & 0xFF) / 255.0F;
float f4 = (color & 0xFF) / 255.0F;
GL11.glColor4f(f2, f3, f4, f1);
drawRoundedOutline(x, y, x2, y2, radius, width);
}

public static void drawRoundedOutline(float x, float y, float x2, float y2, float radius, float width) {
int i = 18;
int j = 90 / i;
GlStateManager.disableTexture2D();
GlStateManager.enableBlend();
GlStateManager.disableCull();
GlStateManager.enableColorMaterial();
GlStateManager.blendFunc(770, 771);
GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
if (width != 1.0F)
GL11.glLineWidth(width);
GL11.glBegin(3);
GL11.glVertex2f(x + radius, y);
GL11.glVertex2f(x2 - radius, y);
GL11.glEnd();
GL11.glBegin(3);
GL11.glVertex2f(x2, y + radius);
GL11.glVertex2f(x2, y2 - radius);
GL11.glEnd();
GL11.glBegin(3);
GL11.glVertex2f(x2 - radius, y2 - 0.1F);
GL11.glVertex2f(x + radius, y2 - 0.1F);
GL11.glEnd();
GL11.glBegin(3);
GL11.glVertex2f(x + 0.1F, y2 - radius);
GL11.glVertex2f(x + 0.1F, y + radius);
GL11.glEnd();
float f1 = x2 - radius;
float f2 = y + radius;
GL11.glBegin(3);
int k;
for (k = 0; k <= i; k++) {
int m = 90 - k * j;
GL11.glVertex2f((float)(f1 + radius * MathUtil.getRightAngle(m)), (float)(f2 - radius * MathUtil.getAngle(m)));
}
GL11.glEnd();
f1 = x2 - radius;
f2 = y2 - radius;
GL11.glBegin(3);
for (k = 0; k <= i; k++) {
int m = k * j + 270;
GL11.glVertex2f((float)(f1 + radius * MathUtil.getRightAngle(m)), (float)(f2 - radius * MathUtil.getAngle(m)));
}
GL11.glEnd();
GL11.glBegin(3);
f1 = x + radius;
f2 = y2 - radius;
for (k = 0; k <= i; k++) {
int m = k * j + 90;
GL11.glVertex2f((float)(f1 + radius * MathUtil.getRightAngle(m)), (float)(f2 + radius * MathUtil.getAngle(m)));
}
GL11.glEnd();
GL11.glBegin(3);
f1 = x + radius;
f2 = y + radius;
for (k = 0; k <= i; k++) {
int m = 270 - k * j;
GL11.glVertex2f((float)(f1 + radius * MathUtil.getRightAngle(m)), (float)(f2 + radius * MathUtil.getAngle(m)));
}
GL11.glEnd();
if (width != 1.0F)
GL11.glLineWidth(1.0F);
GlStateManager.enableCull();
GlStateManager.disableBlend();
GlStateManager.disableColorMaterial();
GlStateManager.enableTexture2D();
}

public void drawLine(float x, float x1, float y, float thickness, int colour, boolean smooth) {
drawLines(new float[] { x, y, x1, y }, thickness, new Color(colour, true), smooth);
}

public void drawVerticalLine(float x, float y, float y1, float thickness, int colour, boolean smooth) {
drawLines(new float[] { x, y, x, y1 }, thickness, new Color(colour, true), smooth);
}

public static void drawLines(float[] points, float thickness, Color colour, boolean smooth) {
GL11.glPushMatrix();
GL11.glDisable(3553);
GL11.glBlendFunc(770, 771);
if (smooth) {
GL11.glEnable(2848);
} else {
GL11.glDisable(2848);
}
GL11.glLineWidth(thickness);
GL11.glColor4f(colour.getRed() / 255.0F, colour.getGreen() / 255.0F, colour.getBlue() / 255.0F, colour.getAlpha() / 255.0F);
GL11.glBegin(1);
for (int i = 0; i < points.length; i += 2)
GL11.glVertex2f(points[i], points[i + 1]);
GL11.glEnd();
GL11.glEnable(2848);
GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
GL11.glEnable(3553);
GL11.glPopMatrix();
}

public void drawOutline(int x, int y, int width, int height, float thickness, int color) {
drawLine(x, x + width, y, thickness, color, false);
drawLine(x, x + width, y + height, thickness, color, false);

drawVerticalLine(x, y, y + height, thickness, color, false);
drawVerticalLine(x + width, y, y + height, thickness, color, false);
}

public static int drawString(String text, int x, int y, boolean shadow) {
GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
return drawString(text, x, y, 16777215, shadow);
}

public static int drawString(String text, int x, int y, int color, boolean shadow) {
GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
String[] lines = text.split("\n");
if (lines.length > 1) {
int j = 0;
for (int i = 0; i < lines.length; i++)
    j += Minecraft.getMinecraft().fontRendererObj.drawString(lines[i], x, (y + i * (Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT + 2)), color, shadow);
return j;
}
return Minecraft.getMinecraft().fontRendererObj.drawString(text, x, y, color, shadow);
}

public static int drawScaledString(String text, int x, int y, boolean shadow, float scale) {
GlStateManager.pushMatrix();
GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
GlStateManager.scale(scale, scale, 1.0F);
int i = drawString(text, (int)(x / scale), (int)(y / scale), shadow);
GlStateManager.scale(Math.pow(scale, -1.0D), Math.pow(scale, -1.0D), 1.0D);
GlStateManager.popMatrix();

return i;
}

}