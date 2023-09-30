package axolotl.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class RotationUtils {
	
	public static Minecraft mc = Minecraft.getMinecraft();

	public static float[] getRotationsToEntity(Entity e) {
		double deltaX = e.posX + e.posX - e.lastTickPosX - mc.thePlayer.lastTickPosX;
		double deltaY = e.posY - 3.5D + e.getEyeHeight() - mc.thePlayer.posY + mc.thePlayer.getEyeHeight();
		double deltaZ = e.posZ + e.posZ - e.lastTickPosZ - mc.thePlayer.lastTickPosZ;
		double distance = Math.sqrt(Math.pow(deltaX, 2.0D) + Math.pow(deltaZ, 2.0D));
		float yaw = (float)Math.toDegrees(-Math.atan(deltaX / deltaZ));
		float pitch = (float)-Math.toDegrees(Math.atan(deltaY / distance));
		double v = Math.toDegrees(Math.atan(deltaZ / deltaX));
		if (deltaX < 0.0D && deltaZ < 0.0D) {
			yaw = (float)(90.0D + v);
		} else if (deltaX > 0.0D && deltaZ < 0.0D) {
			yaw = (float)(-90.0D + v);
		}
		return new float[] { yaw, pitch };
	}

	public static double getDirection(final float yaw, final Object yaw2) {
		float rotationYaw = yaw;

		if (yaw2 != null) {
			rotationYaw = (float)yaw2;
		}

		if (mc.thePlayer.moveForward < 0F) rotationYaw += 180F;

		float forward = 1F;

		if (mc.thePlayer.moveForward < 0F) forward = -0.5F;
		else if (mc.thePlayer.moveForward > 0F) forward = 0.5F;

		if (mc.thePlayer.moveStrafing > 0F) rotationYaw -= 90F * forward;
		if (mc.thePlayer.moveStrafing < 0F) rotationYaw += 90F * forward;

		return Math.toRadians(rotationYaw);
	}

}
