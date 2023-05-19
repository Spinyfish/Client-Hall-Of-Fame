package club.marsh.bloom.impl.mods.movement.speeds;

import club.marsh.bloom.api.module.Mode;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.api.value.Value;
import club.marsh.bloom.impl.events.UpdateEvent;
import club.marsh.bloom.impl.utils.movement.MovementUtils;
import com.google.common.eventbus.Subscribe;

public class AcentraSpeed extends Mode {

	int ticks;

	public AcentraSpeed(Module original, String name, Value mode) {
		super(original, name, mode);
	}

	@Override
	public void onEnable() {
		ticks = 0;
	}



	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if (!this.canBeUsed()) return;

		if (mc.thePlayer.onGround) {
			ticks = 0;
			mc.thePlayer.jump();
		} else if (++ticks == 4) {
			mc.thePlayer.motionY -= (double) (0.08F);
			mc.thePlayer.motionY *= (double) (0.98F);
		}
		if (mc.thePlayer.hurtTime > 4 && MovementUtils.isMoving())
			MovementUtils.setSpeed(MovementUtils.getSpeed());
	}

}
