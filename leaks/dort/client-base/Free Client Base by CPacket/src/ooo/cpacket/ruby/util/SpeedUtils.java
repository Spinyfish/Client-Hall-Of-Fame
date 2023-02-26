package ooo.cpacket.ruby.util;

import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import ooo.cpacket.ruby.api.event.events.player.EventRawMove;

public class SpeedUtils {
	private static final Minecraft mc;

	static {
		mc = Minecraft.getMinecraft();
	
	}
	
	public static double getBaseMoveSpeed() {
		double baseSpeed = 0.2873;
		if (mc.thePlayer != null)
		if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)) {
			final int amplifier = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed)
					.getAmplifier(); 
			baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
		}
		return baseSpeed;
	}

	public static void setMoveSpeed(final EventRawMove aab, final double moveSpeed) {
		float forward = SpeedUtils.mc.thePlayer.movementInput.moveForward;
		float strafe = SpeedUtils.mc.thePlayer.movementInput.moveStrafe;
		float yaw = SpeedUtils.mc.thePlayer.rotationYaw;
		if (forward == 0.0 && strafe == 0.0) {
			SpeedUtils.mc.thePlayer.motionX = 0.0;
			SpeedUtils.mc.thePlayer.motionZ = 0.0;
		} else {
			if (forward != 0.0) {
				if (strafe > 0.0f) {
					yaw += ((forward > 0.0) ? -45 : 45);
				} else if (strafe < 0.0f) {
					yaw += ((forward > 0.0) ? 45 : -45);
				}
				strafe = 0.0f;
				if (forward > 0.0f) {
					forward = 1.0f;
				} else if (forward < 0.0f) {
					forward = -1.0f;
				}
			}
			final double xDist = forward * moveSpeed * Math.cos(Math.toRadians(yaw + 90.0f))
					+ strafe * moveSpeed * Math.sin(Math.toRadians(yaw + 90.0f));
			final double zDist = forward * moveSpeed * Math.sin(Math.toRadians(yaw + 90.0f))
					- strafe * moveSpeed * Math.cos(Math.toRadians(yaw + 90.0f));
			aab.setX(xDist);
			aab.setZ(zDist);
		}
	}

	public static void setMoveSpeed(final double moveSpeed) {
		float forward = SpeedUtils.mc.thePlayer.movementInput.moveForward;
		float strafe = SpeedUtils.mc.thePlayer.movementInput.moveStrafe;
		float yaw = SpeedUtils.mc.thePlayer.rotationYaw;
		if (forward == 0.0 && strafe == 0.0) {
			SpeedUtils.mc.thePlayer.motionX = 0.0;
			SpeedUtils.mc.thePlayer.motionZ = 0.0;
		}
		int d = 45;
        if (forward != 0.0) {
            if (strafe > 0.0) {
                yaw += ((forward > 0.0) ? -d : d);
            }
            else if (strafe < 0.0) {
                yaw += ((forward > 0.0) ? d : -d);
            }
            strafe = 0.0f;
            if (forward > 0.0) {
                forward = 1.0f;
            }
            else if (forward < 0.0) {
                forward = -1.0f;
            }
		}
		final double xDist = forward * moveSpeed * Math.cos(Math.toRadians(yaw + 90.0f))
				+ strafe * moveSpeed * Math.sin(Math.toRadians(yaw + 90.0f));
		final double zDist = forward * moveSpeed * Math.sin(Math.toRadians(yaw + 90.0f))
				- strafe * moveSpeed * Math.cos(Math.toRadians(yaw + 90.0f));
		SpeedUtils.mc.thePlayer.motionX = xDist;
		SpeedUtils.mc.thePlayer.motionZ = zDist;
	}

	public static void setSpeed(double speed) {
		if (mc.thePlayer.isMoving()) {
			Minecraft.getMinecraft().thePlayer.motionX = (-MathHelper.sin(getDirection()) * speed);
			Minecraft.getMinecraft().thePlayer.motionZ = (MathHelper.cos(getDirection()) * speed);
		} else {
			Minecraft.getMinecraft().thePlayer.motionX = 0;
			Minecraft.getMinecraft().thePlayer.motionZ = 0;
		}
	}

	public static float getDirection() {
		float yaw = Minecraft.getMinecraft().thePlayer.rotationYaw;
		float forward = Minecraft.getMinecraft().thePlayer.moveForward;
		float strafe = Minecraft.getMinecraft().thePlayer.moveStrafing;
		yaw += (forward < 0.0F ? 180 : 0);
		if (strafe < 0.0F) {
			yaw += (forward == 0.0F ? 90 : forward < 0.0F ? -45 : 45);
		}
		if (strafe > 0.0F) {
			yaw -= (forward == 0.0F ? 90 : forward < 0.0F ? -45 : 45);
		}

		return yaw * 0.017453292F;
	}

	public static long getBaseMoveSpeedLong() {
		double baseSpeed = 32;
		if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)) {
			final int amplifier = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed)
					.getAmplifier();
			baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
		}
		return (long) (baseSpeed / 32);
	}

	public static float getBaseMoveSpeedFloat() {
		double baseSpeed = 0.6;
		if (Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)) {
			final int amplifier = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.moveSpeed)
					.getAmplifier();
			baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
		}
		return (float)baseSpeed;
	}

	public static double getBaseSpeed() {
		return Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
	}

	public static boolean setMoveSpeed(long val) {
		float f = val * 0.1F;
		SpeedUtils.setSpeed(f);
		return true;
	}
	public static boolean setBaseMoveSpeed() {
		float f = (float) (SpeedUtils.getBaseMoveSpeedFloat());
		{
			SpeedUtils.setMoveSpeed(f);
			return true;
		}
	}
}