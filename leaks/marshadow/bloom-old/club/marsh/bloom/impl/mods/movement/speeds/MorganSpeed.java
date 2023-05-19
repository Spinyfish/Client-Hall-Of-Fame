package club.marsh.bloom.impl.mods.movement.speeds;

import club.marsh.bloom.impl.mods.combat.KillAura;
import club.marsh.bloom.impl.utils.movement.MovementUtils;
import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Mode;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.api.value.Value;
import club.marsh.bloom.impl.events.UpdateEvent;

public class MorganSpeed extends Mode {

	int ticks = 0;
	public MorganSpeed(Module original, String name, Value mode) {
		super(original, name, mode);
	}

	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if (this.canBeUsed()) {
			if (mc.thePlayer.onGround) {
				mc.thePlayer.jump();
				ticks = 0;
			} else if (++ticks == 1) {
				if (!KillAura.toggled) {
					MovementUtils.setSpeed(MovementUtils.getSpeed());
				}
			}
        }
	}

}
