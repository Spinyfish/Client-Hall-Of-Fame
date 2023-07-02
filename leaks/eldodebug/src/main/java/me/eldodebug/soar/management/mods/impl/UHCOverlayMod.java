package me.eldodebug.soar.management.mods.impl;

import me.eldodebug.soar.management.mods.Mod;
import me.eldodebug.soar.management.mods.ModCategory;

public class UHCOverlayMod extends Mod{

	public UHCOverlayMod() {
		super("UHC Overlay", "Make UHC items larger", ModCategory.RENDER);
	}

	@Override
	public void setup() {
		this.addSliderSetting("Gold Ingot Scale", this, 1.5F, 1.0F, 5.0F, false);
		this.addSliderSetting("Gold Nugget Scale", this, 1.5F, 1.0F, 5.0F, false);
		this.addSliderSetting("Gold Ore Scale", this, 1.5F, 1.0F, 5.0F, false);
		this.addSliderSetting("Gold Apple Scale", this, 1.5F, 1.0F, 5.0F, false);
		this.addSliderSetting("Skull Scale", this, 1.5F, 1.0F, 5.0F, false);
	}
}
