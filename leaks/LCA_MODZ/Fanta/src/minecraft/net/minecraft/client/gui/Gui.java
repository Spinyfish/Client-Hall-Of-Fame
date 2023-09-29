package net.minecraft.client.gui;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import de.fanta.gui.font.BasicFontRenderer;
import de.fanta.module.impl.visual.Themes;
import de.fanta.utils.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class Gui {
	public static final ResourceLocation optionsBackground = new ResourceLocation(
			"textures/gui/options_background.png");
	public static final ResourceLocation statIcons = new ResourceLocation("textures/gui/container/stats_icons.png");
	public static final ResourceLocation icons = new ResourceLocation("textures/gui/icons.png");
	public static float zLevel;

	/**
	 * Draws a solid color rectangle with the specified coordinates and color (ARGB
	 * format). Args: x1, y1, x2, y2, color
	 */

	public static void drawRect2(double x1, double y1, double x2, double y2, int argbColor) {
		GlStateManager.color(1, 1, 1, 1);
		if (x1 < x2) {
			double temp = x1;
			x1 = x2;
			x2 = temp;
		}

		if (y1 < y2) {
			double temp1 = y1;
			y1 = y2;
			y2 = temp1;
		}

		float a = (float) (argbColor >> 24 & 255) / 255.0F;
		float r = (float) (argbColor >> 16 & 255) / 255.0F;
		float g = (float) (argbColor >> 8 & 255) / 255.0F;
		float b = (float) (argbColor & 255) / 255.0F;
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.color(r, g, b, a);
		worldrenderer.begin(7, DefaultVertexFormats.POSITION);
		worldrenderer.pos((double) x1, (double) y2, 0.0D).endVertex();
		worldrenderer.pos((double) x2, (double) y2, 0.0D).endVertex();
		worldrenderer.pos((double) x2, (double) y1, 0.0D).endVertex();
		worldrenderer.pos((double) x1, (double) y1, 0.0D).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}

	public static void drawRGBLineHorizontal(double x, double y, double width, float linewidth, int colors,
			boolean reverse) {
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GL11.glLineWidth(linewidth);
		GL11.glBegin(GL11.GL_LINE_STRIP);
		colors *= width;
		double steps = width / colors;
		double cX = x, cX2 = x + steps;
		if (reverse) {
			for (int i = colors; i > 0; i--) {
				int argbColor = Themes.rainbow(i * 10);
				float a = (float) (argbColor >> 24 & 255) / 255.0F;
				float r = (float) (argbColor >> 16 & 255) / 255.0F;
				float g = (float) (argbColor >> 8 & 255) / 255.0F;
				float b = (float) (argbColor & 255) / 255.0F;
				GlStateManager.color(r, g, b, a);
				GL11.glVertex2d(cX, y);
				GL11.glVertex2d(cX2, y);
				cX = cX2;
				cX2 += steps;
			}
		} else {
			for (int i = 0; i < colors; i++) {
				int argbColor = Themes.rainbow(i * 10);
				float a = (float) (argbColor >> 24 & 255) / 255.0F;
				float r = (float) (argbColor >> 16 & 255) / 255.0F;
				float g = (float) (argbColor >> 8 & 255) / 255.0F;
				float b = (float) (argbColor & 255) / 255.0F;
				GlStateManager.color(r, g, b, a);
				GL11.glVertex2d(cX, y);
				GL11.glVertex2d(cX2, y);
				cX = cX2;
				cX2 += steps;
			}
		}
		GL11.glEnd();
		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}

	public static void drawRGBLineVertical(double x, double y, double height, float linewidth, int colors,
			boolean reverse) {
		GlStateManager.shadeModel(GL11.GL_SMOOTH);
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GL11.glLineWidth(linewidth);
		GL11.glBegin(GL11.GL_LINE_STRIP);
		colors *= height;
		double steps = height / colors;
		double cY = y, cY2 = y + steps;
		if (reverse) {
			for (int i = colors; i > 0; i--) {
				int argbColor = Themes.rainbow(i * 10);
				float a = (float) (argbColor >> 24 & 255) / 255.0F;
				float r = (float) (argbColor >> 16 & 255) / 255.0F;
				float g = (float) (argbColor >> 8 & 255) / 255.0F;
				float b = (float) (argbColor & 255) / 255.0F;
				GlStateManager.color(r, g, b, a);
				GL11.glVertex2d(x, cY);
				GL11.glVertex2d(x, cY2);
				cY = cY2;
				cY2 += steps;
			}
		} else {
			for (int i = 0; i < colors; i++) {
				int argbColor = Themes.rainbow(i * 10);
				float a = (float) (argbColor >> 24 & 255) / 255.0F;
				float r = (float) (argbColor >> 16 & 255) / 255.0F;
				float g = (float) (argbColor >> 8 & 255) / 255.0F;
				float b = (float) (argbColor & 255) / 255.0F;
				GlStateManager.color(r, g, b, a);
				GL11.glVertex2d(x, cY);
				GL11.glVertex2d(x, cY2);
				cY = cY2;
				cY2 += steps;
			}
		}
		GL11.glEnd();
		GlStateManager.shadeModel(GL11.GL_FLAT);
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}

	public static void drawRect(float h, float l, float k, float g, int color) {
		GlStateManager.color(1, 1, 1, 1);
		if (h < k) {
			int i = (int) h;
			h = k;
			k = i;
		}

		if (l < g) {
			int j = (int) l;
			l = (int) g;
			g = j;
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
		worldrenderer.pos((double) h, (double) g, 0.0D).endVertex();
		worldrenderer.pos((double) k, (double) g, 0.0D).endVertex();
		worldrenderer.pos((double) k, (double) l, 0.0D).endVertex();
		worldrenderer.pos((double) h, (double) l, 0.0D).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}

	/**
	 * Draws a textured rectangle at z = 0. Args: x, y, u, v, width, height,
	 * textureWidth, textureHeight
	 */
	public static void drawModalRectWithCustomSizedTexture(float x, float y, float u, float v, float width,
			float height, float textureWidth, float textureHeight) {
		float f = 1.0F / textureWidth;
		float f1 = 1.0F / textureHeight;
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		GL11.glEnable(3042);
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		worldrenderer.pos(x, y + height, 0.0D).tex(u * f, (v + (float) height) * f1).endVertex();
		worldrenderer.pos(x + width, y + height, 0.0D).tex((u + (float) width) * f, (v + (float) height) * f1)
				.endVertex();
		worldrenderer.pos(x + width, y, 0.0D).tex((u + (float) width) * f, v * f1).endVertex();
		worldrenderer.pos(x, y, 0.0D).tex(u * f, v * f1).endVertex();
		tessellator.draw();
	}

	/**
	 * Draws a scaled, textured, tiled modal rect at z = 0. This method isn't used
	 * anywhere in vanilla code.
	 */
	public static void drawScaledCustomSizeModalRect(int x, int y, float u, float v, int uWidth, int vHeight, int width,
			int height, float tileWidth, float tileHeight) {
		float f = 1.0F / tileWidth;
		float f1 = 1.0F / tileHeight;
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		worldrenderer.pos(x, y + height, 0.0D).tex(u * f, (v + (float) vHeight) * f1).endVertex();
		worldrenderer.pos(x + width, y + height, 0.0D).tex((u + (float) uWidth) * f, (v + (float) vHeight) * f1)
				.endVertex();
		worldrenderer.pos(x + width, y, 0.0D).tex((u + (float) uWidth) * f, v * f1).endVertex();
		worldrenderer.pos(x, y, 0.0D).tex(u * f, v * f1).endVertex();
		tessellator.draw();
	}

	/**
	 * Draw a 1 pixel wide horizontal line. Args: x1, x2, y, color
	 */
	protected void drawHorizontalLine(int startX, int endX, int y, int color) {
		if (endX < startX) {
			int i = startX;
			startX = endX;
			endX = i;
		}

		drawRect(startX, y, endX + 1, y + 1, color);
	}

	/**
	 * Draw a 1 pixel wide vertical line. Args : x, y1, y2, color
	 */
	protected void drawVerticalLine(int x, int startY, int endY, int color) {
		if (endY < startY) {
			int i = startY;
			startY = endY;
			endY = i;
		}

		drawRect(x, startY + 1, x + 1, endY, color);
	}

	/**
	 * Draws a rectangle with a vertical gradient between the specified colors (ARGB
	 * format). Args : x1, y1, x2, y2, topColor, bottomColor
	 */
	public static void drawGradientRect(double left, double top, double right, double bottom, int startColor, int endColor) {
		float f = (float) (startColor >> 24 & 255) / 255.0F;
		float f1 = (float) (startColor >> 16 & 255) / 255.0F;
		float f2 = (float) (startColor >> 8 & 255) / 255.0F;
		float f3 = (float) (startColor & 255) / 255.0F;
		float f4 = (float) (endColor >> 24 & 255) / 255.0F;
		float f5 = (float) (endColor >> 16 & 255) / 255.0F;
		float f6 = (float) (endColor >> 8 & 255) / 255.0F;
		float f7 = (float) (endColor & 255) / 255.0F;
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		GlStateManager.shadeModel(7425);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		worldrenderer.pos(right, top, zLevel).color(f1, f2, f3, f).endVertex();
		worldrenderer.pos(left, top, zLevel).color(f1, f2, f3, f).endVertex();
		worldrenderer.pos(left, bottom, zLevel).color(f5, f6, f7, f4).endVertex();
		worldrenderer.pos(right, bottom, zLevel).color(f5, f6, f7, f4).endVertex();
		tessellator.draw();
		GlStateManager.shadeModel(7424);
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
	}

	/**
	 * Renders the specified text to the screen, center-aligned. Args : renderer,
	 * string, x, y, color
	 */
	public void drawCenteredString(BasicFontRenderer fontRendererIn, String text, int x, int y, int color) {
		fontRendererIn.drawStringWithShadow(text, (float) (x - fontRendererIn.getStringWidth(text) / 2), (float) y,
				color);
	}

	/**
	 * Renders the specified text to the screen. Args : renderer, string, x, y,
	 * color
	 */
	public void drawString(BasicFontRenderer fontRendererIn, String text, float x, float y, int color) {
		fontRendererIn.drawStringWithShadow(text, (float) x, (float) y, color);
	}

	/**
	 * Draws a textured rectangle at the stored z-value. Args: x, y, u, v, width,
	 * height
	 */
	public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		worldrenderer.pos(x + 0, y + height, this.zLevel)
				.tex((float) (textureX + 0) * f, (float) (textureY + height) * f1).endVertex();
		worldrenderer.pos(x + width, y + height, this.zLevel)
				.tex((float) (textureX + width) * f, (float) (textureY + height) * f1).endVertex();
		worldrenderer.pos(x + width, y + 0, this.zLevel)
				.tex((float) (textureX + width) * f, (float) (textureY + 0) * f1).endVertex();
		worldrenderer.pos(x + 0, y + 0, this.zLevel).tex((float) (textureX + 0) * f, (float) (textureY + 0) * f1)
				.endVertex();
		tessellator.draw();
	}

	/**
	 * Draws a textured rectangle using the texture currently bound to the
	 * TextureManager
	 */
	public void drawTexturedModalRect(float xCoord, float yCoord, int minU, int minV, int maxU, int maxV) {
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		worldrenderer.pos(xCoord + 0.0F, yCoord + (float) maxV, this.zLevel)
				.tex((float) (minU + 0) * f, (float) (minV + maxV) * f1).endVertex();
		worldrenderer.pos(xCoord + (float) maxU, yCoord + (float) maxV, this.zLevel)
				.tex((float) (minU + maxU) * f, (float) (minV + maxV) * f1).endVertex();
		worldrenderer.pos(xCoord + (float) maxU, yCoord + 0.0F, this.zLevel)
				.tex((float) (minU + maxU) * f, (float) (minV + 0) * f1).endVertex();
		worldrenderer.pos(xCoord + 0.0F, yCoord + 0.0F, this.zLevel)
				.tex((float) (minU + 0) * f, (float) (minV + 0) * f1).endVertex();
		tessellator.draw();
	}

	/**
	 * Draws a texture rectangle using the texture currently bound to the
	 * TextureManager
	 */
	public void drawTexturedModalRect(int xCoord, int yCoord, TextureAtlasSprite textureSprite, int widthIn,
			int heightIn) {
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		worldrenderer.pos(xCoord + 0, yCoord + heightIn, this.zLevel)
				.tex(textureSprite.getMinU(), textureSprite.getMaxV()).endVertex();
		worldrenderer.pos(xCoord + widthIn, yCoord + heightIn, this.zLevel)
				.tex(textureSprite.getMaxU(), textureSprite.getMaxV()).endVertex();
		worldrenderer.pos(xCoord + widthIn, yCoord + 0, this.zLevel)
				.tex(textureSprite.getMaxU(), textureSprite.getMinV()).endVertex();
		worldrenderer.pos(xCoord + 0, yCoord + 0, this.zLevel).tex(textureSprite.getMinU(), textureSprite.getMinV())
				.endVertex();
		tessellator.draw();
	}

	public void drawHollowRect(float x, float y, float width, float height, float lineWidth, int color) {
		drawRect2(x, y, x + lineWidth, y + height, color);
		drawRect2(x + width - lineWidth, y, x + width, y + height, color);
		drawRect2(x, y, x + width, y + lineWidth, color);
		drawRect2(x, y + height - lineWidth, x + width, y + height, color);
	}

	public void drawHollowRect(double x, double y, double width, double height, double lineWidth, int color) {
		drawRect2(x, y, x + lineWidth, y + height, color);
		drawRect2(x + width - lineWidth, y, x + width, y + height, color);
		drawRect2(x, y, x + width, y + lineWidth, color);
		drawRect2(x, y + height - lineWidth, x + width, y + height, color);
	}

	public static void draw2dLine(double x, double z, double x1, double z1, Color color) {
		enableRender3D(true);
		RenderUtil.glColor(color.getRGB());
		GL11.glBegin(1);
		GL11.glVertex2d(x, z);
		GL11.glVertex2d(x1, z1);
		GL11.glEnd();
		disableRender3D(true);
	}

	public static void draw2dLine(double x, double z, double x1, double z1, double lineWidth, Color color) {
		enableRender3D(true);
		RenderUtil.glColor(color.getRGB());
		GL11.glLineWidth((float) lineWidth);
		GL11.glBegin(1);
		GL11.glVertex2d(x, z);
		GL11.glVertex2d(x1, z1);
		GL11.glEnd();
		disableRender3D(true);
	}

	private static void enableRender3D(boolean disableDepth) {
		if (disableDepth) {
			GL11.glDepthMask(false);
			GL11.glDisable(2929);
		}
		GL11.glDisable(3008);
		GL11.glEnable(3042);
		GL11.glDisable(3553);
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(2848);
		GL11.glHint(3154, 4354);
		GL11.glLineWidth(1.0F);
	}

	private static void disableRender3D(boolean enableDepth) {
		if (enableDepth) {
			GL11.glDepthMask(true);
			GL11.glEnable(2929);
		}
		GL11.glEnable(3553);
		GL11.glDisable(3042);
		GL11.glEnable(3008);
		GL11.glDisable(2848);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

}
