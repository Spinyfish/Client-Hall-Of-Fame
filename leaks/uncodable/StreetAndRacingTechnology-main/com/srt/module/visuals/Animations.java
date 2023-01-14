package com.srt.module.visuals;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import com.srt.events.Event;
import com.srt.events.listeners.EventUpdate;
import com.srt.module.ModuleBase;
import com.srt.settings.settings.ModeSetting;

public class Animations extends ModuleBase {

	public static ModeSetting mode;
	
	public Animations() {
		super("Animations", Keyboard.KEY_NONE, Category.VISUALS);

		ArrayList<String> modes = new ArrayList<>();
		modes.add("Gangsta");
		modes.add("1.7");
		modes.add("Test");
		mode = new ModeSetting("Block Mode", modes);
		addSettings(mode);
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventUpdate) {
			setSuffix(mode.getCurrentValue());
		}
	}
	
	

}
