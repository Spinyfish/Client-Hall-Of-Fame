package club.marsh.bloom.impl.mods.movement.flys;

import club.marsh.bloom.Bloom;
import club.marsh.bloom.api.module.Mode;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.api.value.BooleanValue;
import club.marsh.bloom.api.value.NumberValue;
import club.marsh.bloom.api.value.Value;
import club.marsh.bloom.impl.events.UpdateEvent;
import club.marsh.bloom.impl.ui.notification.NotificationType;
import club.marsh.bloom.impl.utils.movement.ClipUtils;
import club.marsh.bloom.impl.utils.movement.MovementUtils;
import com.google.common.eventbus.Subscribe;

public class CustomDamageFly extends Mode {
	double speed = 0;
	int stage = 0;
	BooleanValue boost = new BooleanValue("Boost", false, () -> (canBeUsed()));
	NumberValue<Double> boostSpeed = new NumberValue("Boost Speed",5.0D,0D,9.5D, () -> (canBeUsed() && boost.isOn()));
	NumberValue<Double> boostTicks = new NumberValue("Boost Ticks",159,0,350, 0, () -> (canBeUsed() && boost.isOn()));
	BooleanValue waitForDamage = new BooleanValue("Wait for damage", false, () -> canBeUsed());
	BooleanValue manualDamage = new BooleanValue("Manual damage", false, () -> canBeUsed() && waitForDamage.isOn());
	BooleanValue jump = new BooleanValue("Jump Before Damage", true, () -> canBeUsed());
	BooleanValue onlyOnGround = new BooleanValue("Only on ground", true, () -> canBeUsed());
	BooleanValue useMotion = new BooleanValue("Use Motion", true, () -> canBeUsed());
	NumberValue<Double> motion = new NumberValue("Motion",0D,0D,10D, () -> (canBeUsed() && useMotion.isOn()));


	public CustomDamageFly(Module original, String name, Value mode) {
		super(original, name, mode);
		registerValues(boost,boostSpeed,boostTicks, waitForDamage,manualDamage,jump,onlyOnGround,useMotion,motion);
	}

	@Override
	public void onEnable() {
		if (!this.canBeUsed()) return;
		if (onlyOnGround.isOn() && !mc.thePlayer.onGround) {
			Bloom.INSTANCE.notificationPublisher.publish("Warning", "We have prevented toggling off ground.", 1000L, NotificationType.WARNING);
			original.setToggled(false);
			return;
		}

		speed = waitForDamage.isOn() ? MovementUtils.getBaseSpeed() : boostSpeed.getValDouble();
		if (!manualDamage.isOn()) {
			ClipUtils.clip2(3.05,0,false);
			ClipUtils.clip2(0,0,false);
			ClipUtils.clip2(0,0,true);
		}
		stage = waitForDamage.isOn() ? 0 : 1;
		if (jump.isOn() && mc.thePlayer.onGround) {
			mc.thePlayer.jump();
		}
	}

	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if (!this.canBeUsed()) return;
		if (mc.thePlayer.hurtTime != 0)
			stage = 2;
		if (stage == 2) {
			if (waitForDamage.isOn())
				speed = boostSpeed.getValDouble();
			stage--;
		}
		if (stage < 1)
			return;
		speed -= speed / boostTicks.getObject().doubleValue();
		if (speed < MovementUtils.getBaseSpeed())
			speed = MovementUtils.getBaseSpeed();
		if (boost.isOn())
			MovementUtils.setSpeed(speed);
		if (useMotion.isOn())
			mc.thePlayer.motionY = motion.getValDouble();
	}
}
