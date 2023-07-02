package me.eldodebug.soar.management.mods.impl;

import java.util.ArrayList;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.events.EventTarget;
import me.eldodebug.soar.management.events.impl.EventRenderCrosshair;
import me.eldodebug.soar.management.mods.Mod;
import me.eldodebug.soar.management.mods.ModCategory;
import me.eldodebug.soar.utils.GlUtils;
import me.eldodebug.soar.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class CrosshairMod extends Mod{

	public CrosshairMod() {
		super("Crosshair", "Customize crosshair", ModCategory.RENDER);
	}
	
	@Override
	public void setup() {
		
		ArrayList<String> options = new ArrayList<>();
		
		options.add("1");
		options.add("2");
		options.add("3");
		options.add("4");
		options.add("5");
		
		this.addBooleanSetting("Hide ThirdPersonView", this, false);
		this.addBooleanSetting("Custom Crosshair", this, false);
		this.addModeSetting("Type", this, "1", options);
		this.addSliderSetting("Scale", this, 1, 0.3, 2, false);
	}
	
	@EventTarget
	public void onRenderCrosshair(EventRenderCrosshair event) {
		
		String mode = Soar.instance.settingsManager.getSettingByName(this, "Type").getValString();
		
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        
        if((Soar.instance.settingsManager.getSettingByName(this, "Hide ThirdPersonView").getValBoolean() && mc.gameSettings.thirdPersonView != 0)) {
        	event.setCancelled(true);
        }
        
        if(Soar.instance.settingsManager.getSettingByName(this, "Custom Crosshair").getValBoolean()) {
        	event.setCancelled(true);
        	if((!Soar.instance.settingsManager.getSettingByName(this, "Hide ThirdPersonView").getValBoolean()) || (Soar.instance.settingsManager.getSettingByName(this, "Hide ThirdPersonView").getValBoolean() && mc.gameSettings.thirdPersonView == 0)) {
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(775, 769, 1, 0);
                GlStateManager.enableAlpha();
                GlStateManager.enableDepth();
                
            	mc.getTextureManager().bindTexture(new ResourceLocation("soar/mods/crosshair/type" + mode + ".png"));
            	GlUtils.startScale(sr.getScaledWidth() / 2 - 7, sr.getScaledHeight() / 2 - 7, 16, 16, (float) Soar.instance.settingsManager.getSettingByName(this, "Scale").getValDouble());
            	RenderUtils.drawTexturedModalRect(sr.getScaledWidth() / 2 - 7, sr.getScaledHeight() / 2 - 7, 0, 0, 16, 16);
            	GlUtils.stopScale();
        	}
        }
	}
}