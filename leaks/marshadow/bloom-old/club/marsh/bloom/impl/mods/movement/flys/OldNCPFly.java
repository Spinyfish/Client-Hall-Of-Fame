package club.marsh.bloom.impl.mods.movement.flys;

import club.marsh.bloom.api.module.Mode;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.api.value.BooleanValue;
import club.marsh.bloom.impl.events.UpdateEvent;
import club.marsh.bloom.impl.utils.movement.MovementUtils;
import club.marsh.bloom.api.value.Value;
import com.google.common.eventbus.Subscribe;

public class OldNCPFly extends Mode {

	int ticks;
	float speed = 0.2873F;
	BooleanValue boost = new BooleanValue("Boost", false, this::canBeUsed);
	BooleanValue vertical = new BooleanValue("Vertical (Damage Required)", true, this::canBeUsed);
	public OldNCPFly(Module original, String name, Value mode) {
		super(original, name, mode);
		this.registerValues(boost, vertical);
	}
	
	@Override
	public void onEnable() {
		if (!this.canBeUsed()) return;
	}
	
	@Override
	public void onDisable() {
		if (!this.canBeUsed()) return;
		ticks = 0;
		mc.thePlayer.speedInAir = 0.02f;
		speed = 0.2873F;
	}
	
	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if (!this.canBeUsed()) return;
		if (!boost.isOn())
			speed = 0.2873f;
		mc.thePlayer.motionY = 0;
		mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 1.0e-4, mc.thePlayer.posZ);
		MovementUtils.setSpeed(speed);
		speed -= speed/15;
		if (vertical.isOn()) {
			if (mc.thePlayer.movementInput.jump)
				mc.thePlayer.motionY += 0.3;
			else if (mc.thePlayer.movementInput.sneak)
				mc.thePlayer.motionY -= 0.3;
		}
		if (speed < 0.2873 || !MovementUtils.isMoving()) {
			speed = 0.2873f;
		}
		if (mc.thePlayer.onGround) {
			speed = 1.5f;
			mc.thePlayer.jump();
		}
	}

}
