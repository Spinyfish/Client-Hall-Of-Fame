package com.srt.module.visuals;

import java.awt.Color;
import java.util.ArrayList;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.srt.events.Event;
import com.srt.events.listeners.EventRender2D;
import com.srt.module.ModuleBase;
import com.srt.module.combat.Killaura;
import com.srt.settings.settings.ModeSetting;
import com.thunderware.utils.RenderUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import javax.vecmath.Vector3d;

public class TargetHUD extends ModuleBase {
	public ModeSetting mode;
	
	private int x;
	private int y;

	public TargetHUD() {
		super("TargetHUD", 0, Category.VISUALS);
		setDisplayName("Target HUD");
		x = 200;
		y = 300;
		ArrayList<String> modes = new ArrayList<>();
		modes.add("TW");
		modes.add("Simple");
		modes.add("Sernant");
		this.mode = new ModeSetting("Mode", modes);
		addSettings(mode);
	}
	
	public void onEvent(Event e) {
		setSuffix(mode.getCurrentValue());
		if(e instanceof EventRender2D) {
			if(Killaura.currentTarget != null) {
				EntityLivingBase target = (EntityLivingBase) Killaura.currentTarget;
				String targetName = target.getName();
				if(mode.getCurrentValue().equalsIgnoreCase("TW")) {
					int sizeX = 140;
					int sizeY = 50;
					GuiInventory.drawEntityOnScreen(x + 18, y + 40, 20, -mc.thePlayer.rotationYaw, -mc.thePlayer.rotationPitch, (EntityLivingBase) Killaura.currentTarget);
					Gui.drawRect(x, y, x + sizeX, y + sizeY, new Color(40,40,40).getRGB());
					Gui.drawRect(x - 0.5, y - 0.5, x + sizeX + 0.5, y + sizeY + 0.5, new Color(40,40,40,128).getRGB());
					mc.fontRendererObj.drawString(Killaura.currentTarget.getName(), x + 40, y + 8, -1);
					Gui.drawRect(x, y, x + sizeX, y + sizeY, new Color(40,40,40).getRGB());
					Gui.drawRect(x - 1, y - 1, x + sizeX + 1, y + sizeY + 1, new Color(40,40,40,128).getRGB());
					mc.fontRendererObj.drawString(Killaura.currentTarget.getName(), x + 40, y + 8, -1);
					Gui.drawRect(x + 40, y + 22, x + 40 + 96, y + 32, new Color(21,21,21).getRGB());
					Gui.drawRect(x + 40, y + 22, x + 40 + ((target.getHealth()/target.getMaxHealth()) * 96), y + 32, Hud.getColorInt(4));
					String gg = String.valueOf(Math.floor((target.getHealth()/target.getMaxHealth())*100));
					gg += "%";
					mc.fontRendererObj.drawString(gg, x + 42, y + 23, Color.WHITE.getRGB());
				} else if(mode.getCurrentValue().equalsIgnoreCase("Sernant")) {
					float healthBar_x = x + 5 + (target.getHealth() * 5);
					RenderUtils.renderRoundedQuad(new Vector3d(x, y, 0), new Vector3d(x + targetName.length() + (healthBar_x / 22) + 124, y + 46, 0), 5, new Color(0, 0, 0, 120).getRGB());
					GuiInventory.drawEntityOnScreen(x + 18, y + 33, 16, -target.rotationYaw, -target.rotationPitch + 10, target);
					mc.fontRendererObj.drawStringWithShadow(ChatFormatting.DARK_PURPLE + targetName, x + 35, (float) y + 5, 0xffffffff);
					mc.fontRendererObj.drawStringWithShadow(ChatFormatting.DARK_PURPLE + "DIST: " + (String.format("%.2f", 0.0)), x + 35, y + 22, 0xffffffff);
					mc.fontRendererObj.drawStringWithShadow(ChatFormatting.DARK_PURPLE + "HURT: " + target.hurtTime, x + 84, y + 22, 0xffffffff);
					mc.fontRendererObj.drawStringWithShadow(ChatFormatting.DARK_PURPLE + "" + String.format("%.2f", target.getHealth()), healthBar_x + 5, y + 36, 0xffffffff);
					RenderUtils.renderRoundedQuad(new Vector3d(x + 5, y + 41, 0), new Vector3d(healthBar_x, y + 41, 0), 3, new Color(255, 0, 255).getRGB());
				} else if(mode.getCurrentValue().equalsIgnoreCase("Simple")) {
					int sizeX = 200;
					int sizeY = 14;
					int r = 255;
					int g = 255;
					int b = 255;
					if(target.getHealth() > 15) {
						r = b = 0;
						g = 255;
					}else if(target.getHealth() <= 7.5F) {
						r = 255;
						b = 0;
						g = 0;
					}else {
						r = 255;
						b = 0;
						g = 255;
					}
					Gui.drawRect(x, y, x + sizeX, y + sizeY, new Color(r,g,b).getRGB());
					Gui.drawRect(x - 2, y + sizeY + 2, x + sizeX + 2, y + sizeY, new Color(0,0,0).getRGB());
					Gui.drawRect(x - 2, y + 2, x + sizeX + 2, y, new Color(0,0,0).getRGB());
					Gui.drawRect(x - 2, y + sizeY, x, y, new Color(0,0,0).getRGB());
					Gui.drawRect(x + sizeX + 2, y + sizeY, x + sizeX, y, new Color(0,0,0).getRGB());
					mc.fontRendererObj.drawString(Killaura.currentTarget.getName(), x + 4, y + 3, 0);
					String gg = String.valueOf(Math.floor((target.getHealth()/target.getMaxHealth())*100));
					gg += "%";
					mc.fontRendererObj.drawString(gg, x + sizeX - 4 - mc.fontRendererObj.getStringWidth(gg), y + 3, 0);
				}
			}
		}
	}
	
	private void drawHead(ResourceLocation skin, int width, int height) {
		
    }

}
