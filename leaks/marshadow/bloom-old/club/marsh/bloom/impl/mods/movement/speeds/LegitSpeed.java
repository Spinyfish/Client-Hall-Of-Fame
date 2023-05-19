package club.marsh.bloom.impl.mods.movement.speeds;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Mode;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.utils.movement.MovementUtils;
import club.marsh.bloom.api.value.Value;
import club.marsh.bloom.impl.events.UpdateEvent;

public class LegitSpeed extends Mode {
	
	int airticks = 0;
	public LegitSpeed(Module original, String name, Value mode) {
		super(original, name, mode);
	}
	
	@Override
	public void onDisable() {airticks = 0;}
	
	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if (!this.canBeUsed()) return;
		if (mc.thePlayer.onGround) {
			mc.thePlayer.jump();
			airticks = 0;
			mc.thePlayer.motionX *= 1.035;
			mc.thePlayer.motionZ *= 1.035;
		} else airticks++;
		if (airticks > 6) MovementUtils.setSpeed(MovementUtils.getSpeed());
	}
}
