package igbt.astolfy.module.movement;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import igbt.astolfy.events.Event;
import igbt.astolfy.events.listeners.EventMotion;
import igbt.astolfy.module.ModuleBase;
import igbt.astolfy.settings.settings.ModeSetting;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;

public class LongJump extends ModuleBase {

	public ModeSetting mode;
	public LongJump() {
		super("LongJump", Keyboard.KEY_Z, Category.MOVEMENT);
		ArrayList<String> modes = new ArrayList<>();
		modes.add("Watchdog");
		modes.add("BlocksMC");
		modes.add("AGC");
		mode = new ModeSetting("Mode", "Watchdog", "BlocksMC", "AGC");
		addSettings(mode);
	}
	
	boolean hurt = false;
	boolean hitHeight = false;
	double startY = 0;
	public void onEnable() {				
		startY = mc.thePlayer.posY;
		hitHeight = false;
		mc.timer.timerSpeed = 0.175f;
		switch(mode.getCurrentValue()) {
		case "BlocksMC":
			mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,mc.thePlayer.posY + 3.1005,mc.thePlayer.posZ,false));
			mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,mc.thePlayer.posY,mc.thePlayer.posZ,false));
			mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,mc.thePlayer.posY,mc.thePlayer.posZ,true));
			break;
		case "AGC":
			mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,mc.thePlayer.posY - 2.1005,mc.thePlayer.posZ,false));
			mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
			break;
		case "Watchdog":
	        double x = mc.thePlayer.posX;
	        double y = mc.thePlayer.posY;
	        double z = mc.thePlayer.posZ;
	        double val = 100;
	        for(int i = 0; i < 54; i++) {
	        	mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C00PacketKeepAlive(1));
	            mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y - ((0.0285523643 / 50) * val), z, false));
	            mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));
	        }
	        mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, true));
			break;
		}
		//mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,mc.thePlayer.posY,mc.thePlayer.posZ,false));
		
	}
	public void onDisable() {
		hurt = false;
		mc.timer.timerSpeed = 1;
	}
	
	public void onEvent(Event event) {
		if(event instanceof EventMotion) {
			switch(mode.getCurrentValue()) {
			case "AGC":
				if(!hurt && mc.thePlayer.hurtTime > 0) {
					hurt = true;
				}
				if(hurt) {
					mc.timer.timerSpeed = 1;
					mc.thePlayer.setSpeed(1.95);
					if(mc.thePlayer.posY < startY + 3)
						mc.thePlayer.motionY = 0.32;
					else
						toggle();
				}
			break;
			case "BlocksMC":
				if(!hurt && mc.thePlayer.hurtTime > 0) {
					hurt = true;
					mc.thePlayer.motionY = 1.0;
				}
				if(hurt) {
					mc.timer.timerSpeed = 1;
					mc.thePlayer.setSpeed(1.89);
					if(mc.thePlayer.onGround) {
						toggle();
					}
				}
			break;	
			case "Watchdog":
				if(!hurt && mc.thePlayer.hurtTime > 0) {
					hurt = true;
				}
				if(hurt) {
					if(startY + 3 > mc.thePlayer.posY && !hitHeight) {
						mc.thePlayer.motionY = 0.43f;
					}else {
						hitHeight = true;
					}
					mc.timer.timerSpeed = 1;
					mc.thePlayer.setSpeed(0.28);
					if(mc.thePlayer.onGround && hitHeight) {
						toggle();
					}
				}
			break;
			}
		}
	}

}
