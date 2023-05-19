package club.marsh.bloom.impl.mods.movement.flys;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

import club.marsh.bloom.api.value.BooleanValue;
import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Mode;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.utils.movement.MovementUtils;
import club.marsh.bloom.api.value.NumberValue;
import club.marsh.bloom.api.value.Value;
import net.minecraft.network.Packet;
import club.marsh.bloom.impl.events.UpdateEvent;

public class VanillaFly extends Mode {
	BooleanValue antiKick = new BooleanValue("Anti Kick", true, () -> (canBeUsed()));
	NumberValue speed = new NumberValue("Vanilla Speed",5.0,0,9.5, () -> (canBeUsed()));

	
	public VanillaFly(Module original, String name, Value mode) {
		super(original, name, mode);
		registerValues(speed, antiKick);
	}
	
	
	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if (!this.canBeUsed()) return;
		mc.thePlayer.motionY = mc.thePlayer.movementInput.jump ? speed.getObject().doubleValue() : mc.thePlayer.movementInput.sneak ? -speed.getObject().doubleValue() : 0;
		if (antiKick.isOn() && mc.thePlayer.ticksExisted % 2 == 0)
			mc.thePlayer.motionY = -0.3;
		MovementUtils.setSpeed(speed.getValDouble());
	}
}
