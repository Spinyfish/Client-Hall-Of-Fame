package club.marsh.bloom.impl.mods.render;

import org.lwjgl.input.Keyboard;

import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;

public class BetterFPS extends Module
{
	public static boolean on = false;
	
	public BetterFPS()
	{
		super("Better FPS",Keyboard.KEY_NONE,Category.VISUAL);
	}
	
	@Override
	public void onEnable()
	{
		on = true;
	}
	
	@Override
	public void onDisable()
	{
		on = false;
	}
}
