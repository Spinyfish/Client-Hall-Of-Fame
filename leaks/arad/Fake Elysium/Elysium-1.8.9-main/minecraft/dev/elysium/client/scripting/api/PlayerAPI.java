package dev.elysium.client.scripting.api;

import dev.elysium.client.utils.player.MovementUtil;
import net.minecraft.client.entity.EntityPlayerSP;

public class PlayerAPI {

	public EntityPlayerSP real;
	
	public PlayerAPI(EntityPlayerSP real) {
		this.real = real;
	}
	
	public double GetMotionX() {
		return real.motionX;
	}
	public double GetMotionY() {
		return real.motionY;
	}
	public double GetMotionZ() {
		return real.motionZ;
	}
	
	public void Jump() {
		real.jump();
	}
	
	public boolean OnGround() {
		return real.onGround;
	}
	
	public void Strafe(double speed) {
		MovementUtil.strafe(speed);
	}
	public boolean IsMoving() {
		return MovementUtil.isMoving();
	}
}
