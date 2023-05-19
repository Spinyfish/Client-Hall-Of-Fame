package club.marsh.bloom.impl.mods.movement.flys;

import club.marsh.bloom.api.module.Mode;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.events.CollideEvent;
import club.marsh.bloom.impl.events.PacketEvent;
import club.marsh.bloom.impl.events.UpdateEvent;
import club.marsh.bloom.impl.utils.movement.ClipUtils;
import club.marsh.bloom.impl.utils.movement.MovementUtils;
import club.marsh.bloom.api.value.Value;
import com.google.common.eventbus.Subscribe;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class NoRulesFly extends Mode {

	int ticks;
	private final Deque<Packet<?>> packetDeque = new ConcurrentLinkedDeque<>();

	public NoRulesFly(Module original, String name, Value mode) {
		super(original, name, mode);
	}

	@Override
	public void onEnable() {
		if (!this.canBeUsed()) return;
		packetDeque.clear();
	}

	@Override
	public void onDisable() {
		if (!this.canBeUsed()) return;
		ticks = 0;
		mc.thePlayer.speedInAir = 0.02f;
		mc.thePlayer.motionX = 0;
		mc.thePlayer.motionZ = 0;
		//mc.gameSettings.keyBindUseItem.pressed = false;
	}

	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if (!this.canBeUsed()) return;
		if (ticks == 0) {
			if (mc.thePlayer.onGround) {
				mc.thePlayer.jump();
				mc.thePlayer.motionY = 0.42;
			} else if (mc.thePlayer.fallDistance > 0) {
				mc.thePlayer.motionX = mc.thePlayer.motionZ = mc.thePlayer.motionY = 0;
			} else if (mc.thePlayer.motionY <= 0.4 && mc.thePlayer.motionY > 0) mc.thePlayer.motionY += 0.045;
			mc.thePlayer.speedInAir = 0f;
			mc.thePlayer.motionX = 0;
			mc.thePlayer.motionZ = 0;
		} else if (ticks != -1) {
			if (mc.thePlayer.speedInAir == 0f)
				ClipUtils.clip(0.5,0);
			mc.thePlayer.speedInAir = 0.02f;
			if (mc.thePlayer.ticksExisted-ticks <= 50) {
				MovementUtils.setSpeed(3 - (mc.thePlayer.ticksExisted-ticks)/50);
				mc.thePlayer.motionY = mc.thePlayer.movementInput.jump ? 0.85 : mc.thePlayer.movementInput.sneak ? -0.85 : 0;
			} else {
				ticks = -1;
				mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
			}
		} else if (ticks == -1) {
			mc.thePlayer.motionY = 0;
		}
	}

	@Subscribe
	public void onPacket(PacketEvent e) {
		if (!this.canBeUsed()) return;
		if (mc.thePlayer != null && mc.theWorld != null && mc.thePlayer.ticksExisted <= 1)
			packetDeque.clear();
		if (e.getPacket() instanceof S08PacketPlayerPosLook) {
			ticks = mc.thePlayer.ticksExisted;
		} else if (e.getPacket() instanceof C03PacketPlayer && !((C03PacketPlayer) e.getPacket()).isMoving() && !((C03PacketPlayer) e.getPacket()).getRotating()) {
			if (ticks > 0) {
				e.setCancelled(true);
				if (mc.thePlayer.ticksExisted % 4 == 0)
					ticks += 1;
			}
		}
	}

	@Subscribe
	public void onCollide(CollideEvent e) {
		if (!this.canBeUsed()) return;
		//MovementUtils.collide(e);
	}
}
