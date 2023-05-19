package club.marsh.bloom.impl.mods.movement.speeds;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Mode;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.utils.movement.MovementUtils;
import club.marsh.bloom.api.value.BooleanValue;
import club.marsh.bloom.api.value.Value;
import club.marsh.bloom.impl.events.UpdateEvent;

public class HypixelSpeed extends Mode {
	
	BooleanValue groundstrafe = new BooleanValue("Ground Strafe",false, () -> (canBeUsed()));
	BooleanValue damageboost = new BooleanValue("Damage Boost",true, () -> (canBeUsed()));
	
	
	public HypixelSpeed(Module original, String name, Value mode) {
		super(original, name, mode);
		registerValues(groundstrafe,damageboost);
	}
	
	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if (!this.canBeUsed()) return;
		mc.gameSettings.keyBindJump.pressed = false;
		if (mc.thePlayer.movementInput.moveForward != 0 || mc.thePlayer.movementInput.moveStrafe != 0) {
			if (mc.thePlayer.onGround) {
				mc.thePlayer.jump();
				if (groundstrafe.isOn())
					MovementUtils.setSpeed(MovementUtils.getSpeed() + 0.035);
			} else {
				if (mc.thePlayer.hurtTime != 0 && damageboost.isOn()) {
				//mc.thePlayer.motionY -= 0.005;
				//mc.thePlayer.motionY *= 0.98f;
					MovementUtils.setSpeed(MovementUtils.getSpeed());
					if (MovementUtils.getSpeed() < 0.3)
						MovementUtils.setSpeed(0.3);
				}
			}
		}
	}

}
