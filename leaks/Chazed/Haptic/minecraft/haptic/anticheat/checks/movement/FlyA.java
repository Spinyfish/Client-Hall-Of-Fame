package haptic.anticheat.checks.movement;

import haptic.Haptic;
import haptic.anticheat.Check;
import haptic.anticheat.User;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;

public class FlyA extends Check {
	
	private int ticksNoYMove;
	
	private double lastMotionY;
	private boolean lastTickOnGround = true;
	private int ticksOffGround;
	
	private boolean tookVelocity;
	
	public FlyA(User user) {
		super("FlyA", user);
	}
	
	public void handle() {
		EntityPlayer player = user.getPlayer();
		
		if(player.onGround) {
			ticksOffGround = 0;
			if(mc.thePlayer.hurtTime == 0) {
				tookVelocity = false;
			}
		} else {
			ticksOffGround++;
			
			if(mc.thePlayer.hurtTime > 0) {
				tookVelocity = true;
			}
		}
		
		if(player.isInvisible() || player.capabilities.isFlying) {
			lastMotionY = player.motionY;
			lastTickOnGround = player.onGround;
			return;
		}
		
		if(player.hurtTime == 0) {
			if(lastMotionY != 0 && !player.onGround && !lastTickOnGround) {
				double predictedMotionY = (lastMotionY - 0.08) * 0.9800000190734863D;
				
				double maxExceed = 0.03 * (mc.getNetHandler().getPlayerInfo(player.getUniqueID()).getResponseTime() * 0.02);
				
				if(player.motionY > predictedMotionY + maxExceed) {
					violation();
				}
			}
		}
		
		if(player.motionY == 0 && player.hurtTime == 0) {
			if(++ticksNoYMove > 3) {
				violation();
			} else {
				ticksNoYMove = 0;
			}
		}
		
		if(Math.abs(player.motionY) < 0.001 && Math.abs(player.motionY) > 0 && player.hurtTime == 0) {
			violation();
		}
		
		lastMotionY = player.motionY;
		lastTickOnGround = player.onGround;
	}
}
