package markgg.events;

import net.minecraft.client.Minecraft;

public class EventMove extends Event{
	public double x;

	public double y;

	public double z;

	public EventMove(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public double getX() {
		return this.x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return this.y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return this.z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public void setSpeed(double moveSpeed) {
		float forward = (Minecraft.getMinecraft()).thePlayer.movementInput.moveForward;
		float strafe = (Minecraft.getMinecraft()).thePlayer.movementInput.moveStrafe;
		float yaw = (Minecraft.getMinecraft()).thePlayer.rotationYaw;
		if (forward == 0.0D && strafe == 0.0D) {
			(Minecraft.getMinecraft()).thePlayer.motionX = 0.0D;
			(Minecraft.getMinecraft()).thePlayer.motionZ = 0.0D;
		} else {
			if (forward != 0.0D) {
				if (strafe > 0.0F) {
					yaw += ((forward > 0.0D) ? -45 : 45);
				} else if (strafe < 0.0F) {
					yaw += ((forward > 0.0D) ? 45 : -45);
				} 
				strafe = 0.0F;
				if (forward > 0.0F) {
					forward = 1.0F;
				} else if (forward < 0.0F) {
					forward = -1.0F;
				} 
			} 
			double xDist = forward * moveSpeed * Math.cos(Math.toRadians((yaw + 90.0F))) + strafe * moveSpeed * Math.sin(Math.toRadians((yaw + 90.0F)));
			double zDist = forward * moveSpeed * Math.sin(Math.toRadians((yaw + 90.0F))) - strafe * moveSpeed * Math.cos(Math.toRadians((yaw + 90.0F)));
			(Minecraft.getMinecraft()).thePlayer.motionX = xDist;
			(Minecraft.getMinecraft()).thePlayer.motionZ = zDist;
			setX(xDist);
			setZ(zDist);
		} 
	}

	public void setSpeed(double moveSpeed, float yaw) {
		float forward = (Minecraft.getMinecraft()).thePlayer.movementInput.moveForward;
		float strafe = (Minecraft.getMinecraft()).thePlayer.movementInput.moveStrafe;
		if (forward == 0.0D && strafe == 0.0D) {
			(Minecraft.getMinecraft()).thePlayer.motionX = 0.0D;
			(Minecraft.getMinecraft()).thePlayer.motionZ = 0.0D;
		} else {
			if (forward != 0.0D) {
				if (strafe > 0.0F) {
					yaw += ((forward > 0.0D) ? -45 : 45);
				} else if (strafe < 0.0F) {
					yaw += ((forward > 0.0D) ? 45 : -45);
				} 
				strafe = 0.0F;
				if (forward > 0.0F) {
					forward = 1.0F;
				} else if (forward < 0.0F) {
					forward = -1.0F;
				} 
			} 
			double xDist = forward * moveSpeed * Math.cos(Math.toRadians((yaw + 90.0F))) + 
					strafe * moveSpeed * Math.sin(Math.toRadians((yaw + 90.0F)));
			double zDist = forward * moveSpeed * Math.sin(Math.toRadians((yaw + 90.0F))) - 
					strafe * moveSpeed * Math.cos(Math.toRadians((yaw + 90.0F)));
			(Minecraft.getMinecraft()).thePlayer.motionX = xDist;
			(Minecraft.getMinecraft()).thePlayer.motionZ = zDist;
			setX(xDist);
			setZ(zDist);
		} 
	}
}
