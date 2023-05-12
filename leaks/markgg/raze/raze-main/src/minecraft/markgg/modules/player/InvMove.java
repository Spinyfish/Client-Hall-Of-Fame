package markgg.modules.player;

import markgg.modules.Module.Category;
import markgg.settings.BooleanSetting;
import markgg.settings.NumberSetting;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import markgg.Client;
import markgg.events.Event;
import markgg.events.listeners.EventUpdate;
import markgg.modules.Module;

public class InvMove extends Module{	

	public InvMove() {
		super("InvMove", "Move in your inventory", 0, Category.PLAYER);
	}

	public void onEvent(Event e) {
		if(e instanceof EventUpdate) {
			if (this.mc.currentScreen instanceof GuiScreen) {
				if (Keyboard.isKeyDown(205) && !(mc.currentScreen instanceof GuiChat)) mc.thePlayer.rotationYaw += 8.0F; 
				if (Keyboard.isKeyDown(203) && !(mc.currentScreen instanceof GuiChat)) mc.thePlayer.rotationYaw -= 8.0F; 
				if (Keyboard.isKeyDown(200) && !(mc.currentScreen instanceof GuiChat)) mc.thePlayer.rotationPitch -= 8.0F; 
				if (Keyboard.isKeyDown(208) && !(mc.currentScreen instanceof GuiChat)) mc.thePlayer.rotationPitch += 8.0F;
			}
		}

		KeyBinding[] moveKeys = { mc.gameSettings.keyBindRight, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindForward, mc.gameSettings.keyBindJump, mc.gameSettings.keyBindSprint };
		if (!(this.mc.currentScreen instanceof GuiScreen) || mc.currentScreen instanceof GuiChat) {
			byte b1;

			int j;

			KeyBinding[] arrayOfKeyBinding;

			for (j = (arrayOfKeyBinding = moveKeys).length, b1 = 0; b1 < j; ) { KeyBinding bind = arrayOfKeyBinding[b1];
			if (!Keyboard.isKeyDown(bind.getKeyCode()))
				KeyBinding.setKeyBindState(bind.getKeyCode(), false); 
			b1++; }

			return;
		}
		byte b;
		int i;
		KeyBinding[] arrayOfKeyBinding1;
		for (i = (arrayOfKeyBinding1 = moveKeys).length, b = 0; b < i; ) {
			KeyBinding key = arrayOfKeyBinding1[b];
			key.pressed = Keyboard.isKeyDown(key.getKeyCode());
			b++;
		} 
	}
}
