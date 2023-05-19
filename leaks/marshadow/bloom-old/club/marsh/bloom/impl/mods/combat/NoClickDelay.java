package club.marsh.bloom.impl.mods.combat;

import org.lwjgl.input.Keyboard;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Category;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.events.Render2DEvent;


public class NoClickDelay extends Module {
	public NoClickDelay() {
		super("No Click Delay",Keyboard.KEY_NONE,Category.COMBAT);
	}

	@Subscribe
	public void onRender(Render2DEvent e) {
		mc.leftClickCounter = -1;
	}

}
