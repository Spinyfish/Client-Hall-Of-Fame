package club.marsh.bloom.impl.mods.movement.speeds;

import club.marsh.bloom.api.module.Mode;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.api.value.NumberValue;
import club.marsh.bloom.api.value.Value;
import club.marsh.bloom.impl.events.UpdateEvent;
import club.marsh.bloom.impl.utils.movement.MovementUtils;
import com.google.common.eventbus.Subscribe;

public class VanillaBhopSpeed extends Mode {


	NumberValue speed = new NumberValue("Speed",1.0,0,9.75, () -> (canBeUsed()));
	public VanillaBhopSpeed(Module original, String name, Value mode) {
		super(original, name, mode);
		registerValues(speed);
	}

	
	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if (!this.canBeUsed()) return;
		if (mc.thePlayer.onGround)
			mc.thePlayer.jump();
		MovementUtils.setSpeed(speed.getValDouble());
	}

}
