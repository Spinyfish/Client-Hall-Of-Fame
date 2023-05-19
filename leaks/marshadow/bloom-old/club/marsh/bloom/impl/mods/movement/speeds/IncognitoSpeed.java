package club.marsh.bloom.impl.mods.movement.speeds;

import club.marsh.bloom.api.module.Mode;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.events.UpdateEvent;
import club.marsh.bloom.impl.utils.movement.MovementUtils;
import club.marsh.bloom.api.value.Value;
import com.google.common.eventbus.Subscribe;

public class IncognitoSpeed extends Mode {



	public IncognitoSpeed(Module original, String name, Value mode) {
		super(original, name, mode);
	}

	
	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if (!this.canBeUsed()) return;
		if (mc.thePlayer.onGround) {
			if (!mc.thePlayer.isCollidedHorizontally) {
				MovementUtils.setSpeed(mc.thePlayer.ticksExisted % 3 != 0 ? 0.3 : 3);
			}
		} else {
			MovementUtils.setSpeed(0);
		}
		//mc.timer.timerSpeed = 1.05f;
	}
	

}
