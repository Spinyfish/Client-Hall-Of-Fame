package me.eldodebug.soar.management.mods.impl;

import me.eldodebug.soar.management.events.EventTarget;
import me.eldodebug.soar.management.events.impl.EventRender2D;
import me.eldodebug.soar.management.events.impl.EventRenderShadow;
import me.eldodebug.soar.management.mods.HudMod;

public class MemoryUsageMod extends HudMod{

	public MemoryUsageMod() {
		super("Memory Usage", "Display Minecraft memory usage");
	}

	@EventTarget
	public void onRender2D(EventRender2D event) {
		super.onRender2D();
	}
	
	@EventTarget
	public void onRenderShadow(EventRenderShadow event) {
		super.onRenderShadow();
	}
	
	@Override
	public String getText() {
		
		Runtime runtime = Runtime.getRuntime();
		String mem = "Mem: " + (runtime.totalMemory() - runtime.freeMemory()) * 100L / runtime.maxMemory() + "%";
		
		return mem;
	}
}
