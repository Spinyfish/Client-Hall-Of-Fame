package club.marsh.bloom.impl.mods.movement.flys;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Mode;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.utils.movement.MovementUtils;
import club.marsh.bloom.api.value.Value;
import club.marsh.bloom.impl.events.CollideEvent;
import club.marsh.bloom.impl.events.UpdateEvent;

public class JumpFly extends Mode {
	

	public JumpFly(Module original, String name, Value mode) {
		super(original, name, mode);
	}


	
	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if (!this.canBeUsed()) return;
		if(mc.thePlayer.onGround)
			mc.thePlayer.jump();
	}

	
	@Subscribe
	public void onCollide(CollideEvent e) {
		if (!this.canBeUsed()) return;
		if (mc.thePlayer.fallDistance > 1)
			MovementUtils.collide(e);
	}
}
