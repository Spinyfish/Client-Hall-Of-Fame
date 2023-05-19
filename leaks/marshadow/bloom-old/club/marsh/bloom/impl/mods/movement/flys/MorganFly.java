package club.marsh.bloom.impl.mods.movement.flys;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Mode;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.api.value.Value;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import club.marsh.bloom.impl.events.CollideEvent;
import club.marsh.bloom.impl.events.UpdateEvent;

public class MorganFly extends Mode {
	
	int ticks;
	public MorganFly(Module original, String name, Value mode) {
		super(original, name, mode);
	}
	
	@Override
	public void onEnable() {
		if (!this.canBeUsed()) return;
		ticks = 0;
	}
	
	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if (!this.canBeUsed()) return;
		if (mc.thePlayer.onGround) {
			e.y -= 11;
			mc.thePlayer.jump();
		} else {
			mc.thePlayer.motionY = 0;
			mc.thePlayer.movementInput.jump = false;
			mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1, mc.thePlayer.posZ), 1, null, 0f, 1f, 0f));
		}
	}

	@Subscribe
	public void onCollide(CollideEvent e) {
		if (!this.canBeUsed()) return;
		//if (MovementUtils.isMoving() && mc.timer.timerSpeed != 0.45f)
		//	MovementUtils.collide(e);
	}
}
