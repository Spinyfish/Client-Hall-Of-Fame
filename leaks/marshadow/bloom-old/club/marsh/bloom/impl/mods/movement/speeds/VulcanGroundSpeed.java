package club.marsh.bloom.impl.mods.movement.speeds;

import club.marsh.bloom.api.module.Mode;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.api.value.Value;
import club.marsh.bloom.impl.events.UpdateEvent;
import club.marsh.bloom.impl.utils.movement.MovementUtils;
import com.google.common.eventbus.Subscribe;
import net.minecraft.util.BlockPos;

public class VulcanGroundSpeed extends Mode {



	public VulcanGroundSpeed(Module original, String name, Value mode) {
		super(original, name, mode);
	}
	double moveSpeed = 0;
	int count = 0;
	@Override
	public void onEnable() {
		count = 0;
		moveSpeed = 0;
	}

	
	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if (!this.canBeUsed()) return;
		if (mc.thePlayer.movementInput.moveStrafe != 0 || mc.thePlayer.movementInput.moveForward != 0) {
			if (mc.thePlayer.onGround && mc.theWorld.isAirBlock(new BlockPos(mc.thePlayer.posX,mc.thePlayer.posY + 2,mc.thePlayer.posZ)) && mc.theWorld.isAirBlock(new BlockPos(mc.thePlayer.posX,mc.thePlayer.posY + 1,mc.thePlayer.posZ))) {
				double speed = Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ);
				boolean boost = mc.thePlayer.isPotionActive(1);
				switch (count) {
					case 1:
						moveSpeed = 0.42f;
						speed = boost ? speed + 0.2 : 0.48;
						e.setGround(true);
						break;
					case 2:
						speed = boost ? speed * 0.71 : 0.19;
						moveSpeed -= 0.0784f;
						e.setGround(false);
						break;
					default:
						count = 0;
						speed /= boost ? 0.64 : 0.66;
						e.setGround(true);
						break;
				}
				MovementUtils.setSpeed(speed);
				count++;
				e.setY(e.getY() + moveSpeed);
			}
		} else {
			count = 0;
		}
	}
	

}
