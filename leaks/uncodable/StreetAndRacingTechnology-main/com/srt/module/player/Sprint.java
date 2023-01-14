package com.srt.module.player;

import org.lwjgl.input.Keyboard;

import com.srt.SRT;
import com.srt.events.Event;
import com.srt.events.listeners.EventMotion;
import com.srt.module.ModuleBase;

public class Sprint extends ModuleBase {
	
	public Sprint() {
		super("Sprint", Keyboard.KEY_M, Category.PLAYER);
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventMotion) {
			if(mc.thePlayer.moveForward > 0 && !mc.thePlayer.isUsingItem() && !mc.thePlayer.isCollidedHorizontally && mc.thePlayer.getFoodStats().getFoodLevel() > 6 && !mc.thePlayer.isSneaking() && !SRT.moduleManager.getModuleByName("Scaffold").isToggled())
				mc.thePlayer.setSprinting(true);
		}
	}
}
