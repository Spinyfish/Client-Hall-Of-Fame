package me.eldodebug.soar.management.mods.impl;

import me.eldodebug.soar.management.mods.Mod;
import me.eldodebug.soar.management.mods.ModCategory;

public class OverlayEditorMod extends Mod{

	public OverlayEditorMod() {
		super("Overlay Editor", "Edit overlay", ModCategory.RENDER);
	}

	@Override
	public void setup() {
		this.addBooleanSetting("Disable Achievements", this, false);
		this.addBooleanSetting("Fire", this, false);
		this.addSliderSetting("Fire Height", this, 0, -0.8, 0.4, false);
		this.addBooleanSetting("Hide Pumpkin", this, false);
	}
}
