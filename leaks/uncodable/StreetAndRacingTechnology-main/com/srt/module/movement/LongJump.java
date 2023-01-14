package com.srt.module.movement;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import com.srt.events.Event;
import com.srt.events.EventType;
import com.srt.events.listeners.EventMotion;
import com.srt.events.listeners.EventMove;
import com.srt.events.listeners.EventPacket;
import com.srt.module.ModuleBase;
import com.srt.settings.settings.ModeSetting;
import com.srt.settings.settings.NumberSetting;
import com.thunderware.utils.MovementUtils;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.potion.Potion;

public class LongJump extends ModuleBase {

	public NumberSetting speed = new NumberSetting("Speed", 0.5, 0.05, 0.15, 3);
	public ModeSetting mode;
	public boolean jumped;
	public int ticks;
	public boolean fast;
	
	public LongJump() {
		super("LongJump", Keyboard.KEY_Z, Category.MOVEMENT);
		ArrayList<String> modes = new ArrayList<>();
		modes.add("Vanilla");
		modes.add("Hypixel");
		modes.add("BlocksMC");
		modes.add("AGC");
		mode = new ModeSetting("Mode", modes);
		addSettings(mode, speed);
	}
	
	public void onEnable() {
		fast = false;
		jumped = false;
		if(mode.getCurrentValue() == "AGC") {
			mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,mc.thePlayer.posY-3.0001,mc.thePlayer.posZ,false));
		}
		if(mode.getCurrentValue().contains("Hypixel")) {
				/*
			double x = mc.thePlayer.posX,
	                y = mc.thePlayer.posY,
	                z = mc.thePlayer.posZ;
	        for (int i = 0; i < 65; i++) {
	            mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.0624399212, z, false));
	            mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));
	        }
	        mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, true));
	        */
		}
	}
	
	public void onDisable() {
		mc.timer.timerSpeed = 1.0f;
		mc.gameSettings.keyBindJump.pressed = false;
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventMotion) {
			this.setSuffix(mode.getCurrentValue());
			switch(mode.getCurrentValue()) {
				case "BlocksMC":
					if(mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0) {
						if(mc.thePlayer.onGround) {
							if(jumped == true) {
								this.toggle();
							}else {
								mc.thePlayer.jump();
								mc.thePlayer.motionY += 0.4;
							}
						}else {
							jumped = true;
						}
						mc.thePlayer.setSpeed(speed.getValue());
					}else {
						mc.thePlayer.motionX = 0;
						mc.thePlayer.motionZ = 0;
					}
					break;
				case "Vanilla":
					if(mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0) {
						if(mc.thePlayer.onGround) {
							if(jumped == true) {
								this.toggle();
							}else {
								mc.thePlayer.jump();
							}
						}else {
							jumped = true;
						}
						if(mc.thePlayer.hurtTime != 0) {
							mc.timer.timerSpeed = 1.0f;
						}
						mc.thePlayer.setSpeed(speed.getValue());
					}else {
						mc.thePlayer.motionX = 0;
						mc.thePlayer.motionZ = 0;
					}
					break;
				case "AGC":
					if(mc.thePlayer.hurtTime == 9) {
						mc.thePlayer.jump();
						jumped = true;
						mc.timer.timerSpeed = 0.5f;
						mc.thePlayer.setSpeed(speed.getValue());
					}else {
						mc.timer.timerSpeed = 1.0f;
					}
					if(jumped == true && mc.thePlayer.onGround && mc.thePlayer.hurtTime != 0) {
						mc.timer.timerSpeed = 1.0f;
						this.toggle();
					}
					break;

				case "Hypixel":
					if(e.type != EventType.PRE)
		                return;
		            ticks++;
		            if(mc.thePlayer.onGround && jumped){
		                this.toggle();
		            }
		            if(ticks == 21){
		            	double x = mc.thePlayer.posX,
		    	                y = mc.thePlayer.posY,
		    	                z = mc.thePlayer.posZ;
		    	        for (int i = 0; i < 65; i++) {
		    	            mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.0624399212, z, false));
		    	            mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));
		    	        }
		    	        mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, true));
		                mc.timer.timerSpeed = 0.334f;
		                mc.thePlayer.motionY = 0.67f;
		                MovementUtils.setMotion(0.2783*1.7);
		                jumped = true;
		            }else{
		                if(ticks == 22){
		                    mc.thePlayer.motionY += 0.05;
		                    MovementUtils.setMotion(0.2783*1.55);
		                }
		                if(ticks == 23){
		                	MovementUtils.setMotion(0.2783*1.48);
		                }
		                mc.timer.timerSpeed = 1.0f;
		            }
		            if(jumped == true && mc.thePlayer.fallDistance > 0.1){
		                mc.thePlayer.motionY += 0.015;
		            }
		            break;
			}
		}
		if(e instanceof EventPacket) {
			EventPacket event = (EventPacket)e;
			switch(mode.getCurrentValue()) {
				case "AGC":
					if(event.getPacket() instanceof S12PacketEntityVelocity) {
						S12PacketEntityVelocity op = (S12PacketEntityVelocity)event.getPacket();
						if(op.getEntityID() == mc.thePlayer.getEntityId()) {
							event.setCancelled(true);
						}
					}
					break;
				case "Hypixel":
					if(ticks < 21 && event.getPacket() instanceof C03PacketPlayer){
		                e.setCancelled(true);
		            }
					break;
			}
		}
		if(e instanceof EventMove) {
			if(ticks < 21 && mode.getCurrentValue() == "Hypixel"){
                e.setCancelled(true);
            }
		}
	}
}
