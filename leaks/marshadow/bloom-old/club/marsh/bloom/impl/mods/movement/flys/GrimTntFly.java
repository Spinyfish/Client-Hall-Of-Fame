package club.marsh.bloom.impl.mods.movement.flys;

import club.marsh.bloom.api.module.Mode;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.events.CollideEvent;
import club.marsh.bloom.impl.events.PacketEvent;
import club.marsh.bloom.impl.events.UpdateEvent;
import club.marsh.bloom.api.value.Value;
import com.google.common.eventbus.Subscribe;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

public class GrimTntFly extends Mode {

	int ticks;


	public GrimTntFly(Module original, String name, Value mode) {
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
	}

	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if (!this.canBeUsed()) return;
		mc.gameSettings.keyBindJump.pressed = mc.thePlayer.onGround;
		if (ticks == 1) {
			if (mc.thePlayer.hurtTime != 0) {
				e.y -= 100;
				e.x += 15;
				e.z += 15;
				mc.thePlayer.motionY = 2.5;
				mc.thePlayer.motionX *= 10;
				mc.thePlayer.motionZ *= 10;
				e.ground = false;
			}
			ticks = 0;
		}

	}

	@Subscribe
	public void onPacket(PacketEvent e) {
		if (!this.canBeUsed()) return;
		if (e.getPacket() instanceof S12PacketEntityVelocity) {
			if (((S12PacketEntityVelocity) e.getPacket()).getEntityID() == mc.thePlayer.getEntityId()) {
				ticks = 1;
				mc.thePlayer.motionY += (((S12PacketEntityVelocity) e.getPacket()).getMotionY()/8000) * 2000;
				mc.thePlayer.motionX += (((S12PacketEntityVelocity) e.getPacket()).getMotionX()/8000) * 2000;
				mc.thePlayer.motionZ += (((S12PacketEntityVelocity) e.getPacket()).getMotionZ()/8000) * 2000;
			}
		} //else if (e.getPacket() instanceof C0FPacketConfirmTransaction && ticks == 1) {
		//	e.setCancelled(true);
		//}
	}

	@Subscribe
	public void onCollide(CollideEvent e) {
		if (!this.canBeUsed()) return;

	}
}
