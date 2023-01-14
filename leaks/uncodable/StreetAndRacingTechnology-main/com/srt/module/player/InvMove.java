package com.srt.module.player;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import com.srt.events.Event;
import com.srt.events.listeners.EventMotion;
import com.srt.module.ModuleBase;
import com.srt.settings.settings.ModeSetting;

import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.network.play.client.C03PacketPlayer;

public class InvMove extends ModuleBase {
	
	public InvMove() {
		super("InvMove", Keyboard.KEY_NONE, Category.PLAYER);
		setDisplayName("Inventory Move");
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventMotion) {
			if(mc.thePlayer.openContainer != null && !mc.ingameGUI.getChatGUI().getChatOpen()) {
				mc.gameSettings.keyBindForward.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode());
				mc.gameSettings.keyBindBack.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode());
				mc.gameSettings.keyBindRight.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode());
				mc.gameSettings.keyBindLeft.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode());
				mc.gameSettings.keyBindJump.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());
				mc.gameSettings.keyBindSprint.pressed = Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode());
			}
		}
	}
}
