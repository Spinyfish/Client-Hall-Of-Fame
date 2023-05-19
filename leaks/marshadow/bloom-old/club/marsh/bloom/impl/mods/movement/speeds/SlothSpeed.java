package club.marsh.bloom.impl.mods.movement.speeds;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Mode;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.utils.movement.MovementUtils;
import club.marsh.bloom.api.value.Value;
import net.minecraft.network.Packet;
import net.minecraft.potion.Potion;
import club.marsh.bloom.impl.events.CollideEvent;
import club.marsh.bloom.impl.events.PacketEvent;
import club.marsh.bloom.impl.events.UpdateEvent;

public class SlothSpeed extends Mode {
	

	public SlothSpeed(Module original, String name, Value mode) {
		super(original, name, mode);
	}
	
	@Override
	public void onEnable() {
		//mc.timer.timerSpeed = 1.05f;
	}
	
	@Override
	public void onDisable() {
		//mc.timer.timerSpeed = 1f;
	}
	
	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if (!this.canBeUsed()) return;
		if (!mc.thePlayer.onGround) {
			double boost = (mc.thePlayer.motionY > 0 ? mc.thePlayer.motionY-0.4 : 0);
			boost *= 1.15;
			if (mc.thePlayer.isPotionActive(Potion.moveSpeed))
				boost += (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier())/1.4;
			if (boost < 0)
				boost = 0;
			if (MovementUtils.getSpeed() < (0.35 + boost))
				MovementUtils.setSpeed(0.35 + boost);
			else
				MovementUtils.setSpeed(MovementUtils.getSpeed());
		} else {
			mc.thePlayer.jump();
			mc.thePlayer.motionY = 0.54;
			if (MovementUtils.getSpeed() < (0.45))
				MovementUtils.setSpeed(0.45);
			else
				MovementUtils.setSpeed(MovementUtils.getSpeed());
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
