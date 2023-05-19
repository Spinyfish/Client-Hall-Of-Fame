package club.marsh.bloom.impl.mods.movement.flys;

import club.marsh.bloom.api.components.BlinkingComponent;
import club.marsh.bloom.api.module.Mode;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.api.value.BooleanValue;
import club.marsh.bloom.api.value.NumberValue;
import club.marsh.bloom.api.value.Value;
import club.marsh.bloom.impl.events.UpdateEvent;
import club.marsh.bloom.impl.utils.movement.MovementUtils;
import com.google.common.eventbus.Subscribe;

public class NCPBlinkHeadhitterFly extends Mode {
	NumberValue speed = new NumberValue("Blink Speed",1.0,0,3, () -> (canBeUsed()));
	boolean hitHead = false;

	public NCPBlinkHeadhitterFly(Module original, String name, Value mode) {
		super(original, name, mode);
		registerValues(speed);
	}

	@Override
	public void onDisable() {
		BlinkingComponent.INSTANCE.setOn(false);
		super.onDisable();
	}

	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if (!this.canBeUsed()) return;
		if (mc.thePlayer.isCollidedVertically) {hitHead = true;};
		if (mc.thePlayer.onGround) {
			mc.thePlayer.jump();
			hitHead = false;
		}
		if (hitHead) {
			BlinkingComponent.INSTANCE.setOn(true);
			MovementUtils.setSpeed(speed.getValDouble());
			hitHead = false;
		}
	}
}
