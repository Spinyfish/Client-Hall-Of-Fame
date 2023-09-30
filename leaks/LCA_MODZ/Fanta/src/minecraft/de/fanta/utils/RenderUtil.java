package de.fanta.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.EXTPackedDepthStencil;
import org.lwjgl.opengl.GL11;

import de.fanta.gui.font.BasicFontRenderer;

import java.awt.*;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.*;

public class RenderUtil {
	
	public static void glColor(int hex) {
		float alpha = (float) (hex >> 24 & 255) / 255F;
		float red = (float) (hex >> 16 & 255) / 255F;
		float green = (float) (hex >> 8 & 255) / 255F;
		float blue = (float) (hex & 255) / 255F;
		GL11.glColor4f(red, green, blue, alpha);
	}

    public static void drawGradientRect(final double x, final double y, final double width, final double height, final Color startColor, final Color endColor) {
        Gui.drawGradientRect(x, y, x + width, y + height, startColor.getRGB(), endColor.getRGB());
    }

    public static void drawGradientRect(final double x, final double y, final double width, final double height, final int startColor, final int endColor) {
        Gui.drawGradientRect(x, y, x + width, y + height, startColor, endColor);
    }
    
    public static void prepareScissorBox(final double x, final double y, final double width, final double height) {
        int factor = getScaledResolution().getScaleFactor();
        GL11.glScissor((int) (x * (float) factor), (int) (((float) getScaledResolution().getScaledHeight() - (y + height)) * (float) factor), (int) (((x + width) - x) * (float) factor), (int) (((y + height) - y) * (float) factor));
    }

    public static void prepareScissorBox(final float x, final float y, final float width, final float height) {
        prepareScissorBox((double) x,(double) y,(double) width,(double) height);
    }
    
    public static void drawRect(final double x, final double y, final double width, final double height, final Color color) {
        Gui.drawRect2(x, y, x + width, y + height, color.getRGB());
    }

    public static void drawRect(final double x, final double y, final double width, final double height, final int color) {
        Gui.drawRect2(x, y, x + width, y + height, color);
    }
    
    public static ScaledResolution getScaledResolution() {
    	return new ScaledResolution(Minecraft.getMinecraft());
    }
	
	public static double interpolate(double current, double old, double scale) {
		return old + (current - old) * scale;
	}

	public static void rectangle(double left, double top, double right, double bottom, int color) {
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

		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		glColor(color);
		worldrenderer.begin(7, DefaultVertexFormats.POSITION);
		worldrenderer.pos(left, bottom, 0.0D).endVertex();
		worldrenderer.pos(right, bottom, 0.0D).endVertex();
		worldrenderer.pos(right, top, 0.0D).endVertex();
		worldrenderer.pos(left, top, 0.0D).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
		GlStateManager.color(1, 1, 1, 1);
	}
	

	public static void rectangle2(double left, double top, double right, double bottom, Color color) {
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

		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

		worldrenderer.begin(7, DefaultVertexFormats.POSITION);
		worldrenderer.pos(left, bottom, 0.0D).endVertex();
		worldrenderer.pos(right, bottom, 0.0D).endVertex();
		worldrenderer.pos(right, top, 0.0D).endVertex();
		worldrenderer.pos(left, top, 0.0D).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
		GlStateManager.color(1, 1, 1, 1);
	}

	public static void renderTag(float left, float top, float right, float bottom, int color) {
		if (left < right) {
			float i = left;
			left = right;
			right = i;
		}

		if (top < bottom) {
			float j = top;
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

	public static void scissorBox(float x, float y, float width, float length) {
		ScaledResolution scale = new ScaledResolution(Minecraft.getMinecraft());
		int scaleFactor = scale.getScaleFactor();
		GL11.glScissor((int) (x * scaleFactor), (int) ((scale.getScaledHeight() - length) * scaleFactor),
				(int) ((width - x) * scaleFactor), (int) ((length - y) * scaleFactor));
	}

	public static void drawCircle(double x, double y, double radius, int color) {
		glDisable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glBegin(GL_POLYGON);
		glColor(color);
		for (int i = 0; i <= 360; i++) {
			double x2 = Math.sin(((i * Math.PI) / 180)) * radius;
			double y2 = Math.cos(((i * Math.PI) / 180)) * radius;
			glVertex2d(x + x2, y + y2);
		}
		glEnd();
		glEnable(GL_TEXTURE_2D);
		glDisable(GL_BLEND);
	}

	public static void drawTriangleFilled(float x, float y, float width, float height, int color) {
		glDisable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glEnable(GL_LINE_SMOOTH);

		glLineWidth(1);
		glColor(color);

		glBegin(GL_POLYGON);
		glVertex2d(x, y);
		glVertex2d(x + width, y + height);
		glVertex2d(x + width, y + height);
		glVertex2d(x + width * 2, y);
		glVertex2d(x + width * 2, y);
		glVertex2d(x, y);
		glEnd();

		glDisable(GL_LINE_SMOOTH);
		glDisable(GL_BLEND);
		glEnable(GL_TEXTURE_2D);
	}

	public static void drawTriangle(float x, float y, float width, float height, int color) {
		glDisable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glEnable(GL_LINE_SMOOTH);

		glLineWidth(1);
		glBegin(GL_LINES);

		glColor(color);

		glVertex2d(x, y);
		glVertex2d(x + width, y + height);

		glEnd();
		glBegin(GL_LINES);

		glVertex2d(x + width, y + height);
		glVertex2d(x + width * 2, y);

		glEnd();
		glBegin(GL_LINES);

		glVertex2d(x + width * 2, y);
		glVertex2d(x, y);

		glEnd();

		glDisable(GL_LINE_SMOOTH);
		glDisable(GL_BLEND);
		glEnable(GL_TEXTURE_2D);
	}

	public static void drawRoundedRect(float x, float y, float width, float height, int roundFactor, int color) {
		GlStateManager.pushMatrix();
		drawCircle(x + height / roundFactor, y + height / roundFactor, height / roundFactor, color);
		drawCircle(x + height / roundFactor, y + height - height / roundFactor, height / roundFactor, color);
		drawCircle(x + width - height / roundFactor, y + height / roundFactor, height / roundFactor, color);
		drawCircle(x + width - height / roundFactor, y + height - height / roundFactor, height / roundFactor, color);
		Gui.drawRect(x, y + height / roundFactor, x + width, y + height - height / roundFactor, color);
		Gui.drawRect(x + height / roundFactor, y, x + width - height / roundFactor, y + height, color);
		GlStateManager.popMatrix();
	}

	public static void color(float red, float green, float blue, float alpha) {
		glColor4f(red / 255f, green / 255f, blue / 255f, alpha / 255f);
	}

	public static void color(Color color) {
		color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}
	
	
	public static void color(int argb) {

        float alpha = (argb >> 24 & 255) / 255f;
        float red = (argb >> 16 & 255) / 255f;
        float green = (argb >> 8 & 255) / 255f;
        float blue = (argb & 255) / 255f;

        glColor4f(red, green, blue, alpha);
    }

	public static void drawRoundedRect2(double x, double y, double width, double height, double cornerRadius,
			Color color) {
		glPushMatrix();
		glDisable(GL_TEXTURE_2D);
		glEnable(GL_LINE_SMOOTH);
		glEnable(GL_BLEND);
		GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 0, 1);
		color(color);
		glBegin(GL_POLYGON);
		double cornerX = x + width - cornerRadius;
		double cornerY = y + height - cornerRadius;
		for (int i = 0; i <= 90; i += 30) {
			glVertex2d(cornerX + Math.sin(i * Math.PI / 180.0D) * cornerRadius,
					cornerY + Math.cos(i * Math.PI / 180.0D) * cornerRadius);
		}
		cornerX = x + width - cornerRadius;
		cornerY = y + cornerRadius;
		for (int i = 90; i <= 180; i += 30) {
			glVertex2d(cornerX + Math.sin(i * Math.PI / 180.0D) * cornerRadius,
					cornerY + Math.cos(i * Math.PI / 180.0D) * cornerRadius);
		}
		cornerX = x + cornerRadius;
		cornerY = y + cornerRadius;
		for (int i = 180; i <= 270; i += 30) {
			glVertex2d(cornerX + Math.sin(i * Math.PI / 180.0D) * cornerRadius,
					cornerY + Math.cos(i * Math.PI / 180.0D) * cornerRadius);
		}
		cornerX = x + cornerRadius;
		cornerY = y + height - cornerRadius;
		for (int i = 270; i <= 360; i += 30) {
			glVertex2d(cornerX + Math.sin(i * Math.PI / 180.0D) * cornerRadius,
					cornerY + Math.cos(i * Math.PI / 180.0D) * cornerRadius);
		}
		glEnd();
		color(1, 1, 1, 1);
		glDisable(GL_BLEND);
		glDisable(GL_LINE_SMOOTH);
		glEnable(GL_TEXTURE_2D);
		glPopMatrix();
	}

	public static void draw3dLine(double x, double y, double z, double x1, double y1, double z1, Color color) {
		GlStateManager.pushMatrix();
		if (true) {
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
		glColor(color.getRGB());
		GL11.glBegin(1);
		GL11.glVertex3d(x, y, z);
		GL11.glVertex3d(x1, y1, z1);
		GL11.glEnd();
		if (true) {
			GL11.glDepthMask(true);
			GL11.glEnable(2929);
		}
		GL11.glEnable(3553);
		GL11.glDisable(3042);
		GL11.glEnable(3008);
		GL11.glDisable(2848);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
	}

	public static void draw2dLine(double x, double z, double x1, double z1, Color color) {
		GlStateManager.pushMatrix();
		GL11.glEnable(3042);
		GL11.glDisable(3553);
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(2848);
		GL11.glHint(3154, 4354);
		GL11.glLineWidth(1F);
		glColor(color.getRGB());
		GL11.glBegin(1);
		GL11.glVertex2d(x, z);
		GL11.glVertex2d(x1, z1);
		GL11.glEnd();
		GL11.glEnable(3553);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
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

	public static void renderOne() {
		checkSetupFBO();
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glLineWidth(3);
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glEnable(GL11.GL_STENCIL_TEST);
		GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
		GL11.glClearStencil(0xF);
		GL11.glStencilFunc(GL11.GL_NEVER, 1, 0xF);
		GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
	}

	public static void renderTwo() {
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glStencilFunc(GL11.GL_NEVER, 0, 0xF);
		GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
	}

	public static void renderThree() {
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xF);
		GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
		GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
	}

	public static void renderFour() {
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		glColor(new Color(0, 172, 255).getRGB());
		GL11.glDepthMask(false);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_POLYGON_OFFSET_LINE);
		GL11.glPolygonOffset(1.0F, -2000000F);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
	}

	public static void renderFive() {
		GL11.glEnable(GL11.GL_LINE_SMOOTH);
		GL11.glPolygonOffset(1.0F, 2000000F);
		GL11.glDisable(GL11.GL_POLYGON_OFFSET_LINE);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_STENCIL_TEST);
		GL11.glDisable(GL11.GL_LINE_SMOOTH);
		GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_DONT_CARE);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glPopAttrib();
	}

	public static void checkSetupFBO() {
		// Gets the FBO of Minecraft
		Framebuffer fbo = Minecraft.getMinecraft().getFramebuffer();

		// Check if FBO isn't null
		if (fbo != null) {
			// Checks if screen has been resized or new FBO has been created
			if (fbo.depthBuffer > -1) {
				// Sets up the FBO with depth and stencil extensions (24/8 bit)
				setupFBO(fbo);
				// Reset the ID to prevent multiple FBO's
				fbo.depthBuffer = -1;
			}
		}
	}

	/**
	 * Sets up the FBO with depth and stencil
	 *
	 * @param fbo Framebuffer
	 */
	public static void setupFBO(Framebuffer fbo) {
		// Deletes old render buffer extensions such as depth
		// Args: Render Buffer ID
		EXTFramebufferObject.glDeleteRenderbuffersEXT(fbo.depthBuffer);
		// Generates a new render buffer ID for the depth and stencil extension
		int stencil_depth_buffer_ID = EXTFramebufferObject.glGenRenderbuffersEXT();
		// Binds new render buffer by ID
		// Args: Target (GL_RENDERBUFFER_EXT), ID
		EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, stencil_depth_buffer_ID);
		// Adds the depth and stencil extension
		// Args: Target (GL_RENDERBUFFER_EXT), Extension (GL_DEPTH_STENCIL_EXT),
		// Width, Height
		EXTFramebufferObject.glRenderbufferStorageEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT,
				EXTPackedDepthStencil.GL_DEPTH_STENCIL_EXT, Minecraft.getMinecraft().displayWidth,
				Minecraft.getMinecraft().displayHeight);
		// Adds the stencil attachment
		// Args: Target (GL_FRAMEBUFFER_EXT), Attachment
		// (GL_STENCIL_ATTACHMENT_EXT), Target (GL_RENDERBUFFER_EXT), ID
		EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
				EXTFramebufferObject.GL_STENCIL_ATTACHMENT_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT,
				stencil_depth_buffer_ID);
		// Adds the depth attachment
		// Args: Target (GL_FRAMEBUFFER_EXT), Attachment
		// (GL_DEPTH_ATTACHMENT_EXT), Target (GL_RENDERBUFFER_EXT), ID
		EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
				EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT,
				stencil_depth_buffer_ID);
	}
	public static Color injectAlpha(int color, int alpha){
        Color temp = new Color(color);
        return new Color(temp.getRed(), temp.getGreen(), temp.getBlue(), MathHelper.clamp_int((int) alpha, 0,255));
    }
	

}