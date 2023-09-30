package igbt.astolfy.module.player;

import org.lwjgl.input.Keyboard;

import igbt.astolfy.events.Event;
import igbt.astolfy.events.listeners.EventUpdate;
import igbt.astolfy.module.ModuleBase;
import net.minecraft.client.gui.GuiChat;

public class InvMove extends ModuleBase {

	public InvMove() {
		super("InvMove", Keyboard.KEY_NONE, Category.PLAYER);
	}
	
	public void onEvent(Event e) {
		if(e instanceof EventUpdate) {
	        if (mc.currentScreen != null && !(mc.currentScreen instanceof GuiChat)) {
				mc.gameSettings.keyBindForward.pressed = org.lwjgl.input.Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode());
				mc.gameSettings.keyBindLeft.pressed = org.lwjgl.input.Keyboard.isKeyDown(mc.gameSettings.keyBindLeft.getKeyCode());
				mc.gameSettings.keyBindBack.pressed = org.lwjgl.input.Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode());
				mc.gameSettings.keyBindRight.pressed = org.lwjgl.input.Keyboard.isKeyDown(mc.gameSettings.keyBindRight.getKeyCode());
				mc.gameSettings.keyBindJump.pressed = org.lwjgl.input.Keyboard.isKeyDown(mc.gameSettings.keyBindJump.getKeyCode());
				mc.gameSettings.keyBindSprint.pressed = org.lwjgl.input.Keyboard.isKeyDown(mc.gameSettings.keyBindSprint.getKeyCode());
	        }
		}
	}

}
