package club.marsh.bloom.impl.mods.movement.flys;

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

public class SlothFly extends Mode {
	

	
	public SlothFly(Module original, String name, Value mode) {
		super(original, name, mode);
	}
	
	@Override
	public void onEnable() {
		if (!this.canBeUsed()) return;
		//mc.thePlayer.motionY = 0.75;
	}
	
	@Override
	public void onDisable() {

	}
	
	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if (!this.canBeUsed()) return;
		e.ground = true;
		if (mc.thePlayer.ticksExisted % 2 == 0)
			mc.thePlayer.motionY = -0.25 - (mc.thePlayer.movementInput.sneak ? 0.35 : 0);
		else
			mc.thePlayer.motionY = 0.25 + (mc.thePlayer.movementInput.jump ? 0.35 : 0);
		//mc.thePlayer.motionY += mc.thePlayer.motionY > 0 ? Math.random()/100 : -Math.random()/100;
		double boost = (mc.thePlayer.motionY > 0 ? mc.thePlayer.motionY-0.4 : 0);
		if (mc.thePlayer.isPotionActive(Potion.moveSpeed))
			boost += (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier())/1.4;
		if (boost < 0)
			boost = 0;
		if (MovementUtils.getSpeed() < (0.25 + boost))
			MovementUtils.setSpeed(0.25 + boost);
		else
			MovementUtils.setSpeed(MovementUtils.getSpeed());
	}
	
	@Subscribe
	public void onPacket(PacketEvent e) {
	
	}
	
	@Subscribe
	public void onCollide(CollideEvent e) {
		//MovementUtils.collide(e);
	}
}
