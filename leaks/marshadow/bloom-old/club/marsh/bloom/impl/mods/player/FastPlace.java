package club.marsh.bloom.impl.mods.player;

import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.events.UpdateEvent;

public class FastPlace extends Module
{
	public FastPlace()
	{
		super("Fast Place",Keyboard.KEY_NONE,Category.PLAYER);
	}
	
	@Subscribe
	public void onUpdate(UpdateEvent e)
	{
		mc.rightClickDelayTimer = 0;
	}
}
