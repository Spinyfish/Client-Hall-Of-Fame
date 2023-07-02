package me.eldodebug.soar.gui;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL11;

import me.eldodebug.soar.utils.render.RenderUtils;
import me.eldodebug.soar.utils.render.RoundedUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;

public class GuiSplashScreen {

	private static float progress = 0;
	private static float maxProgress = 17;
	private static File dataFile = new File(Minecraft.getMinecraft().mcDataDir, "soar/Config.txt");;
	
	public static void update() {
		if(Minecraft.getMinecraft().gameSettings == null || Minecraft.getMinecraft().getTextureManager() == null) {
			return;
		}
		onRender(Minecraft.getMinecraft().getTextureManager());
	}
	
	public static void onRender(TextureManager textureManagerInstance) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int i = sr.getScaleFactor();
        Framebuffer framebuffer = new Framebuffer(sr.getScaledWidth() * i, sr.getScaledHeight() * i, true);
        framebuffer.bindFramebuffer(false);
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
        
        GlStateManager.resetColor();
        GlStateManager.color(1f, 1f, 1f, 1f);
        
        drawProgress(textureManagerInstance);
        
        framebuffer.unbindFramebuffer();
        framebuffer.framebufferRender(sr.getScaledWidth() * i, sr.getScaledHeight() * i);
        
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(516, 0.1F);
        
        Minecraft.getMinecraft().updateDisplay();
	}
	
	private static void drawProgress(TextureManager textureManagerInstance) {
		if(Minecraft.getMinecraft().gameSettings == null || Minecraft.getMinecraft().getTextureManager() == null) {
			return;
		}

		ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
		int addX = 65;
		int addY = 2;
		int size = 82;
		Color color = getClientColor(1);
		
		RoundedUtils.drawRound(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), 0, getBackgroundColor(1));
		
		GlStateManager.enableBlend();
		textureManagerInstance.bindTexture(new ResourceLocation("soar/icon.png"));

		GlStateManager.color(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, 1F);
		RenderUtils.drawScaledCustomSizeModalRect((sr.getScaledWidth() / 2) - (size / 2), (sr.getScaledHeight() / 2) - (size / 2) - 25, size, size, size, size, size, size, size, size);
		GlStateManager.disableBlend();
		
		RoundedUtils.drawRound(sr.getScaledWidth() / 2 - addX, sr.getScaledHeight() / 2 - addY + 30, addX * 2, addY * 2, 2, getBackgroundColor(4));
		RoundedUtils.drawGradientRoundLR(sr.getScaledWidth() / 2 - addX, sr.getScaledHeight() / 2 - addY + 30, (progress / maxProgress) * (addX * 2), addY * 2, 2, getClientColor(1), getClientColor(2));
	}
	
	public static void setProgress(int newProgress) {
		progress = newProgress;
		update();
	}
	
	private static Color getClientColor(int id) {
		
		int red = 0;
		int green = 0;
		int blue = 0;
		
		int red2 = 0;
		int green2 = 0;
		int blue2 = 0;
		
		if(!dataFile.exists() || dataFile.length() == 0) {
			if(id == 1) {
				return new Color(100, 234, 190);
			}else {
				return new Color(254, 250, 163);
			}
		}
		
		ArrayList<String> lines = new ArrayList<String>();
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(dataFile));
			String line = reader.readLine();
			while (line != null) {
				lines.add(line);
				line = reader.readLine();
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (String s : lines) {
			String[] args = s.split(":");
			
			if (s.toLowerCase().startsWith("color:")) {
				if(StringUtils.isNumeric(args[1]) || StringUtils.isNumeric(args[2]) || StringUtils.isNumeric(args[3])) {
					red = Integer.parseInt(args[1]);
					green = Integer.parseInt(args[2]);
					blue = Integer.parseInt(args[3]);
				}
			}
			
			if (s.toLowerCase().startsWith("color2:")) {
				if(StringUtils.isNumeric(args[1]) || StringUtils.isNumeric(args[2]) || StringUtils.isNumeric(args[3])) {
					red2 = Integer.parseInt(args[1]);
					green2 = Integer.parseInt(args[2]);
					blue2 = Integer.parseInt(args[3]);
				}
			}
		}
		
		if(id == 1) {
			return new Color(red, green, blue);
		}else {
			return new Color(red2, green2, blue2);
		}
	}
	
	private static Color getBackgroundColor(int id) {
		Color color = new Color(232, 234, 240);
		boolean dark = false;
		
		if(!dataFile.exists() || dataFile.length() == 0) {
			if(id ==1) {
				return new Color(232, 234, 240);
			}
			if(id == 2) {
				return new Color(239, 244, 249);
			}
			if(id == 3) {
				return new Color(247, 250, 252);
			}
			if(id == 4) {
				return new Color(253, 254, 254);
			}
		}
		
		if(dataFile.exists()) {
			ArrayList<String> lines = new ArrayList<String>();
			
			try {
				BufferedReader reader = new BufferedReader(new FileReader(dataFile));
				String line = reader.readLine();
				while (line != null) {
					lines.add(line);
					line = reader.readLine();
				}
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			for (String s : lines) {
				if(s.contains("SET:DarkMode:Client:true")) {
					dark = true;
				}
			}
			
			switch(id) {
				case 1:
					if(dark) {
						color = new Color(26, 33, 42);
					}else {
						color = new Color(232, 234, 240);
					}
					break;
				case 2:
					if(dark) {
						color = new Color(35, 40, 46);
					}else {
						color = new Color(239, 244, 249);
					}
					break;
				case 3:
					if(dark) {
						color = new Color(46, 51, 57);
					}else {
						color = new Color(247, 250, 252);
					}
					break;
				case 4:
					if(dark) {
						color = new Color(57, 61, 67);
					}else {
						color = new Color(253, 254, 254);
					}
					break;
			}
		}

		return color;
	}
}
