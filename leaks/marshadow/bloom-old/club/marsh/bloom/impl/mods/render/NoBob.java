package club.marsh.bloom.impl.mods.render;

import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.events.UpdateEvent;
//all my homies hate bob
// fuck bob
public class NoBob extends Module {

	public NoBob() {
		super("Anti Bob",Keyboard.KEY_NONE,Category.VISUAL);
	}
	@Subscribe
	public void onUpdate(UpdateEvent e) {
		mc.thePlayer.distanceWalkedModified = 0;
	}
}
