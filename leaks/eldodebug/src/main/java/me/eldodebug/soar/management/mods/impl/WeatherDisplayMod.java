package me.eldodebug.soar.management.mods.impl;

import me.eldodebug.soar.management.events.EventTarget;
import me.eldodebug.soar.management.events.impl.EventRender2D;
import me.eldodebug.soar.management.events.impl.EventRenderShadow;
import me.eldodebug.soar.management.mods.HudMod;

public class WeatherDisplayMod extends HudMod{

	public WeatherDisplayMod() {
		super("Weather Display", "Display current weather");
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
		
		String weather = "Clear";
		
		if(mc.theWorld.isRaining()) {
			weather = "Raining";
		}
		
		if(mc.theWorld.isThundering()) {
			weather = "Thundering";
		}
		
		return "Weather: " + weather;
	}
}
