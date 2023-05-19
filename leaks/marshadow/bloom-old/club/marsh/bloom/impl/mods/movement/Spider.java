package club.marsh.bloom.impl.mods.movement;

import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.events.UpdateEvent;
import club.marsh.bloom.api.value.NumberValue;


public class Spider extends Module {
	public Spider() {
		super("Spider",Keyboard.KEY_NONE,Category.MOVEMENT);
	}
	NumberValue motion = new NumberValue("Motion",0.42,0,1); //thats the jump motion fyi
	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if (mc.thePlayer.isCollidedHorizontally)
			mc.thePlayer.motionY = motion.getValDouble();
	}
}
