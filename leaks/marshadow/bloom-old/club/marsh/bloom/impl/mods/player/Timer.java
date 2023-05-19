package club.marsh.bloom.impl.mods.player;

import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.api.value.NumberValue;
import club.marsh.bloom.impl.events.UpdateEvent;

public class Timer extends Module
{
	public Timer()
	{
		super("Timer", Keyboard.KEY_NONE, Category.PLAYER);
	}
	
	NumberValue timer = new NumberValue("Timer",1, 0.25, 3);
	
	@Override
	public void onDisable()
	{
		mc.timer.timerSpeed = 1;
	}
	
	@Subscribe
	public void onUpdate(UpdateEvent e)
	{
		mc.timer.timerSpeed = (float) timer.value.doubleValue();
	}
}
