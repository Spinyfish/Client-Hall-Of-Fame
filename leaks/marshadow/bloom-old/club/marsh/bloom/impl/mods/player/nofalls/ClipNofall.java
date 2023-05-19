package club.marsh.bloom.impl.mods.player.nofalls;

import club.marsh.bloom.api.module.Mode;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.events.UpdateEvent;
import club.marsh.bloom.api.value.Value;
import com.google.common.eventbus.Subscribe;

public class ClipNofall extends Mode {

	public ClipNofall(Module original, String name, Value mode) {
		super(original, name, mode);
	}
	
	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if (!this.canBeUsed()) return;
		if (mc.thePlayer.fallDistance >= 3) {
			e.y -= 11;
			e.ground = true;
			mc.thePlayer.fallDistance = 0;
		}
	}
	
	
}
