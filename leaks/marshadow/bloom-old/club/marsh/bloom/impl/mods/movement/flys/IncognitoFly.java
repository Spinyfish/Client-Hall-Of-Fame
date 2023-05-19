package club.marsh.bloom.impl.mods.movement.flys;

import club.marsh.bloom.api.module.Mode;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.events.CollideEvent;
import club.marsh.bloom.impl.events.UpdateEvent;
import club.marsh.bloom.impl.utils.movement.MovementUtils;
import club.marsh.bloom.api.value.Value;
import com.google.common.eventbus.Subscribe;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;

public class IncognitoFly extends Mode {

	int ticks;
	public IncognitoFly(Module original, String name, Value mode) {
		super(original, name, mode);
	}
	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if (!this.canBeUsed()) return;
		if (mc.thePlayer.hurtTime != 0)
			ticks = mc.thePlayer.ticksExisted;
		if (mc.thePlayer.onGround) {
			MovementUtils.setSpeed(0.325);
			mc.thePlayer.jump();
			//mc.thePlayer.motionY = 0.0152;
		}
		if (Math.abs(ticks-mc.thePlayer.ticksExisted) <= 14 && Math.abs(ticks-mc.thePlayer.ticksExisted) >= 3) {
			mc.thePlayer.motionY = 0;
			mc.thePlayer.motionY += mc.thePlayer.movementInput.jump ? 0.75 : 0;
			mc.thePlayer.motionY -= mc.thePlayer.movementInput.sneak ? 0.75 : 0;
			MovementUtils.setSpeed(9.5);
		}
		if (Math.abs(ticks-mc.thePlayer.ticksExisted) == 15)
			mc.thePlayer.motionX = mc.thePlayer.motionZ = 0;
		if (mc.thePlayer.onGround || (mc.thePlayer.fallDistance > 0 && mc.thePlayer.ticksExisted % 2 == 0)) {
			mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
			mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ), 1, null, 0f, 1f, 0f));
		}
	}

	
	@Subscribe
	public void onCollide(CollideEvent e) {
		if (!this.canBeUsed()) return;
		if (mc.thePlayer.fallDistance > 0.75)
			MovementUtils.collide(e);
	}
}
