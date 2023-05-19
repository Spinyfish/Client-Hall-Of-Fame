package club.marsh.bloom.impl.mods.movement.flys;

import club.marsh.bloom.impl.utils.movement.ClipUtils;
import club.marsh.bloom.impl.utils.movement.MovementUtils;
import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Mode;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.api.value.Value;
import net.minecraft.network.Packet;
import club.marsh.bloom.impl.events.CollideEvent;
import club.marsh.bloom.impl.events.PacketEvent;
import club.marsh.bloom.impl.events.UpdateEvent;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class DevFly extends Mode {

	int ticks;
	double veloX, veloY, veloZ;
	private final Deque<Packet<?>> packetDeque = new ConcurrentLinkedDeque<>();

	public DevFly(Module original, String name, Value mode) {
		super(original, name, mode);
	}

	@Override
	public void onEnable() {
		if (!this.canBeUsed()) return;
		//veloY = mc.thePlayer.posY;
		veloY = Math.round(mc.thePlayer.posY) + 1;
		mc.getNetHandler().addToSendQueueSilent(new C03PacketPlayer.C04PacketPlayerPosition(0, 1E159, 0, false));
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
		if (ticks == 0) {
			return;
		}
		mc.thePlayer.motionY = (double) (-0.0784F);
		if (mc.thePlayer.ticksExisted % 2 == 0) {
			mc.thePlayer.motionY = 0.02f;
			e.ground = true;
		}
		MovementUtils.setSpeed(0);
		MovementUtils.setPosition(1);
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
