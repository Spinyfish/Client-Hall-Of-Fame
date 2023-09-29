package de.hero.clickgui.util;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import de.fanta.utils.FileManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ScreenShotHelper;

public class McOutlineHelper {

	private static final Minecraft mc = Minecraft.getMinecraft();

	public static void start() {
		mc.renderGlobal.getEntityOutlineFramebuffer().bindFramebuffer(false);
		GL11.glColor4d(1, 1, 1, 1);
	}

	public static void stop() {
		mc.renderGlobal.getEntityOutlineFramebuffer().unbindFramebuffer();
		mc.getFramebuffer().bindFramebuffer(false);
	}

	public static void render() {
//		stop();
		//mc.renderGlobal.getEntityOutlineShader().loadShaderGroup(mc.timer.renderPartialTicks);
		stop();
		ScaledResolution sr = ScaledResolution.INSTANCE;
		GlStateManager.enableBlend();
		GlStateManager.enableTexture2D();
		GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 0, 1);
		Framebuffer fb = mc.renderGlobal.getEntityOutlineShader().getFramebufferRaw("final");
		GL11.glColor4d(1, 1, 1, 1);
//		 //fb.framebufferRenderExt(fb.framebufferWidth, fb.framebufferHeight, false);
//		fb.bindFramebufferTexture();
		mc.renderGlobal.getEntityOutlineFramebuffer().bindFramebufferTexture();
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2d(0, 1);
		GL11.glVertex2d(0, 0);
		
		GL11.glTexCoord2d(0, 0);
		GL11.glVertex2d(0, sr.getScaledHeight());
		
		GL11.glTexCoord2d(1, 0);
		GL11.glVertex2d(sr.getScaledWidth(), sr.getScaledHeight());
		
		GL11.glTexCoord2d(1, 1);
		GL11.glVertex2d(sr.getScaledWidth(), 0);
		GL11.glEnd();
	
		GlStateManager.disableBlend();
//		GlStateManager.disableTexture2D();
		
		mc.renderGlobal.getEntityOutlineFramebuffer().framebufferClear();
		stop();
	}
}
