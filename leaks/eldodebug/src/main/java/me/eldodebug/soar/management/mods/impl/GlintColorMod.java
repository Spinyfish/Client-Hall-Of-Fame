package me.eldodebug.soar.management.mods.impl;

import java.awt.Color;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.mods.Mod;
import me.eldodebug.soar.management.mods.ModCategory;
import me.eldodebug.soar.management.settings.Setting;
import me.eldodebug.soar.utils.color.ColorUtils;

public class GlintColorMod extends Mod{

	public static GlintColorMod instance = new GlintColorMod();
	
	public GlintColorMod() {
		super("Glint Color", "Customize Glint Color", ModCategory.RENDER);
	}

	@Override
	public void setup() {
		Soar.instance.settingsManager.addSetting(new Setting("Rainbow", this, false));
	}
	
	public Color getColor() {
		if(Soar.instance.settingsManager.getSettingByClass(GlintColorMod.class, "Rainbow").getValBoolean()) {
			return ColorUtils.rainbow(0, 25, 255);
		}
		
		return ColorUtils.getClientColor(0);
	}
}
