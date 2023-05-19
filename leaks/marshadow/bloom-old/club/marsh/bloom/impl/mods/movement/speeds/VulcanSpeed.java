package club.marsh.bloom.impl.mods.movement.speeds;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Mode;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.utils.movement.MovementUtils;
import club.marsh.bloom.api.value.Value;
import net.minecraft.network.Packet;
import club.marsh.bloom.impl.events.CollideEvent;
import club.marsh.bloom.impl.events.PacketEvent;
import club.marsh.bloom.impl.events.UpdateEvent;

public class VulcanSpeed extends Mode {
	
	int ticks;
	
	public VulcanSpeed(Module original, String name, Value mode) {
		super(original, name, mode);
	}
	
	@Override
	public void onEnable() {
		ticks = 0;
	}
	
	@Override
	public void onDisable() {

	}
	
	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if (!this.canBeUsed()) return;
		if (mc.thePlayer.onGround) {
			MovementUtils.setSpeed(0.4);
			e.y += mc.thePlayer.motionY = 0.0152;
			ticks = 0;
		} else if (++ticks == 1) {
			MovementUtils.setSpeed(0.29);
		}
	}
	
	@Subscribe
	public void onPacket(PacketEvent e) {
	
	}
	
	@Subscribe
	public void onCollide(CollideEvent e) {
		//MovementUtils.collide(e);
	}
}
