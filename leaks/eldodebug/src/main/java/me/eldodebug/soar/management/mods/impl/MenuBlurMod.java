package me.eldodebug.soar.management.mods.impl;

import me.eldodebug.soar.Soar;
import me.eldodebug.soar.management.mods.Mod;
import me.eldodebug.soar.management.mods.ModCategory;
import me.eldodebug.soar.management.settings.Setting;

public class MenuBlurMod extends Mod{

	public MenuBlurMod() {
		super("Menu Blur", "Blur the menu", ModCategory.RENDER);
	}

	@Override
	public void setup() {
		Soar.instance.settingsManager.addSetting(new Setting("Radius", this, 20, 1, 40, true));
	}
}
