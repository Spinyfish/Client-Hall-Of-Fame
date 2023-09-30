package felix.util.visual;

import java.awt.Color;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.glu.GLU;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

/**
 * @author sendQueue <Vinii>
 *
 *         Further info at Vinii.de or github@vinii.de, file created at 20.10.2020. 
 *         Use is only authorized if given credit!
 * 
 */
public class RenderUtil {
	
	private static final Frustum frustrum = new Frustum();
	
	/**
	 * Makes a color darker with a custom factor.
	 * <p>
	 * <h1>CAUTION: 0 < FACTOR < 1!</h1>
	 * <p>
	 * Might be inconsistent because of rounding errors.
	 * 
	 * @param color
	 * @param factor
	 * @return
	 */
	public static Color darker(Color color, float factor) {
		//in case of keks
		factor = MathHelper.clamp_float(factor, 0.001f, 0.999f);
		
		return new Color(Math.max((int) (color.getRed() * factor), 0), Math.max((int) (color.getGreen() * factor), 0),
				Math.max((int) (color.getBlue() * factor), 0), color.getAlpha());
	}

	/**
	 * Makes a color brighter/lighter with a custom factor.
	 * <p>
	 * <h1>CAUTION: 0 < FACTOR < 1!</h1>
	 * <p>
	 * Might be inconsistent because of rounding errors.
	 * 
	 * @param color
	 * @param factor
	 * @return
	 */
	public static Color brighter(Color color, float factor) {
		//in case of keks
		factor = MathHelper.clamp_float(factor, 0.001f, 0.999f);
		
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();
		int alpha = color.getAlpha();

		/*
		 * From 2D group: 1. black.brighter() should return grey 2. applying brighter to
		 * blue will always return blue, brighter 3. non pure color (non zero rgb) will
		 * eventually return white
		 */

		int i = (int) (1.0 / (1.0 - factor));
		if (r == 0 && g == 0 && b == 0) {
			return new Color(i, i, i, alpha);
		}
		if (r > 0 && r < i)
			r = i;
		if (g > 0 && g < i)
			g = i;
		if (b > 0 && b < i)
			b = i;

		return new Color(Math.min((int) (r / factor), 255), Math.min((int) (g / factor), 255),
				Math.min((int) (b / factor), 255), alpha);
	}
	
	/**
	 * Defines a rectangle (scissorBox) in window coordinates not GL's: from
	 * https://vinii.de/github/LWJGLUtil/scissorBoxGL.png to
	 * https://vinii.de/github/LWJGLUtil/scissorBoxWindow.png
	 * 
	 * @param x
	 * @param y
	 * @param x2
	 * @param y2
	 */
	public static void scissorBox(final int x, final int y, final int width, final int height) {
		final ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
		final int factor = scaledResolution.getScaleFactor();

		GL11.glEnable(GL11.GL_SCISSOR_TEST);

		GL11.glScissor(x * factor, (scaledResolution.getScaledHeight() - (y + height)) * factor,
				((x + width) - x) * factor, ((y + height) - y) * factor);

		// disable GL_SCISSOR_TEST after bounding
	}
	
	
	/**
	 * Draws a rectangle.
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param color
	 */
	public static void drawRect(final float x, final float y, final float width, final float height, final int color) {
		GL11.glEnable(3042);
		GL11.glDisable(3553);
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(2848);
		GL11.glShadeModel(7425);

		GL11.glPushMatrix();
		GL11.glBegin(7);

		glColor(color);

		GL11.glVertex2d(x, y);
		GL11.glVertex2d(x, y + height);
		GL11.glVertex2f(x + width, y + height);
		GL11.glVertex2f(x + width, y);

		GL11.glEnd();
		GL11.glPopMatrix();

		GL11.glEnable(3553);
		GL11.glDisable(3042);
		GL11.glDisable(2848);
		GL11.glShadeModel(7424);
	}
	
	public static void drawFace(final int x, final int y, final ResourceLocation resourceLocation) {
        GlStateManager.enableBlend();
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
        GlStateManager.enableBlend();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.8f);
        Gui.drawScaledCustomSizeModalRect(x, y, 8.0f, 8.0f, 8, 8, 31, 31, 64.0f, 64.0f);
        GlStateManager.disableBlend();
	}
	
	public static void drawRect(final double x, final double y, final double width, final double height, final int color) {
		GL11.glEnable(3042);
		GL11.glDisable(3553);
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(2848);
		GL11.glShadeModel(7425);

		GL11.glPushMatrix();
		GL11.glBegin(7);

		glColor(color);

		GL11.glVertex2d(x, y);
		GL11.glVertex2d(x, y + height);
		GL11.glVertex2f((float)x + (float)width, (float)y + (float)height);
		GL11.glVertex2f((float)x + (float)width, (float)y);

		GL11.glEnd();
		GL11.glPopMatrix();

		GL11.glEnable(3553);
		GL11.glDisable(3042);
		GL11.glDisable(2848);
		GL11.glShadeModel(7424);
	}
	
	public static void drawRoundedRect2(final double x, final double y, final double width, final double height, double radius, int color) {
		RenderUtil.drawRoundedRect(x, y, width - x, height - y, radius, color);
	}
	
	public static void drawRect2(final double x, final double y, final double width, final double height, final int color) {
		RenderUtil.drawRect(x, y, width - x, height - y, color);
	}
	
	public static void drawVerticalGradient2(final float x, final float y, final float width, final float height, final int bottomColor, final int topColor) {
		RenderUtil.drawVerticalGradient(x, y, width - x, height - y, topColor, bottomColor);
	}
	
	public static void drawHorizontalGardient2(final float x, final float y, final float width, final float height, final int leftColor, final int rightColor) {
		RenderUtil.drawHorizontalGradient(x, y, width - x, height - y, leftColor, rightColor);
	}
	
	public static void drawBorderedRect2(final double x, final double y, final double width, final double height, final float lineSize, final int borderColor, final int color) {
		RenderUtil.drawBorderedRect(x, y, width - x, height - y, lineSize, borderColor, color);
	}
	
	/**
	 * Draws rect with rounded corners, how it's made:
	 * https://vinii.de/github/LWJGLUtil/roundedRect.png
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param radius
	 * @param color
	 */
	 public static void drawRoundedRect(double x, double y, double width, double height, double radius, int color) {
	        GlStateManager.enableBlend();
	        GlStateManager.disableTexture2D();
	        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
	        double x1 = x + width;
	        double y1 = y + height;
	        float f = (color >> 24 & 0xFF) / 255.0F;
	        float f1 = (color >> 16 & 0xFF) / 255.0F;
	        float f2 = (color >> 8 & 0xFF) / 255.0F;
	        float f3 = (color & 0xFF) / 255.0F;
	        GL11.glPushAttrib(0);
	        GL11.glScaled(0.5, 0.5, 0.5);

	        x *= 2;
	        y *= 2;
	        x1 *= 2;
	        y1 *= 2;

	        GL11.glDisable(GL11.GL_TEXTURE_2D);
	        GL11.glColor4f(f1, f2, f3, f);
	        GL11.glEnable(GL11.GL_LINE_SMOOTH);

	        GL11.glBegin(GL11.GL_POLYGON);

	        for (int i = 0; i <= 90; i += 3) {
	            GL11.glVertex2d(x + radius + +(Math.sin((i * Math.PI / 180)) * (radius * -1)), y + radius + (Math.cos((i * Math.PI / 180)) * (radius * -1)));
	        }

	        for (int i = 90; i <= 180; i += 3) {
	            GL11.glVertex2d(x + radius + (Math.sin((i * Math.PI / 180)) * (radius * -1)), y1 - radius + (Math.cos((i * Math.PI / 180)) * (radius * -1)));
	        }

	        for (int i = 0; i <= 90; i += 3) {
	            GL11.glVertex2d(x1 - radius + (Math.sin((i * Math.PI / 180)) * radius), y1 - radius + (Math.cos((i * Math.PI / 180)) * radius));
	        }

	        for (int i = 90; i <= 180; i += 3) {
	            GL11.glVertex2d(x1 - radius + (Math.sin((i * Math.PI / 180)) * radius), y + radius + (Math.cos((i * Math.PI / 180)) * radius));
	        }

	        GL11.glEnd();

	        GL11.glEnable(GL11.GL_TEXTURE_2D);
	        GL11.glDisable(GL11.GL_LINE_SMOOTH);
	        GL11.glEnable(GL11.GL_TEXTURE_2D);

	        GL11.glScaled(2, 2, 2);

	        GL11.glPopAttrib();
	        GL11.glColor4f(1, 1, 1, 1);
	        GlStateManager.enableTexture2D();
	        GlStateManager.disableBlend();

	    }

	/**
	 * Draws a rect, as in {@link Gui}#drawRect.
	 * 
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 * @param color
	 */
	public static void relativeRect(final float left, final float top, final float right, final float bottom,
			final int color) {

		final Tessellator tessellator = Tessellator.getInstance();
		final WorldRenderer worldRenderer = tessellator.getWorldRenderer();

		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		glColor(color);


		tessellator.draw();

		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
	}

	/**
	 * Draws a polygon circle
	 * 
	 * @param x
	 * @param y
	 * @param sideLength
	 * @param amountOfSides
	 * @param filled
	 * @param color
	 */
	public static final void polygonCircle(final double x, final double y, double sideLength, final double degree,
			final int color) {
		sideLength *= 0.5;

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_CULL_FACE);

		GlStateManager.disableAlpha();

		glColor(color);

		GL11.glLineWidth(1);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		// since its filled, otherwise GL_LINE_STRIP
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
		for (double i = 0; i <= degree; i++) {
			final double angle = i * (Math.PI * 2) / degree;

			GL11.glVertex2d(x + (sideLength * Math.cos(angle)) + sideLength,
					y + (sideLength * Math.sin(angle)) + sideLength);
		}

		GL11.glColor4f(1, 1, 1, 1);
		GL11.glEnd();
		GL11.glDisable(GL11.GL_LINE_SMOOTH);

		GlStateManager.enableAlpha();

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
	}
	
	/**
	 * 
	 * Draws a horizontal gradient rect
	 * 
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 * @param col1
	 * @param col2
	 */
	public static void drawHorizontalGradient(final float x, final float y, final float width, final float height,
			final int leftColor, final int rightColor) {
		GL11.glEnable(3042);
		GL11.glDisable(3553);
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(2848);
		GL11.glShadeModel(7425);

		GL11.glPushMatrix();
		GL11.glBegin(7);

		glColor(leftColor);

		GL11.glVertex2d(x, y);
		GL11.glVertex2d(x, y + height);

		glColor(rightColor);

		GL11.glVertex2f(x + width, y + height);
		GL11.glVertex2f(x + width, y);

		GL11.glEnd();
		GL11.glPopMatrix();

		GL11.glEnable(3553);
		GL11.glDisable(3042);
		GL11.glDisable(2848);
		GL11.glShadeModel(7424);
	}
  
	/**
	 * Draws a vertical gradient rect
	 * 
	 * @param x
	 * @param y
	 * @param x1
	 * @param y1
	 * @param topColor
	 * @param bottomColor
	 */
	public static void drawVerticalGradient(final float x, final float y, final float width, final float height,
			final int topColor, final int bottomColor) {
		GL11.glEnable(3042);
		GL11.glDisable(3553);
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(2848);
		GL11.glShadeModel(7425);
		
		GL11.glPushMatrix();
		GL11.glBegin(7);
		
		glColor(topColor);
		
		GL11.glVertex2f(x, y + height);
		GL11.glVertex2f(x + width, y + height);
		
		glColor(bottomColor);
		
		GL11.glVertex2f(x + width, y);
		GL11.glVertex2f(x, y);
		
		GL11.glEnd();
		GL11.glPopMatrix();
		
		GL11.glEnable(3553);
		GL11.glDisable(3042);
		GL11.glDisable(2848);
		GL11.glShadeModel(7424);
	}
  
  	/**
	 * Sets color from hex
	 * 
	 * @param hex
	 */
	   public static void glColor(int hex) {
		      float alpha = (float)(hex >> 24 & 255) / 255.0F;
		      float red = (float)(hex >> 16 & 255) / 255.0F;
		      float green = (float)(hex >> 8 & 255) / 255.0F;
		      float blue = (float)(hex & 255) / 255.0F;
		      GL11.glColor4f(red, green, blue, alpha);
		   }

    public static int getRainbow(int speed, int offset, float s) {
        float hue = (System.currentTimeMillis() + offset) % speed;
        hue /= speed;
        return Color.getHSBColor(hue, s, 1f).getRGB();

    }

    public static void drawBorderedRect(double x, double y, double width, double height, double lineSize, int borderColor, int color) {
        Gui.drawRect(x, y, x + width, y + height, color);
        Gui.drawRect(x, y, x + width, y + lineSize, borderColor);
        Gui.drawRect(x, y, x + lineSize, y + height, borderColor);
        Gui.drawRect(x + width, y, x + width - lineSize, y + height, borderColor);
        Gui.drawRect(x, y + height, x + width, y + height - lineSize, borderColor);
    }

    public static int getHealthColor(EntityLivingBase player) {
        float f = player.getHealth();
        float f1 = player.getMaxHealth();
        float f2 = Math.max(0.0F, Math.min(f, f1) / f1);
        return Color.HSBtoRGB(f2 / 3.0F, 1.0F, 1.0F) | 0xFF000000;
    }

    public static void prepareScissorBox(float x, float y, float x2, float y2) {
        ScaledResolution scale = new ScaledResolution(Minecraft.getMinecraft());
        int factor = scale.getScaleFactor();
        GL11.glScissor((int) (x * (float) factor), (int) (((float) scale.getScaledHeight() - y2) * (float) factor), (int) ((x2 - x) * (float) factor), (int) ((y2 - y) * (float) factor));
    }

	public static void startDrawing() {
		GL11.glEnable((int) 3042);
		GL11.glEnable((int) 3042);
		GL11.glBlendFunc((int) 770, (int) 771);
		GL11.glEnable((int) 2848);
		GL11.glDisable((int) 3553);
		GL11.glDisable((int) 2929);
		Minecraft.getMinecraft().entityRenderer.setupCameraTransform(Minecraft.getMinecraft().timer.renderPartialTicks,
				0);
	}

	public static void stopDrawing() {
		GL11.glDisable((int) 3042);
		GL11.glEnable((int) 3553);
		GL11.glDisable((int) 2848);
		GL11.glDisable((int) 3042);
		GL11.glEnable((int) 2929);
	}

	   public static boolean isInViewFrustrum(Entity entity) {
		      return isInViewFrustrum(entity.getEntityBoundingBox()) || entity.ignoreFrustumCheck;
		   }
	   
	   private static boolean isInViewFrustrum(AxisAlignedBB bb) {
		      Entity current = Minecraft.getMinecraft().getRenderViewEntity();
		      frustrum.setPosition(current.posX, current.posY, current.posZ);
		      return frustrum.isBoundingBoxInFrustum(bb);
		   }

	   public static double interpolateScale(double current, double old, double scale) {
		      return old + (current - old) * scale;
	   }
	   
	    public static double interpolate(double old,
                double now,
                float partialTicks) {
	    	return old + (now - old) * partialTicks;
	    }

	    public static float interpolate(float old,
               float now,
               float partialTicks) {
		return old + (now - old) * partialTicks;
		}

	    
	   public static void glColor(float alpha, int redRGB, int greenRGB, int blueRGB) {
		      float red = 0.003921569F * (float)redRGB;
		      float green = 0.003921569F * (float)greenRGB;
		      float blue = 0.003921569F * (float)blueRGB;
		      GL11.glColor4f(red, green, blue, alpha);
		   }
	   
		public static void drawBoundingBox(AxisAlignedBB aa) {
			Tessellator tessellator = Tessellator.getInstance();
			WorldRenderer worldRenderer = tessellator.getWorldRenderer();
			worldRenderer.begin(7, DefaultVertexFormats.field_181703_c);
			worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
			worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
			worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
			worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
			worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
			worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
			worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
			worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
			tessellator.draw();
			worldRenderer.begin(7, DefaultVertexFormats.field_181703_c);
			worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
			worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
			worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
			worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
			worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
			worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
			worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
			worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
			tessellator.draw();
			worldRenderer.begin(7, DefaultVertexFormats.field_181703_c);
			worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
			worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
			worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
			worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
			worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
			worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
			worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
			worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
			tessellator.draw();
			worldRenderer.begin(7, DefaultVertexFormats.field_181703_c);
			worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
			worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
			worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
			worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
			worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
			worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
			worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
			worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
			tessellator.draw();
			worldRenderer.begin(7, DefaultVertexFormats.field_181703_c);
			worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
			worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
			worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
			worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
			worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
			worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
			worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
			worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
			tessellator.draw();
			worldRenderer.begin(7, DefaultVertexFormats.field_181703_c);
			worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
			worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
			worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
			worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
			worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
			worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
			worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
			worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
			tessellator.draw();
		}
		   public static void func_181662_bt3D() {
		      GL11.glDepthMask(true);
		      GL11.glEnable(2929);
		      GL11.glDisable(2848);
		      GL11.glEnable(3553);
		      GL11.glDisable(3042);
		      GL11.glPopMatrix();
		      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		      if (!GL11.glIsEnabled(2896)) {
		         GL11.glEnable(2896);
		      }

		   }


			public static void drawOutlinedBoundingBox(AxisAlignedBB aa) {
				Tessellator tessellator = Tessellator.getInstance();
				WorldRenderer worldRenderer = tessellator.getWorldRenderer();
				worldRenderer.begin(3, DefaultVertexFormats.field_181703_c);
				worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
				worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
				worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
				worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
				worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
				tessellator.draw();
				worldRenderer.begin(3, DefaultVertexFormats.field_181703_c);
				worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
				worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
				worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
				worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
				worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
				tessellator.draw();
				worldRenderer.begin(1, DefaultVertexFormats.field_181703_c);
				worldRenderer.pos(aa.minX, aa.minY, aa.minZ).endVertex();
				worldRenderer.pos(aa.minX, aa.maxY, aa.minZ).endVertex();
				worldRenderer.pos(aa.maxX, aa.minY, aa.minZ).endVertex();
				worldRenderer.pos(aa.maxX, aa.maxY, aa.minZ).endVertex();
				worldRenderer.pos(aa.maxX, aa.minY, aa.maxZ).endVertex();
				worldRenderer.pos(aa.maxX, aa.maxY, aa.maxZ).endVertex();
				worldRenderer.pos(aa.minX, aa.minY, aa.maxZ).endVertex();
				worldRenderer.pos(aa.minX, aa.maxY, aa.maxZ).endVertex();
				tessellator.draw();
			}

		   public static void drawLines(AxisAlignedBB boundingBox) {
			      GL11.glPushMatrix();
			      GL11.glBegin(2);
			      GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
			      GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
			      GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
			      GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
			      GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
			      GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
			      GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
			      GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
			      GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
			      GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
			      GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
			      GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
			      GL11.glEnd();
			      GL11.glPopMatrix();
			}

		    public static void pre3D() {
		    	GL11.glPushMatrix();
	            GlStateManager.disableAlpha();
	            GlStateManager.enableBlend();
	            GlStateManager.blendFunc(770, 771);
	            GlStateManager.disableTexture2D();
	            GlStateManager.disableDepth();
	            GL11.glEnable(2848);
		    }

		    public static void post3D() {
	            GL11.glDisable(2848);
	            GlStateManager.enableDepth();
	            GlStateManager.enableTexture2D();
	            GlStateManager.enableBlend();
	            GlStateManager.enableAlpha();
	            GL11.glPopMatrix();
		    }


			public static void drawImage(ResourceLocation image, int x, int y, int width, int height) {
				drawImage(image, x, y, width, height, 1.0f);
			}

			public static void drawImage(ResourceLocation image, int x, int y, int width, int height, float alpha) {
				ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
				GL11.glDisable((int) 2929);
				GL11.glEnable((int) 3042);
				GL11.glDepthMask((boolean) false);
				OpenGlHelper.glBlendFunc((int) 770, (int) 771, (int) 1, (int) 0);
				GL11.glColor4f((float) 1.0f, (float) 1.0f, (float) 1.0f, alpha);
				Minecraft.getMinecraft().getTextureManager().bindTexture(image);
				Gui.drawModalRectWithCustomSizedTexture((int) x, (int) y, (float) 0.0f, (float) 0.0f, (int) width, (int) height,
						(float) width, (float) height);
				GL11.glDepthMask((boolean) true);
				GL11.glDisable((int) 3042);
				GL11.glEnable((int) 2929);
			}

		    public static void startSmooth() {
		        GL11.glEnable(2848);
		        GL11.glEnable(2881);
		        GL11.glEnable(2832);
		        GL11.glEnable(3042);
		        GL11.glBlendFunc(770, 771);
		        GL11.glHint(3154, 4354);
		        GL11.glHint(3155, 4354);
		        GL11.glHint(3153, 4354);
		    }

		    public static void endSmooth() {
		        GL11.glDisable(2848);
		        GL11.glDisable(2881);
		        GL11.glEnable(2832);
		    }
		    
		    public static void drawCheckMark(float x, float y, int width, int color) {
		        float f = (color >> 24 & 255) / 255.0f;
		        float f1 = (color >> 16 & 255) / 255.0f;
		        float f2 = (color >> 8 & 255) / 255.0f;
		        float f3 = (color & 255) / 255.0f;
		        GL11.glPushMatrix();
		        glEnable(GL_BLEND);
		        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		        glDisable(3553);
		        glEnable(2848);
		        glBlendFunc(770, 771);
		        GL11.glLineWidth(1.5f);
		        GL11.glBegin(3);
		        GL11.glColor4f(f1, f2, f3, f);
		        GL11.glVertex2d(x + width - 6.5, y + 3);
		        GL11.glVertex2d(x + width - 11.5, y + 10);
		        GL11.glVertex2d(x + width - 13.5, y + 8);
		        GL11.glEnd();
		        glEnable(3553);
		        glDisable(GL_BLEND);
		        GL11.glPopMatrix();
		        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		    }

		    public static void drawCircle(double d, double e, float r, int c) {
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
		            GL11.glVertex2d(d + r / 2 + x2, e + r / 2 + y2);
		        }
		        GL11.glEnd();
		        GL11.glBegin(GL11.GL_LINE_LOOP);
		        for (int i = 0; i <= 360; ++i) {
		            double x2 = Math.sin(i * Math.PI / 180.0) * ((r / 2));
		            double y2 = Math.cos(i * Math.PI / 180.0) * ((r / 2));
		            GL11.glVertex2d(d + ((r / 2)) + x2, e + ((r / 2)) + y2);
		        }
		        GL11.glEnd();
		        GL11.glDisable(2848);
		        GlStateManager.enableTexture2D();
		        GlStateManager.disableBlend();
		        GL11.glPopMatrix();
		    }
		    
		    public static int drawHealth(final EntityLivingBase entityLivingBase) {
		        final float health = entityLivingBase.getHealth();
		        final float maxHealth = entityLivingBase.getMaxHealth();
		        return Color.HSBtoRGB(Math.max(0.0f, Math.min(health, maxHealth) / maxHealth) / 3.0f, 1.0f, 1.0f) | 0xFF000000;
		    }

		    public static Color drawHealth(float health, float maxHealth) {
		        float[] fractions = new float[]{0.0F, 0.5F, 1.0F};
		        Color[] colors = new Color[]{new Color(108, 0, 0), new Color(255, 51, 0), Color.GREEN};
		        float progress = health / maxHealth;
		        return blendColors(fractions, colors, progress).brighter();
		     }
		    
		    public static Color getGradientOffset(Color color1, Color color2, double offset) {
		        if (offset > 1) {
		            double left = offset % 1;
		            int off = (int) offset;
		            offset = off % 2 == 0 ? left : 1 - left;

		        }
		        double inverse_percent = 1 - offset;
		        int redPart = (int) (color1.getRed() * inverse_percent + color2.getRed() * offset);
		        int greenPart = (int) (color1.getGreen() * inverse_percent + color2.getGreen() * offset);
		        int bluePart = (int) (color1.getBlue() * inverse_percent + color2.getBlue() * offset);
		        return new Color(redPart, greenPart, bluePart);
		    }
		    
		    public static Color fade(Color color) {
		        return fade(color, 2, 100);
		    }

		    public static Color fade(Color color, int index, int count) {
		        float[] hsb = new float[3];
		        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
		        float brightness = Math.abs(((float) (System.currentTimeMillis() % 2000L) / 1000.0F + (float) index / (float) count * 2.0F) % 2.0F - 1.0F);
		        brightness = 0.5F + 0.5F * brightness;
		        hsb[2] = brightness % 2.0F;
		        return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
		    }

		    public static Color blendColors(float[] fractions, Color[] colors, float progress) {
		        if (fractions.length == colors.length) {
		           int[] indices = getFractionIndices(fractions, progress);
		           float[] range = new float[]{fractions[indices[0]], fractions[indices[1]]};
		           Color[] colorRange = new Color[]{colors[indices[0]], colors[indices[1]]};
		           float max = range[1] - range[0];
		           float value = progress - range[0];
		           float weight = value / max;
		           Color color = blend(colorRange[0], colorRange[1], (double)(1.0F - weight));
		           return color;
		        } else {
		           throw new IllegalArgumentException("Fractions and colours must have equal number of elements");
		        }
		    }

		    public static int[] getFractionIndices(float[] fractions, float progress) {
		        int[] range = new int[2];

		        int startPoint;
		        for(startPoint = 0; startPoint < fractions.length && fractions[startPoint] <= progress; ++startPoint) {
		        }

		        if (startPoint >= fractions.length) {
		           startPoint = fractions.length - 1;
		        }

		        range[0] = startPoint - 1;
		        range[1] = startPoint;
		        return range;
		     }

			public static Color blend(Color color1, Color color2, double ratio) {
			      float r = (float)ratio;
			      float ir = 1.0F - r;
			      float[] rgb1 = color1.getColorComponents(new float[3]);
			      float[] rgb2 = color2.getColorComponents(new float[3]);
			      float red = rgb1[0] * r + rgb2[0] * ir;
			      float green = rgb1[1] * r + rgb2[1] * ir;
			      float blue = rgb1[2] * r + rgb2[2] * ir;
			      if (red < 0.0F) {
			         red = 0.0F;
			      } else if (red > 255.0F) {
			         red = 255.0F;
			      }

			      if (green < 0.0F) {
			         green = 0.0F;
			      } else if (green > 255.0F) {
			         green = 255.0F;
			      }

			      if (blue < 0.0F) {
			         blue = 0.0F;
			      } else if (blue > 255.0F) {
			         blue = 255.0F;
			      }

			      Color color3 = null;

			      try {
			         color3 = new Color(red, green, blue);
			      } catch (IllegalArgumentException var13) {
			      }

			      return color3;
			   }

			public static void drawEntityESP(double x, double y, double z, double width, double height, float red, float green,
					float blue, float alpha, float lineRed, float lineGreen, float lineBlue, float lineAlpha, float lineWdith) {
				GL11.glPushMatrix();
				GL11.glEnable((int) 3042);
				GL11.glBlendFunc((int) 770, (int) 771);
				GL11.glDisable((int) 3553);
				GL11.glEnable((int) 2848);
				GL11.glDisable((int) 2929);
				GL11.glDepthMask((boolean) false);
				GL11.glColor4f((float) red, (float) green, (float) blue, (float) alpha);
				RenderUtil.drawBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
				GL11.glLineWidth((float) lineWdith);
				GL11.glColor4f((float) lineRed, (float) lineGreen, (float) lineBlue, (float) lineAlpha);
				RenderUtil.drawOutlinedBoundingBox(new AxisAlignedBB(x - width, y, z - width, x + width, y + height, z + width));
				GL11.glDisable((int) 2848);
				GL11.glEnable((int) 3553);
				GL11.glEnable((int) 2929);
				GL11.glDepthMask((boolean) true);
				GL11.glDisable((int) 3042);
				GL11.glPopMatrix();
			}

		    public static void startBlending() {
		        GL11.glEnable(GL11.GL_BLEND);
		        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		    }

		    public static void endBlending() {
		        GL11.glDisable(GL11.GL_BLEND);
		    }

		    public static float[] getRGBAs(int rgb) {
		        return new float[]{((rgb >> 16) & 255) / 255F, ((rgb >> 8) & 255) / 255F, (rgb & 255) / 255F,
		                ((rgb >> 24) & 255) / 255F};
		    }

		    public static double animate(double target, double current, double speed) {
		        boolean larger = target > current;
		        if (speed < 0.0D) {
		            speed = 0.0D;
		        } else if (speed > 1.0D) {
		            speed = 1.0D;
		        }

		        double dif = Math.max(target, current) - Math.min(target, current);
		        double factor = dif * speed;
		        if (factor < 0.1D) {
		            factor = 0.1D;
		        }

		        if (larger) {
		            current += factor;
		        } else {
		            current -= factor;
		        }

		        return current;
		    }
}