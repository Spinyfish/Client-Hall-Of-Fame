package com.srt.module.movement;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import com.srt.events.Event;
import com.srt.events.listeners.EventMotion;
import com.srt.module.ModuleBase;
import com.srt.settings.settings.ModeSetting;

import net.minecraft.network.play.client.C03PacketPlayer;

public class Phase extends ModuleBase {
	
	public Phase() {
		super("Phase", Keyboard.KEY_L, Category.PLAYER);
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventMotion) {
			mc.thePlayer.setPosition(mc.thePlayer.posX,mc.thePlayer.posY-4,mc.thePlayer.posZ);
			this.toggle();
		}
	}
}
