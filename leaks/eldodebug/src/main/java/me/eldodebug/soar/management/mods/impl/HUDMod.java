package me.eldodebug.soar.management.mods.impl;

import java.util.ArrayList;

import me.eldodebug.soar.management.mods.Mod;
import me.eldodebug.soar.management.mods.ModCategory;
import me.eldodebug.soar.utils.DayEventUtils;

public class HUDMod extends Mod{

	public HUDMod() {
		super("HUD", "Customize HUD", ModCategory.RENDER);
	}

	@Override
	public void setup() {
		
		ArrayList<String> options = new ArrayList<String>();
		
		options.add("Glass1");
		options.add("Glass2");
		options.add("Color");
		options.add("Rainbow");
		options.add("Clear1");
		options.add("Clear2");
		options.add("Theme1");
		options.add("Theme2");
		options.add("Outline1");
		options.add("Outline2");
		options.add("Outline3");
		options.add("Outline4");
		options.add("Classic");
		
		if(DayEventUtils.isChristmas()) {
			options.add("Christmas");
		}
		
		if(DayEventUtils.isHalloween()) {
			options.add("Halloween");
		}
		
		this.addModeSetting("Design", this, "Color", options);
		this.addBooleanSetting("Blur", this, false);
		this.addBooleanSetting("Background", this, true);
		this.addBooleanSetting("Shadow", this, true);
		this.addSliderSetting("Radius", this, 6, 0, 10, true);
		this.addSliderSetting("Opacity", this, 0.8, 0.1, 1.0, false);
		this.addSliderSetting("Blur Radius", this, 15, 1, 30, true);
		this.addBooleanSetting("Hide Debug Menu", this, true);
		
		this.setToggled(true);
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
		this.setToggled(true);
	}
}
