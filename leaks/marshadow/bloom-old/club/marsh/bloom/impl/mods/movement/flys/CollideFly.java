package club.marsh.bloom.impl.mods.movement.flys;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Mode;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.utils.movement.MovementUtils;
import club.marsh.bloom.api.value.Value;
import net.minecraft.network.Packet;
import club.marsh.bloom.impl.events.CollideEvent;

public class CollideFly extends Mode {
	

	public CollideFly(Module original, String name, Value mode) {
		super(original, name, mode);
	}
	
	@Subscribe
	public void onCollide(CollideEvent e) {
		if (!this.canBeUsed()) return;
		//e.setBoundingBox(AxisAlignedBB.fromBounds(-50, -1, -50, 50, 1.0F, 50));
		MovementUtils.collide(e);
	}
}
