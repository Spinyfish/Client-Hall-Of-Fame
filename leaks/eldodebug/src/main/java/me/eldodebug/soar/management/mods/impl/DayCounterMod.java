package me.eldodebug.soar.management.mods.impl;

import me.eldodebug.soar.management.events.EventTarget;
import me.eldodebug.soar.management.events.impl.EventRender2D;
import me.eldodebug.soar.management.events.impl.EventRenderShadow;
import me.eldodebug.soar.management.mods.HudMod;

public class DayCounterMod extends HudMod{

	public DayCounterMod() {
		super("Day Counter", "Count Minecraft day");
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
		
		if(mc.theWorld != null && mc.theWorld.getWorldInfo() != null) {
			long time = mc.theWorld.getWorldInfo().getWorldTotalTime() / 24000L;
			
			return time + " Day" + (time != 1L ? "s" :"");
		}else {
			return null;
		}
	}
}
