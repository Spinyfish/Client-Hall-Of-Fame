package com.srt.module.visuals;

import com.srt.events.Event;
import com.srt.events.listeners.EventRender2D;
import com.srt.module.ModuleBase;
import com.srt.settings.settings.NumberSetting;

import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.world.World;

public class Ambience extends ModuleBase {

	public NumberSetting time = new NumberSetting("Time", 16000, 1, 0, 24000);
	
	public Ambience() {
		super("Ambience", 0, Category.VISUALS);
		addSettings(time);
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventRender2D) {
			WorldClient.setWorldTimeNoEvent(time.getValue().intValue());
		}
	}

}
