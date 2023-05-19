package club.marsh.bloom.impl.mods.player.nofalls;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Mode;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.api.value.Value;
import club.marsh.bloom.impl.events.UpdateEvent;

public class GroundSpoofNoFall extends Mode {
	
	public GroundSpoofNoFall(Module original, String name, Value mode) {
		super(original, name, mode);
	}
	
	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if (!this.canBeUsed()) return;
		e.ground = true;
	}
	
	
}
