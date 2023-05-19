package club.marsh.bloom.impl.mods.player;

import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.events.SafewalkEvent;

public class SafeWalk extends Module
{
	public SafeWalk()
	{
		super("Safe Walk", Keyboard.KEY_NONE, Category.PLAYER);
	}
	
	@Subscribe
	public void onSafewalk(SafewalkEvent e)
	{
		e.setCancelled(mc.thePlayer.onGround);
	}
}
