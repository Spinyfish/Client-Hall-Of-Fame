package club.marsh.bloom.impl.mods.player.nofalls;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Mode;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.utils.movement.MovementUtils;
import club.marsh.bloom.api.value.Value;
import club.marsh.bloom.impl.events.CollideEvent;

public class VerusNoFall extends Mode {
	
	public VerusNoFall(Module original, String name, Value mode) {
		super(original, name, mode);
	}
	
	@Subscribe
	public void onCollide(CollideEvent e) {
		if (!this.canBeUsed()) return;
		if (mc.thePlayer.fallDistance >= 2.65) {
			mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
			MovementUtils.collide(e);
		}
	}
	
	
}
