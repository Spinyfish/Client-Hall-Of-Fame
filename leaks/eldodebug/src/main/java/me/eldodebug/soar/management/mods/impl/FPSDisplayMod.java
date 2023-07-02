package me.eldodebug.soar.management.mods.impl;

import me.eldodebug.soar.management.events.EventTarget;
import me.eldodebug.soar.management.events.impl.EventRender2D;
import me.eldodebug.soar.management.events.impl.EventRenderShadow;
import me.eldodebug.soar.management.mods.HudMod;
import net.minecraft.client.Minecraft;

public class FPSDisplayMod extends HudMod{

	public FPSDisplayMod() {
		super("FPS Display", "Display your Minecraft FPS");
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
		return Minecraft.getDebugFPS() + " FPS";
	}
}
