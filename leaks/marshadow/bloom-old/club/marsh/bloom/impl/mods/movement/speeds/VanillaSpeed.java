package club.marsh.bloom.impl.mods.movement.speeds;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Mode;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.utils.movement.MovementUtils;
import club.marsh.bloom.api.value.NumberValue;
import club.marsh.bloom.api.value.Value;
import net.minecraft.network.Packet;
import club.marsh.bloom.impl.events.UpdateEvent;

public class VanillaSpeed extends Mode {
	

	NumberValue speed = new NumberValue("Speed",1.0,0,9.75, () -> (canBeUsed()));
	public VanillaSpeed(Module original, String name, Value mode) {
		super(original, name, mode);
		registerValues(speed);
	}

	
	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if (!this.canBeUsed()) return;
		MovementUtils.setSpeed(speed.getValDouble());
	}

}
