package me.eldodebug.soar.management.mods.impl;

import java.awt.Color;
import java.util.ArrayList;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.events.EventTarget;
import me.eldodebug.soar.management.events.impl.EventRenderShadow;
import me.eldodebug.soar.management.mods.Mod;
import me.eldodebug.soar.management.mods.ModCategory;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

public class HotbarMod extends Mod{

	public HotbarMod() {
		super("Hotbar", "Customize your hotbar", ModCategory.RENDER);
	}

	@Override
	public void setup() {
		
		ArrayList<String> options = new ArrayList<String>();
		
		options.add("Vanilla");
		options.add("Chill");
		options.add("Clear");
		
		this.addBooleanSetting("Animation", this, true);
		this.addSliderSetting("Speed", this, 17, 2, 25, true);
		this.addModeSetting("Design", this, "Vanilla", options);
	}
	
	@EventTarget
	public void onRenderShadow(EventRenderShadow event) {
		String mode = Soar.instance.settingsManager.getSettingByName(this, "Design").getValString();
		ScaledResolution sr = new ScaledResolution(mc);
		
		if(mode.equals("Clear")) {
        	Gui.drawRect(0, sr.getScaledHeight() - 22, sr.getScaledWidth(), sr.getScaledHeight() + 22, new Color(20, 20, 20, 180).getRGB());
		}
	}
}
