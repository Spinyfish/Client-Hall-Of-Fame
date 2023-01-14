package com.srt.module.combat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.input.Keyboard;

import com.srt.SRT;
import com.srt.events.Event;
import com.srt.events.listeners.EventMotion;
import com.srt.events.listeners.EventPacket;
import com.srt.events.listeners.EventUpdate;
import com.srt.module.ModuleBase;
import com.thunderware.utils.TimerUtils;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.potion.Potion;

public class Criticals extends ModuleBase {
	
	public boolean shouldCrit = false;
	public TimerUtils timer = new TimerUtils();
	public int groundTicks = 0;
	
	public Criticals() {
		super("Criticals", Keyboard.KEY_NONE, Category.COMBAT);
	}
	
	public void onEvent(Event event) {
		if(event instanceof EventPacket) {
			EventPacket e = ((EventPacket) event);
			if (e.getPacket() instanceof C02PacketUseEntity && ((C02PacketUseEntity) e.getPacket()).getAction() == C02PacketUseEntity.Action.ATTACK) {
	            final C02PacketUseEntity packetUseEntity = (C02PacketUseEntity) e.getPacket();
	            final EntityLivingBase entity = (EntityLivingBase) packetUseEntity.getEntityFromWorld(mc.theWorld);
            	groundTicks = 0;
	            if (mc.thePlayer.onGround && entity.hurtResistantTime <= 0) {
	                shouldCrit = true;
	            }
	            if(!shouldCrit) {
	            	//entity.hurtResistantTime = -1;
	            }
	        }
		}
		if(event instanceof EventMotion) {
			EventMotion e = (EventMotion)event;
			if(groundTicks > 9 && shouldCrit) {
				shouldCrit = false;
			}
			if(e.isPre() && shouldCrit && !mc.gameSettings.keyBindJump.pressed && mc.thePlayer.onGround && !SRT.moduleManager.getModuleByName("Flight").isToggled() && !SRT.moduleManager.getModuleByName("Speed").isToggled()) {
				double[] packetValues = {1.0E-9};
				if(groundTicks == 1) {
					for (double d : packetValues) {
	                    mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + d, mc.thePlayer.posZ, false));
	                }
					//mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
				}
				groundTicks++;
			}
		}
	}
	
	public void onDisable() {
		shouldCrit = false;
		groundTicks = 0;
	}
	
}
