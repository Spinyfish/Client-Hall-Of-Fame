package com.srt.module.combat;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import com.srt.events.Event;
import com.srt.events.listeners.EventMotion;
import com.srt.events.listeners.EventPacket;
import com.srt.module.ModuleBase;
import com.srt.settings.settings.ModeSetting;

import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

public class Velocity extends ModuleBase {

	public ModeSetting mode;
	public static int hits = 0;
	
	public Velocity() {
		super("Velocity", Keyboard.KEY_NONE, Category.COMBAT);
		setDisplayName("Anti Knockback");
		ArrayList<String> modes = new ArrayList<>();
		modes.add("Cancel");
		modes.add("AGC");
		modes.add("Test");
		this.mode = new ModeSetting("Mode", modes);
		addSettings(mode);
	}
	
	public void onEvent(Event event) {
		this.setSuffix(mode.getCurrentValue());
		if(event instanceof EventPacket) {
			if(mc.thePlayer == null)
				return;
			EventPacket e = (EventPacket)event;
			switch(mode.getCurrentValue()) {
			
				case "Cancel":
					if(e.getPacket() instanceof S12PacketEntityVelocity)
						e.setCancelled(true);
					if(e.getPacket() instanceof S27PacketExplosion)
			            e.setCancelled(true);
					break;
					
				case "Test":
					
					break;
					
				case "AGC":
					if(e.getPacket() instanceof S12PacketEntityVelocity) {
						if(((S12PacketEntityVelocity)e.getPacket()).getEntityID() == mc.thePlayer.getEntityId()) {
							if(hits % 3 != 0) {
								e.setCancelled(true);
							}
							hits++;
						}
					}
					
					break;
					
			}
		}
		
		if(event instanceof EventMotion) {
			if(mc.thePlayer == null)
				return;
			EventMotion e = (EventMotion)event;
			switch(mode.getCurrentValue()) {
				case "Test":
				if(mc.thePlayer.hurtTime > 8) {
					mc.thePlayer.motionX *= 0.61;
					mc.thePlayer.motionZ *= 0.61;
				}
				break;
			}
		}
	}
}
