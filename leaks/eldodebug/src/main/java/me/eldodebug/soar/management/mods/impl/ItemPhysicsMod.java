package me.eldodebug.soar.management.mods.impl;

import me.eldodebug.soar.management.mods.Mod;
import me.eldodebug.soar.management.mods.ModCategory;

public class ItemPhysicsMod extends Mod{

	public ItemPhysicsMod() {
		super("Item Physics", "Add physics to the item", ModCategory.RENDER);
	}

	
	@Override
	public void setup() {
		this.addSliderSetting("Speed", this, 1, 0.5, 4, false);
	}
}
