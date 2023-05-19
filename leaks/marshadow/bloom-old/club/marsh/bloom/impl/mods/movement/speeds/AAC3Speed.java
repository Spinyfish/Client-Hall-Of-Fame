package club.marsh.bloom.impl.mods.movement.speeds;

import club.marsh.bloom.api.module.Mode;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.api.value.NumberValue;
import club.marsh.bloom.api.value.Value;
import club.marsh.bloom.impl.events.UpdateEvent;
import com.google.common.eventbus.Subscribe;

public class AAC3Speed extends Mode {

	int ticks;
	NumberValue speed = new NumberValue("Speed",1.2,1,1.5, () -> (canBeUsed()));

	public AAC3Speed(Module original, String name, Value mode) {
		super(original, name, mode);
		registerValues(speed);
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
			mc.thePlayer.motionX *= speed.getValDouble();
			mc.thePlayer.motionZ *= speed.getValDouble();
		}
	}

}
