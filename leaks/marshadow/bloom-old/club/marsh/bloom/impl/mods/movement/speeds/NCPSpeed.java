package club.marsh.bloom.impl.mods.movement.speeds;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Mode;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.utils.movement.MovementUtils;
import club.marsh.bloom.api.value.Value;
import club.marsh.bloom.impl.events.UpdateEvent;

public class NCPSpeed extends Mode {
	public double speed = 0;
	public NCPSpeed(Module original, String name, Value mode) {
		super(original, name, mode);
	}
	@Override
	public void onDisable() {
		if (this.canBeUsed())
		mc.thePlayer.motionX = mc.thePlayer.motionZ = speed = 0;
	}
	
	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if (!this.canBeUsed()) return;
		mc.timer.timerSpeed = mc.thePlayer.motionY > 0.1 || mc.thePlayer.onGround ? 1.1f : 1f;
		if (mc.thePlayer.onGround) {
			mc.thePlayer.jump();
			//Block blockUnder = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX,mc.thePlayer.posY-1,mc.thePlayer.posZ)).getBlock();
			if (MovementUtils.getSpeed() < 0.48)
				MovementUtils.setSpeed(0.48);
			else
				MovementUtils.setSpeed(MovementUtils.getSpeed()); //addMessage(MovementUtils.getSpeed() + "");
		} else {
			MovementUtils.setSpeed(MovementUtils.getSpeed());
		}
	}

}
