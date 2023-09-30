package haptic.util.rotations;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

public class RotationsUtils {
	
	private static Minecraft mc = Minecraft.getMinecraft();
	
	public static float[] getRotations(EntityLivingBase ent) {
		double x = ent.posX;
		double y = ent.posY + ent.getEyeHeight() / 2.0f;
		double z = ent.posZ;
		return RotationsUtils.getRotationFromPosition(x, y, z);
	}

	public static float[] getRotationFromPosition(double x, double y, double z) {
		double xDiff = x - Minecraft.getMinecraft().thePlayer.posX;
		double zDiff = z - Minecraft.getMinecraft().thePlayer.posZ;
		double yDiff = y - Minecraft.getMinecraft().thePlayer.posY - 1.2;

		double dist = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);
		float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0D / 3.141592653589793D) - 90.0F;
		float pitch = (float) -(Math.atan2(yDiff, dist) * 180.0D / 3.141592653589793D);
		return new float[] { yaw, pitch };
	}
}
