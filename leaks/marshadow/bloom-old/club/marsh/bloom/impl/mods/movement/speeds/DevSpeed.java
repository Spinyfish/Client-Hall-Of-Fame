package club.marsh.bloom.impl.mods.movement.speeds;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

import club.marsh.bloom.impl.events.StrafeEvent;
import club.marsh.bloom.impl.utils.movement.MovementUtils;
import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Mode;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.api.value.Value;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import club.marsh.bloom.impl.events.UpdateEvent;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;

public class DevSpeed extends Mode {

	int ticks;
	double speed;
	private final Deque<Packet<?>> packetDeque = new ConcurrentLinkedDeque<>();

	public DevSpeed(Module original, String name, Value mode) {
		super(original, name, mode);
	}

	@Override
	public void onEnable() {

	}

	@Override
	public void onDisable() {

	}

	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if (!this.canBeUsed()) return;

		if (mc.thePlayer.onGround) {
			speed = 0.8;
			mc.thePlayer.jump();
		} else if (++ticks == 1) {
			speed *= 0.89;
		}
		MovementUtils.setSpeed(speed);
	}

	@Subscribe
	public void onStrafe(StrafeEvent e) {
		//if (mc.thePlayer.onGround || mc.thePlayer.hurtTime != 0)
		//	e.friction *= 1.065f;
		//e.friction *= 1.25f;
	}
}
