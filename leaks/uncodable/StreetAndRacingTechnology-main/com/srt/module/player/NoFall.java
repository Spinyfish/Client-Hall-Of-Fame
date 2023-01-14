package com.srt.module.player;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import com.srt.events.Event;
import com.srt.events.listeners.EventMotion;
import com.srt.module.ModuleBase;
import com.srt.settings.settings.ModeSetting;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;

public class NoFall extends ModuleBase {
	
	public NoFall() {
		super("NoFall", Keyboard.KEY_NONE, Category.PLAYER);
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventMotion) {
			if(mc.thePlayer.fallDistance > 3) {
				int dumb = 0;
				for(int i = (int)mc.thePlayer.posY; i > 0; i--) {
					if(mc.theWorld.isAirBlock(new BlockPos(mc.thePlayer.posX,i,mc.thePlayer.posZ))) {
						dumb++;
					}
				}
				if(dumb < (int)mc.thePlayer.posY-1) {
					((EventMotion)e).setOnGround(true);
					mc.thePlayer.fallDistance = 0;
				}
			}
		}
	}
}
