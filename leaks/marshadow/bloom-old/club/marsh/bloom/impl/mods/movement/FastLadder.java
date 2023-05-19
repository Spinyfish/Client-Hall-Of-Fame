package club.marsh.bloom.impl.mods.movement;

import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.events.UpdateEvent;
import club.marsh.bloom.api.value.NumberValue;


public class FastLadder extends Module {
	public FastLadder() {
		super("FastLadder",Keyboard.KEY_NONE,Category.MOVEMENT);
	}
	NumberValue motion = new NumberValue("Motion",0.42,0,1);
	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if (mc.thePlayer.isOnLadder())
			mc.thePlayer.motionY = motion.value.doubleValue();
	}
}
