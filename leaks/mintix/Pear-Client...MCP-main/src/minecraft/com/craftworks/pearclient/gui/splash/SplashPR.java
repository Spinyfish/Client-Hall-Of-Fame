package com.craftworks.pearclient.gui.splash;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import com.craftworks.pearclient.util.GLRectUtils;
import com.craftworks.pearclient.util.draw.DrawUtil;
import com.craftworks.pearclient.util.draw.GLDraw;
import com.craftworks.pearclient.util.font.GlyphPageFontRenderer;
import com.craftworks.pearclient.util.unicode.UnicodeFontRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;

public class SplashPR {
	
	private static GlyphPageFontRenderer renderer;
	
	private static final int MAX = 10;
	private static int PROGRESS = 0;
	private static String CURRENT = "";
	private static ResourceLocation splash;
	private static UnicodeFontRenderer ufr;
	
	public static void update() {
		if(Minecraft.getMinecraft() == null || Minecraft.getMinecraft().getLanguageManager() == null) {
			return;	
	}
		drawSplash(Minecraft.getMinecraft().getTextureManager());
	}
	
	public static void setProgress(int givenProgress, String givenText) {
		PROGRESS = givenProgress;
		CURRENT = givenText;
		update();
	}
	
	public static void drawSplash(TextureManager tm) {
		
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		int sf = sr.getScaleFactor();
		
		Framebuffer fb = new Framebuffer(sr.getScaledWidth() * sf, sr.getScaledHeight() * sf, true);
		fb.bindFramebuffer(false);
		
		GlStateManager.matrixMode(GL11.GL_PROJECTION);
		GlStateManager.loadIdentity();
		GlStateManager.ortho(0.0D, (double)sr.getScaledWidth(), (double)sr.getScaledHeight(), 0.0D, 1000.0D, 3000.0D);
		GlStateManager.matrixMode(GL11.GL_MODELVIEW);
		GlStateManager.loadIdentity();
		GlStateManager.translate(0.0F, 0.0F, -2000.0F);
		GlStateManager.disableLighting();
		GlStateManager.disableFog();
		GlStateManager.disableDepth();
		GlStateManager.enableTexture2D();
		
		if(splash == null) {
			splash = new ResourceLocation("pearclient/background/PearBackground.png");
		}
		
		tm.bindTexture(splash);
		GlStateManager.resetColor();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		
		Gui.drawScaledCustomSizeModalRect(0, 0, 0, 0, 1920, 1080, sr.getScaledWidth(), sr.getScaledHeight(), 1920, 1080);

		drawProgress();
		fb.unbindFramebuffer();
		fb.framebufferRender(sr.getScaledWidth() * sf, sr.getScaledHeight() * sf);
		
		renderer = GlyphPageFontRenderer.create("Futura", 100, false, false, false);
		
		GlStateManager.enableAlpha();
		GlStateManager.alphaFunc(516, 0.1F);
		
		Minecraft.getMinecraft().updateDisplay();
		
	}
	
	private static void drawProgress() {
		
		renderer = GlyphPageFontRenderer.create("Arial Rounded MT Bold", 20, true, false, false);
		
		if(Minecraft.getMinecraft().gameSettings == null || Minecraft.getMinecraft().getTextureManager() == null) {
			return;
		}
		
		if(ufr == null) {
			ufr = UnicodeFontRenderer.getFontOnPC("Arial", 20);
		}
		
		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

		double nProgress = (double)PROGRESS;
		double cacl = (nProgress / MAX) * (sr.getScaledWidth());
		
		GlStateManager.resetColor();
		resetTextureState();
		
		renderer.drawString(CURRENT, 20, sr.getScaledHeight() - 170, -1, false);
		
		String step = PROGRESS + "/" + "7";
		renderer.drawString(step, sr.getScaledWidth() - 20 - ufr.getStringWidth(step), sr.getScaledHeight() - 170, -1, false);
		
		GlStateManager.resetColor();
		resetTextureState();
		
		GLDraw.drawRoundedRect(0, sr.getScaledHeight() - 150, sr.getScaledWidth(), sr.getScaledHeight() - 130, 4, new Color(0, 0, 0, 200).getRGB());
		DrawUtil.drawRoundedOutline(0, sr.getScaledHeight() - 150, sr.getScaledWidth(), sr.getScaledHeight() - 130, 4, 0.35f, new Color(0, 0, 0, 0),new Color(60, 232, 118));
		if(PROGRESS != 0) {
			GLDraw.drawRoundedRect(0, sr.getScaledHeight() - 150, (int)cacl, sr.getScaledHeight() - 130, 4, new Color(60, 232, 118, 200).getRGB());
		}
		
	}
	
	private static void resetTextureState() {
		GlStateManager.textureState[GlStateManager.activeTextureUnit].textureName = -1;
	}

}
