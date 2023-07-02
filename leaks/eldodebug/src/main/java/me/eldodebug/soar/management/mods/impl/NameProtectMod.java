package me.eldodebug.soar.management.mods.impl;

import me.eldodebug.soar.management.events.EventTarget;
import me.eldodebug.soar.management.events.impl.EventText;
import me.eldodebug.soar.management.mods.Mod;
import me.eldodebug.soar.management.mods.ModCategory;

public class NameProtectMod extends Mod{

	public NameProtectMod() {
		super("Name Protect", "Protect your name", ModCategory.RENDER);
	}

	@EventTarget
	public void onText(EventText event) {
		event.replace(mc.getSession().getUsername(), "You");
	}
}
