package com.srt.module.visuals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.awt.Color;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.srt.SRT;
import com.srt.events.Event;
import com.srt.events.listeners.EventRender2D;
import com.srt.module.ModuleBase;
import com.srt.settings.settings.BooleanSetting;
import com.srt.settings.settings.ColorSetting;
import com.srt.settings.settings.ModeSetting;
import com.thunderware.utils.Mixer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class Hud extends ModuleBase {

	public static ModeSetting color;
	public static ModeSetting rect;
	public static ColorSetting rgb = new ColorSetting("Color",Color.red);
	public static ColorSetting rgb2 = new ColorSetting("Second Color",Color.red);
	public BooleanSetting font = new BooleanSetting("Font",false);
	public BooleanSetting textOnly = new BooleanSetting("Text-Only",false);
	public BooleanSetting bg = new BooleanSetting("Background",true);
	
	public Hud() {
		super("Hud", Keyboard.KEY_NONE, Category.VISUALS);
		setDisplayName("HUD");
		setToggled(true);
		ArrayList<String> colors = new ArrayList<>();
		ArrayList<String> rects = new ArrayList<>();
		colors.add("Astolfo");
		colors.add("Fade");
		colors.add("Static");
		colors.add("Rainbow");
		colors.add("Mixer");
		colors.add("White");
		rects.add("Right");
		rects.add("Border");
		rects.add("Left");
		rects.add("None");
		this.color = new ModeSetting("Color", colors);
		this.rect = new ModeSetting("Rectangle", rects);
		addSettings(color, rect, bg, rgb, rgb2);
	}

	public void onEnable() { }
	/*
	  				
	 */
	public static class sortDefaultFont implements Comparator<ModuleBase> {
		@Override
		public int compare(ModuleBase arg0, ModuleBase arg1) {
				if (Minecraft.getMinecraft().fontRendererObj.getStringWidth(arg0.getDisplayName() + (arg0.getSuffix().length() > 1 ? (" " + arg0.getSuffix()) : "")) > Minecraft.getMinecraft().fontRendererObj.getStringWidth(arg1.getDisplayName() + (arg1.getSuffix().length() > 1 ? (" " + arg1.getSuffix()) : ""))) {
					return -1;
				} else if (Minecraft.getMinecraft().fontRendererObj.getStringWidth(arg0.getDisplayName() + (arg0.getSuffix().length() > 1 ? (" " + arg0.getSuffix()) : "")) < Minecraft.getMinecraft().fontRendererObj.getStringWidth(arg1.getDisplayName() + (arg1.getSuffix().length() > 1 ? (" " + arg1.getSuffix()) : "")))
					return 1;
			return 0;
		}
	}
	
	public static int getColorInt(int offset) {
		switch(color.getCurrentValue()) {
			case "Astolfo":
				return astolfo(offset/2);
			case "Fade":
				return fade(rgb.getValue(), offset * 2, 200).getRGB();
			case "Static":
				return rgb.getValue().getRGB();
			case "Rainbow":
				return Color.HSBtoRGB(getRainbow(4f, offset*8),0.7f,1f);
			case "Mixer":
				return Mixer.getMixedColor(rgb.getValue(), rgb2.getValue(), offset*10, 2).getRGB();
			case "White":
				return new Color(255,255,255).getRGB();
		}
		return rgb.getValue().getRGB();
	}

	public static Color getColor(int offset) {
		switch(color.getCurrentValue()) {
		case "Astolfo":
			return new Color(astolfo(offset/2));
		case "Fade":
			return fade(rgb.getValue(), offset * 2, 200);
		case "Static":
			return rgb.getValue();
		case "Rainbow":
			return new Color(Color.HSBtoRGB(getRainbow(4f, offset*8),1f,1f));
		case "Mixer":
			return Mixer.getMixedColor(rgb.getValue(), rgb2.getValue(), offset*10, 2);
		case "White":
			return new Color(255,255,255);
		}
		return rgb.getValue();
	}

	public static int astolfo(int offset) {
		int i = (int) ((System.currentTimeMillis() / 10 + offset) % 360);
        i = (i > 180 ? 360 - i : i) + 180;
        return Color.HSBtoRGB(i / 360f, 0.5f, 1f);
	}
	
	public static float getRainbow(float seconds, int count) {
		float idk = ((System.currentTimeMillis() + count) % (int)(seconds * 1000)) / (float)(seconds * 1000);
		return idk;
	}
	
	public static final Color fade(Color color, int index, int count) {
		float[] hsb = new float[3];
		Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
		float brightness = Math.abs(((float)(System.currentTimeMillis() % 2000L) / 1000.0F + (float)index / (float)count * 2.0F) % 2.0F - 1.0F);
		brightness = 0.65F + 0.35F * brightness;
		hsb[2] = brightness % 2.0F;
	    return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
	}
	
	public void onEvent(Event event) {
		this.setSuffix(color.getCurrentValue());
		if(event instanceof EventRender2D) {
			EventRender2D e = (EventRender2D)event;
			CopyOnWriteArrayList<ModuleBase> modules = new CopyOnWriteArrayList<ModuleBase>();
			
			for(ModuleBase mod: SRT.moduleManager.getModules()) {
				if(mod.isToggled()) {
					modules.add(mod);
				}
			}
			Collections.sort(modules, new sortDefaultFont());
			int count = 0;
			int totalCount = 0;
			/*
			mc.fontRendererObj.drawStringWithShadow("S\247fRT", 4, 4, getColorInt(5));
			mc.fontRendererObj.drawStringWithShadow("v\247fersion: " + SRT.instance.buildVersion, 4, 14, getColorInt(20));
			mc.fontRendererObj.drawStringWithShadow("fps: \247f" + String.valueOf(mc.getDebugFPS()), 4, 16 + mc.fontRendererObj.FONT_HEIGHT, getColorInt(35));
			mc.fontRendererObj.drawStringWithShadow("BETA", 4, 26 + mc.fontRendererObj.FONT_HEIGHT, getColorInt(50));
			*/
			GL11.glColor4f(255, 255, 255, 255);
	        mc.getTextureManager().bindTexture(new ResourceLocation("srt/srt.png"));
	        GuiScreen.drawModalRectWithCustomSizedTexture(-10, -40, 0, 0, 240, 144, 240, 144);
			double bpt = mc.thePlayer.getDistance(mc.thePlayer.lastTickPosX, mc.thePlayer.posY, mc.thePlayer.lastTickPosZ) * (Minecraft.getMinecraft().timer.ticksPerSecond * Minecraft.getMinecraft().timer.timerSpeed);
			bpt = Math.floor(bpt * 1000) / 1000;
			if(mc.thePlayer != null) {
				mc.fontRendererObj.drawStringWithShadow(String.valueOf(bpt) + " BPS", 4, mc.displayHeight/mc.gameSettings.guiScale - mc.fontRendererObj.FONT_HEIGHT, getColorInt(50));
			}
			for(ModuleBase mod : modules) {
				String text = mod.getDisplayName() + (mod.getSuffix().length() > 0 ? "\2477 " + mod.getSuffix() : "");
				float extraY = 0.5f;
				float x = (float) (e.getWidth() - mc.fontRendererObj.getStringWidth(text) - 5.5);
				float y = (count * (mc.fontRendererObj.FONT_HEIGHT+extraY));
				double endX = e.getWidth();
				double endY = (count * (mc.fontRendererObj.FONT_HEIGHT+extraY)) + mc.fontRendererObj.FONT_HEIGHT+extraY;
				switch(rect.getCurrentValue()) {
					case "Border":{
						Gui.drawRect(x + 1, y, endX, endY, new Color(0,0,0,255).getRGB());
						//Gui.drawGradientRect(endX - 1,y,endX,y + mc.fontRendererObj.FONT_HEIGHT + extraY
								//, getColorInt((int)y), getColorInt((int)endY));
							
						//zaza packet
						Gui.drawGradientRect(x, y, x + 1, endY, getColorInt((int)y), getColorInt((int)endY));
						
						double endXOutline = e.getWidth();
						
						Gui.drawRect(x, endY, endXOutline, endY + 1, getColorInt((int)y));
						break;
					}
					case "Right":{
						if(bg.getValue())
							Gui.drawRect(x + 1, y, endX, endY, new Color(0,0,0,144).getRGB());
						Gui.drawGradientRect(endX - 1,y,endX,y + mc.fontRendererObj.FONT_HEIGHT + extraY
								, getColorInt((int)y), getColorInt((int)endY));
						break;
					}
					case "Left":{
						if(bg.getValue())
							Gui.drawRect(x + 1, y, endX, endY, new Color(0,0,0,144).getRGB());
						Gui.drawGradientRect(x,y,x+1,y + mc.fontRendererObj.FONT_HEIGHT + extraY
								, getColorInt((int)y), getColorInt((int)endY));
						break;
					}
					case "None":{
						if(bg.getValue())
							Gui.drawRect(x + 1, y, endX, endY, new Color(0,0,0,144).getRGB());
						break;
					}
						
				}
				mc.fontRendererObj.drawStringWithShadow(text, x + 2.5F, y + extraY/2, getColorInt((int) y));
				count++;
			}
			
			//Gui.drawRect(0, 0, 10, 10, -1);
		}
	}
}
