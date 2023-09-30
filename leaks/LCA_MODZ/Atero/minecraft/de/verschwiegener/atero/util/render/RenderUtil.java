package de.verschwiegener.atero.util.render;

import java.awt.Color;
import java.awt.Point;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.glu.GLU;

import javax.vecmath.Vector3d;

public class RenderUtil {

    private static ResourceLocation background = new ResourceLocation("atero/assets/background.jpg");
	private static Frustum frustum = new Frustum();
	private final static IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
	private final static FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
	private final static FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
	private static final FloatBuffer vector = GLAllocation.createDirectFloatBuffer(4);
    public static void disable() {
	GL11.glEnd();
	GL11.glDisable(3042);
	GL11.glEnable(3553);
	GL11.glEnable(2929);
	GL11.glDisable(2848);
	GL11.glDisable(3042);
	GL11.glPopAttrib();
	GL11.glPopMatrix();
    }

    private static void drawCircle(final float x, final float y, final float diameter, final int start, final int stop,
	    final Color color) {
	final double twicePi = Math.PI * 2;

	GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F,
		color.getAlpha() / 255.0F);

	for (int i = start; i <= stop; i++) {
	    GL11.glVertex2d(x + diameter * Math.sin(i * twicePi / stop), y + diameter * Math.cos(i * twicePi / stop));
	}
    }
    
    public static void setColor(Color color) {
	GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F,
		color.getAlpha() / 255.0F);
    }

    public static void drawCircleP(final float x, final float y, final float diameter, final int start, final int stop,
	    final Color color) {
	final double twicePi = Math.PI * 2;

	GL11.glPushMatrix();
	GL11.glLineWidth(5F);
	GL11.glScaled(0.5, 0.5, 0.5);

	GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F,
		color.getAlpha() / 255.0F);

	GL11.glBegin(GL11.GL_LINE_LOOP);
	for (int i = start; i <= stop; i++) {
	    GL11.glVertex2d(x + diameter * Math.sin(i * twicePi / stop), y + diameter * Math.cos(i * twicePi / stop));
	}
	GL11.glEnd();
	GL11.glPopMatrix();
	Display.update();
    }

    public static void drawRect(double left, double top, double right, double bottom, int color) {
	if (left < right) {
	    double i = left;
	    left = right;
	    right = i;
	}

	if (top < bottom) {
	    double j = top;
	    top = bottom;
	    bottom = j;
	}

	float f3 = (float) (color >> 24 & 255) / 255.0F;
	float f = (float) (color >> 16 & 255) / 255.0F;
	float f1 = (float) (color >> 8 & 255) / 255.0F;
	float f2 = (float) (color & 255) / 255.0F;
	Tessellator tessellator = Tessellator.getInstance();
	WorldRenderer worldrenderer = tessellator.getWorldRenderer();
	GlStateManager.enableBlend();
	GlStateManager.disableTexture2D();
	GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
	GlStateManager.color(f, f1, f2, f3);
	worldrenderer.begin(7, DefaultVertexFormats.POSITION);
	worldrenderer.pos(left, bottom, 0.0D).endVertex();
	worldrenderer.pos(right, bottom, 0.0D).endVertex();
	worldrenderer.pos(right, top, 0.0D).endVertex();
	worldrenderer.pos(left, top, 0.0D).endVertex();
	tessellator.draw();
	GlStateManager.enableTexture2D();
	GlStateManager.disableBlend();
    }

    /**
     * Draws a full circle
     *
     * @param x
     * @param y
     * @param diameter
     * @param fill
     * @param color
     */
    public static void drawFullCircle(final float x, final float y, final float diameter, final boolean fill,
	    final Color color) {
	final double twicePi = Math.PI * 2;
	final int triageAmount = (int) Math.max(4, diameter * twicePi);
	GL11.glPushMatrix();
	GL11.glPushAttrib(1048575);
	GL11.glDisable(2929);
	GL11.glDisable(3553);
	GL11.glEnable(2848);
	GL11.glEnable(3042);
	GL11.glBlendFunc(770, 771);
	GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F,
		color.getAlpha() / 255.0F);
	GL11.glBegin(GL11.GL_TRIANGLE_FAN);

	for (int i = 0; i <= triageAmount; i++) {
	    GL11.glVertex2d(x + diameter * Math.sin(i * twicePi / triageAmount),
		    y + diameter * Math.cos(i * twicePi / triageAmount));
	}

	GL11.glEnd();
	GL11.glDisable(3042);
	GL11.glEnable(3553);
	GL11.glEnable(2929);
	GL11.glDisable(2848);
	GL11.glDisable(3042);
	GL11.glColor4d(1.0D, 1.0D, 1.0D, 1.0D);
	GL11.glPopAttrib();
	GL11.glPopMatrix();
    }

    public static void drawPlay(final float x, final float y, final float width, final float height,
	    final Color color) {
	enable();
	GL11.glBegin(GL11.GL_TRIANGLES);
	GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F,
		color.getAlpha() / 255.0F);
	GL11.glVertex2d(x, y);
	GL11.glVertex2d(x, y + height);
	GL11.glVertex2d(x + width, y + (height / 2));
	disable();
    }

    public static void drawPause(final int x, final int y, final int width, final int height, final Color color) {
	fillRect(x, y, width / 2 - 3, height, color);
	fillRect(x + (width / 3) * 2 - 2, y, width / 2 - 3, height, color);
    }

    public static void drawlefthalf(final float x, final float y, final float thicknes, final Color color) {
	final float radius = 70;
	GL11.glPushMatrix();
	GL11.glScaled(0.5, 0.5, 0.5);
	GL11.glLineWidth(thicknes);
	GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F,
		color.getAlpha() / 255.0F);

	GL11.glBegin(GL11.GL_POINTS);

	for (double i = 0; i <= Math.PI; i += 0.001) {
	    GL11.glVertex2d(x + Math.sin(i) * radius, y + Math.cos(i) * radius);
	}
	GL11.glEnd();
	GL11.glPopMatrix();
	// GL11.glFlush();
    }

    public static void drawRect(final int x, final int y, final int width, final int height, final Color color,
	    final float thickness) {

	RenderUtil.enable();

	GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F,
		color.getAlpha() / 255.0F);

	GL11.glLineWidth(thickness);

	GL11.glBegin(GL11.GL_LINE_LOOP);

	GL11.glVertex2f(x, y + height);
	GL11.glVertex2f(x + width, y + height);
	GL11.glVertex2f(x + width, y);
	GL11.glVertex2f(x, y);

	RenderUtil.disable();

    }

    public static void drawCircle(final float x, final float y, final float diameter, final Color color,
	    boolean longPressed) {
	final double twicePi = Math.PI * 2;

	final int triageAmount = longPressed ? (int) Math.max(4, diameter * twicePi / 4) : 10;

	RenderUtil.enable();

	GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F,
		color.getAlpha() / 255.0F);
	GL11.glScaled(0.5f, 0.5f, 0.5f);
	GL11.glBegin(GL11.GL_TRIANGLE_FAN);

	for (int i = 0; i <= triageAmount; i++) {
	    GL11.glVertex2d(x * 2 + diameter * Math.sin(i * twicePi / triageAmount),
		    y * 2 + diameter * Math.cos(i * twicePi / triageAmount));
	}

	RenderUtil.disable();
    }

    public static void drawLine(final int x1, final int y1, final int x2, final int y2, final float thickness,
	    final Color color) {

	RenderUtil.enable();

	GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F,
		color.getAlpha() / 255.0F);

	GL11.glLineWidth(thickness);

	GL11.glBegin(GL11.GL_LINES);

	GL11.glVertex2f(x1, y1);
	GL11.glVertex2f(x2, y2);

	RenderUtil.disable();

    }

    /**
     * Draws a Rect with roundet edges after this principle
     * https://stackoverflow.com/questions/5369507/opengl-es-1-0-2d-rounded-rectangle
     *
     * @param x
     * @param y
     * @param width
     * @param height
     * @param diameter
     * @param color
     */
    public static void drawRectRound(final int x, final int y, final int width, final int height, final int diameter,
	    final Color color) {
	// RenderUtil.fillRect(x - diameter, y - diameter, width - diameter * 2, height
	// - diameter * 2, color);

	// RenderUtil.fillRect(x, y - diameter * 2, width - diameter * 4, diameter,
	// color);

	// RenderUtil.fillRect(x, y + height - diameter * 3, width - diameter * 4,
	// diameter, color);
	fillRect(x, y + diameter, width, height - (diameter * 2), color);
	fillRect(x + 5, y, width - (diameter * 2), height, color);

	RenderUtil.enable();

	GL11.glBegin(GL11.GL_TRIANGLE_FAN);
	// Oberen Kreise
	drawCircle(x + diameter, y + diameter, diameter, 0, 360, color);
	drawCircle(x + width - diameter, y + diameter, diameter, 0, 360, color);

	// Untere Kreise
	drawCircle(x + diameter, y + height - diameter, diameter, 0, 360, color);
	drawCircle(x + width - diameter, y + height - diameter, diameter, 0, 360, color);
	RenderUtil.disable();
    }

    /**
     * Draws a Circle
     *
     * @param x
     * @param y
     * @param diameter
     * @param fill
     * @param color
     */
    public static void drawStringCircle(final float x, final float y, final float diameter, final boolean fill,
	    final Color color) {

	final double twicePi = Math.PI * 2;

	final int triageAmount = (int) Math.max(4, diameter * twicePi / 4);

	RenderUtil.enable();

	GL11.glLineWidth(1f);
	GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F,
		color.getAlpha() / 255.0F);
	GL11.glScaled(0.5f, 0.5f, 0.5f);
	GL11.glBegin(GL11.GL_LINE_LOOP);

	for (int i = 0; i <= triageAmount; i++) {
	    GL11.glVertex2d(x * 2 + diameter * Math.sin(i * twicePi / triageAmount),
		    y * 2 + diameter * Math.cos(i * twicePi / triageAmount));
	}

	RenderUtil.disable();
    }

    public static void enable() {
	GL11.glPushMatrix();
	GL11.glPushAttrib(1048575);
	GL11.glDisable(2929);
	GL11.glDisable(3553);
	GL11.glEnable(2848);
	GL11.glEnable(3042);
	GL11.glBlendFunc(770, 771);
    }

    private static int fact(final int n) {
	int fact = 1;
	for (int i = 1; i <= n; i++) {
	    fact *= i;
	}
	return fact;
    }

    public static void fillCircle(final float x, final float y, final float diameter, final boolean fill,
	    final Color color) {

	final double twicePi = Math.PI * 2;

	final int triageAmount = (int) Math.max(4, diameter * twicePi / 4);

	RenderUtil.enable();

	GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F,
		color.getAlpha() / 255.0F);
	GL11.glScaled(0.5f, 0.5f, 0.5f);
	GL11.glBegin(GL11.GL_TRIANGLE_FAN);

	for (int i = 0; i <= triageAmount; i++) {
	    GL11.glVertex2d(x * 2 + diameter * Math.sin(i * twicePi / triageAmount),
		    y * 2 + diameter * Math.cos(i * twicePi / triageAmount));
	}

	RenderUtil.disable();
    }

    public static void fillRect(final double x, final double y, final double width, final double height,
	    final Color color) {

	RenderUtil.enable();

	GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F,
		color.getAlpha() / 255.0F);

	GL11.glBegin(GL11.GL_QUADS);

	GL11.glVertex2d(x, y + height);
	GL11.glVertex2d(x + width, y + height);
	GL11.glVertex2d(x + width, y);
	GL11.glVertex2d(x, y);

	RenderUtil.disable();
    }

    public static void drawImage(ResourceLocation location, int x, int y, int width, int height) {
	// enable();
	GL11.glDisable(2929);
	GL11.glEnable(3042);
	GL11.glDepthMask(false);
	// OpenGlHelper.glBlendFunc(770, 771, 1, 0);
	// GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	Minecraft.getMinecraft().getTextureManager().bindTexture(location);
	Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, width, height, width, height);
	// disable();
	// Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, width, height,
	// width, height);
	GL11.glDepthMask(true);
	GL11.glDisable(3042);
	GL11.glEnable(2929);
    }

    public static void drawBackround(int width, int height) {
	GlStateManager.disableLighting();
	GlStateManager.disableFog();
	GlStateManager.enableTexture2D();
	GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	Minecraft.getMinecraft().getTextureManager().bindTexture(background);
	Gui.drawModalRectWithCustomSizedTexture(0, 0, 0.0F, 0.0F, width, height, width, height);
	RenderUtil.fillRect(0, 0, width, height, new Color(0, 0, 0, 220));
	GlStateManager.disableTexture2D();
    }

    public static void getScissor(int x, int yBottom, int width, int height, int guiScaleFactor) {
	GL11.glScissor((int) (x * guiScaleFactor),
		(int) (Minecraft.getMinecraft().getFramebuffer().framebufferHeight - (yBottom * guiScaleFactor)),
		(int) (width * guiScaleFactor), (int) (height * guiScaleFactor));

    }


	public static double interpolate(double current, double old, double scale) {
		return old + (current - old) * scale;
	}
}
