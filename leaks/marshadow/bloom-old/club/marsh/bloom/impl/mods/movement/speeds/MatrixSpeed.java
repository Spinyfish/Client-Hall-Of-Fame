package club.marsh.bloom.impl.mods.movement.speeds;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

import com.google.common.eventbus.Subscribe;

import club.marsh.bloom.api.module.Mode;
import club.marsh.bloom.api.module.Module;
import club.marsh.bloom.impl.utils.movement.MovementUtils;
import club.marsh.bloom.api.value.Value;
import net.minecraft.network.Packet;
import club.marsh.bloom.impl.events.UpdateEvent;

public class MatrixSpeed extends Mode {
	
	int ticks, strafeticks, jumps;

	public MatrixSpeed(Module original, String name, Value mode) {
		super(original, name, mode);
	}
	
	@Override
	public void onEnable() {
		strafeticks = ticks = jumps = 0;
	}
	
	
	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if (!this.canBeUsed()) return;
		mc.gameSettings.keyBindJump.pressed = false;
		if (mc.thePlayer.movementInput.moveForward != 0 || mc.thePlayer.movementInput.moveStrafe != 0) {
			if (mc.thePlayer.onGround) {
				mc.thePlayer.jump();
				strafeticks = 0;
				ticks = 0;
				jumps++;
			} else {
				if (ticks < 5 || mc.thePlayer.hurtTime != 0) {
				//mc.thePlayer.motionY -= 0.005;
				//mc.thePlayer.motionY *= 0.98f;
					MovementUtils.setSpeed(MovementUtils.getSpeed());
					if (MovementUtils.getSpeed() < 0.2)
						MovementUtils.setSpeed(0.2);
				}
			}
		} else {
			MovementUtils.setSpeed(0);
		}
		ticks++;
        if (mc.thePlayer.ticksExisted % 2 == 0) {
        	mc.timer.timerSpeed = 1.1f;
        } else if (mc.thePlayer.ticksExisted % 5 == 0) {
        	mc.timer.timerSpeed = 0.8f;
        } else if (mc.thePlayer.ticksExisted % 4 == 0) {
        	mc.timer.timerSpeed = 1.3f;
        } else if (mc.thePlayer.ticksExisted % 3 == 0) {
        	mc.timer.timerSpeed = 1f;
        }

	}

}
