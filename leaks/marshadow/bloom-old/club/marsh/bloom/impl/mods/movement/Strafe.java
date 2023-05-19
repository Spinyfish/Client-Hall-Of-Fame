package club.marsh.bloom.impl.mods.movement;

import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.events.UpdateEvent;
import club.marsh.bloom.impl.utils.movement.MovementUtils;

public class Strafe extends Module
{
	public Strafe()
	{
		super("Strafe", Keyboard.KEY_NONE, Category.MOVEMENT);
	}
	
	@Subscribe
	public void onUpdate(UpdateEvent e)
	{
		MovementUtils.strafe();
	}
}
