package me.eldodebug.soar.management.mods.impl;

import me.eldodebug.soar.management.mods.Mod;
import me.eldodebug.soar.management.mods.ModCategory;

public class FPSBoostMod extends Mod{

	public FPSBoostMod() {
		super("FPS Boost", "Boost your Minecraft framerate", ModCategory.OTHER);
	}

	@Override
	public void setup() {
		this.addBooleanSetting("Batch Rendering", this, false);
		this.addBooleanSetting("Chunk Delay", this, false);
		this.addSliderSetting("Delay", this, 2, 1, 5, true);
		this.addBooleanSetting("Disable Armor Stand", this, false);
		this.addBooleanSetting("Disable Invisible Player", this, true);
		this.addBooleanSetting("Disable Skulls", this, false);
		this.addBooleanSetting("Low Animation Tick", this, true);
		this.addBooleanSetting("Limit Particle", this, false);
		this.addBooleanSetting("Remove Font Shadow", this, false);
		this.addBooleanSetting("Remove Block Effects", this, false);
	}
}
