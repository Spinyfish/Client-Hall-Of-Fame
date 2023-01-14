package com.srt.module.player;

import org.lwjgl.input.Keyboard;

import com.srt.events.Event;
import com.srt.events.listeners.EventUpdate;
import com.srt.module.ModuleBase;

public class AutoRespawn extends ModuleBase{

	public AutoRespawn() {
		super("AutoRespawn", Keyboard.KEY_NONE, Category.PLAYER);
		setDisplayName("Auto Respawn");
	}
	 
	public void onEvent(Event e) {
		if(e instanceof EventUpdate) {
			if(mc.thePlayer.isDead) {
				mc.thePlayer.respawnPlayer();
			}
			
		}

	}
	
}
