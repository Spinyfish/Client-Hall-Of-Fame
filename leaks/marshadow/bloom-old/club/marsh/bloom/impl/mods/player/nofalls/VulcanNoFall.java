package club.marsh.bloom.impl.mods.player.nofalls;

import club.marsh.bloom.api.module.Mode;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.api.value.Value;
import club.marsh.bloom.impl.events.CollideEvent;
import club.marsh.bloom.impl.events.UpdateEvent;
import club.marsh.bloom.impl.utils.movement.MovementUtils;
import com.google.common.eventbus.Subscribe;

public class VulcanNoFall extends Mode {

	public VulcanNoFall(Module original, String name, Value mode) {
		super(original, name, mode);
	}
	
	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if (!this.canBeUsed()) return;
		if (mc.thePlayer.fallDistance >= 3.5) {
			mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
			if (mc.thePlayer.ticksExisted % 2 == 0 && mc.thePlayer.motionY < -0.0984)
				mc.thePlayer.motionY = -0.0984;
		}

		if (mc.thePlayer.fallDistance >= 4) {
			e.ground = true;
			e.y = e.y - e.y % 0.015625;
			mc.thePlayer.fallDistance = 0;
		}
		if (mc.thePlayer.motionY < -0.35)
			mc.thePlayer.motionY = -0.0984;
	}
	
	
}
