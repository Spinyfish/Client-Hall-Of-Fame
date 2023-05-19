package club.marsh.bloom.impl.mods.movement.speeds;

import club.marsh.bloom.api.module.Mode;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.api.value.Value;
import club.marsh.bloom.impl.events.UpdateEvent;
import club.marsh.bloom.impl.utils.movement.MovementUtils;
import com.google.common.eventbus.Subscribe;

public class AAC5Speed extends Mode {


	public AAC5Speed(Module original, String name, Value mode) {
		super(original, name, mode);
	}
	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if (!this.canBeUsed()) return;
		if (MovementUtils.isMoving()) {
			if (mc.thePlayer.onGround) {
				mc.thePlayer.jump();
			}
		}
		if (mc.thePlayer.movementInput.moveStrafe == 0) {
			mc.thePlayer.speedInAir = (float) (mc.thePlayer.ticksExisted % 2 == 0 ? 0.027 : 0.02);
		} else {
			mc.thePlayer.speedInAir = 0.02f;
		}
	}

}
