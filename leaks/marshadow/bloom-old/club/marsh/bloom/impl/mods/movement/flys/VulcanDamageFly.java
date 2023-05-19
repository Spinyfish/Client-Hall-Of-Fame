package club.marsh.bloom.impl.mods.movement.flys;

import club.marsh.bloom.api.module.Mode;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.events.CollideEvent;
import club.marsh.bloom.impl.events.PacketEvent;
import club.marsh.bloom.impl.events.UpdateEvent;
import club.marsh.bloom.impl.utils.movement.ClipUtils;
import club.marsh.bloom.api.value.Value;
import com.google.common.eventbus.Subscribe;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class VulcanDamageFly extends Mode {

	int ticks;

	public VulcanDamageFly(Module original, String name, Value mode) {
		super(original, name, mode);
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
		//while (!packetDeque.isEmpty())
		//	mc.getNetHandler().addToSendQueueSilent(packetDeque.poll());
		//packetDeque.clear();
	}

	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if (!this.canBeUsed()) return;
		if (mc.thePlayer.hurtTime != 0) {
			mc.thePlayer.motionY = (double) (0.42f);
			if (ticks == 1) {
				ClipUtils.clip(7.5, 0);
			}
			if (mc.thePlayer.hurtTime >= mc.thePlayer.maxHurtTime-1) {
				e.y -= 11;
			}
		}
		if (ticks == 1 && mc.thePlayer.ticksExisted % 2 == 0 && mc.thePlayer.motionY < -0.0984) {
			mc.thePlayer.motionY = -0.0984;
		}
	}

	@Subscribe
	public void onPacket(PacketEvent e) {
		if (!this.canBeUsed()) return;
		if (e.getPacket() instanceof S08PacketPlayerPosLook) {
			ticks = 1;
		}
	}

	@Subscribe
	public void onCollide(CollideEvent e) {
		if (!this.canBeUsed()) return;
		//if (mc.thePlayer.motionY < -0.25)
		//	MovementUtils.collide(e);
	}
}
