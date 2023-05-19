package club.marsh.bloom.impl.mods.movement.speeds;

import club.marsh.bloom.api.module.Mode;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.api.value.NumberValue;
import club.marsh.bloom.api.value.Value;
import club.marsh.bloom.impl.events.UpdateEvent;
import com.google.common.eventbus.Subscribe;

public class AAC4Speed extends Mode {

	int ticks;

	public AAC4Speed(Module original, String name, Value mode) {
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
		} else if (++ticks == 1) {
			double multi = mc.thePlayer.movementInput.moveStrafe == 0 ? 1.075 : 1.05;
			mc.thePlayer.motionX *= multi;
			mc.thePlayer.motionZ *= multi;
		}
	}

}
