package club.marsh.bloom.impl.mods.movement.speeds;

import club.marsh.bloom.api.module.Mode;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.api.value.Value;
import club.marsh.bloom.impl.events.UpdateEvent;
import club.marsh.bloom.impl.utils.movement.MovementUtils;
import com.google.common.eventbus.Subscribe;

public class HurttimeSpeed extends Mode {

	int ticks;

	public HurttimeSpeed(Module original, String name, Value mode) {
		super(original, name, mode);
	}

	@Override
	public void onEnable() {
		ticks = 0;
	}



	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if (!this.canBeUsed()) return;

		if (mc.thePlayer.hurtTime != 0)
			MovementUtils.setSpeed(MovementUtils.getSpeed());
	}

}
