package club.marsh.bloom.impl.mods.player;

import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.events.UpdateEvent;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.GameSettings;

public class InventoryMove extends Module
{
	public InventoryMove()
	{
		super("Inventory Move",Keyboard.KEY_NONE,Category.PLAYER);
	}
	
	@Subscribe
	public void onUpdate(UpdateEvent e)
	{
		try
		{
			if (mc.currentScreen == null || mc.currentScreen instanceof GuiChat)
			{
				return;
			}
			
			mc.gameSettings.keyBindForward.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindForward);
			mc.gameSettings.keyBindBack.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindBack);
			mc.gameSettings.keyBindRight.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindRight);
			mc.gameSettings.keyBindLeft.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindLeft);
			mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump);
			mc.gameSettings.keyBindSprint.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindSprint);
		}
		
		catch (Exception exception)
		{
		}
	}
}
