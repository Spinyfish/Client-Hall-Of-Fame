package intentions.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class RotationUtils {
   private static Minecraft mc = Minecraft.getMinecraft();

   public static float[] getBowAngles(Entity var0) {
      double var1 = var0.posX - var0.lastTickPosX;
      double var3 = var0.posZ - var0.lastTickPosZ;
      double var5 = (double)Minecraft.getMinecraft().thePlayer.getDistanceToEntity(var0) % 0.8D;
      boolean var7 = var0.isSprinting();
      double var8 = var5 / 0.8D * var1 * (var7 ? 1.25D : 1.0D);
      double var10 = var5 / 0.8D * var3 * (var7 ? 1.25D : 1.0D);
      double var12 = var0.posX + var8 - Minecraft.getMinecraft().thePlayer.posX;
      double var14 = var0.posZ + var10 - Minecraft.getMinecraft().thePlayer.posZ;
      double var16 = Minecraft.getMinecraft().thePlayer.posY + (double)Minecraft.getMinecraft().thePlayer.getEyeHeight() - (var0.posY + (double)var0.getEyeHeight());
      double var18 = (double)Minecraft.getMinecraft().thePlayer.getDistanceToEntity(var0);
      float var20 = (float)Math.toDegrees(Math.atan2(var14, var12)) - 90.0F;
      float var21 = (float)Math.toDegrees(Math.atan2(var16, var18));
      return new float[]{var20, var21};
   }

	/**
	 * 
	 * @param vec
	 * @return index 0 = yaw | index 1 = pitch
	 */
	public static float[] getFacePos(Vec3 vec) {
		double diffX = vec.xCoord + 0.5 - Minecraft.getMinecraft().thePlayer.posX;
		double diffY = vec.yCoord + 0.5
				- (Minecraft.getMinecraft().thePlayer.posY + Minecraft.getMinecraft().thePlayer.getEyeHeight());
		double diffZ = vec.zCoord + 0.5 - Minecraft.getMinecraft().thePlayer.posZ;
		double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
		float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
		float pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI);
		return new float[] {
				Minecraft.getMinecraft().thePlayer.rotationYaw
						+ MathHelper.wrapAngleTo180_float(yaw - Minecraft.getMinecraft().thePlayer.rotationYaw),
				Minecraft.getMinecraft().thePlayer.rotationPitch
						+ MathHelper.wrapAngleTo180_float(pitch - Minecraft.getMinecraft().thePlayer.rotationPitch) };
	}
   
   public static float[] getRotations(EntityLivingBase var0) {
      double var1 = var0.posX;
      double var3 = var0.posY + (double)(var0.getEyeHeight() / 2.0F);
      double var5 = var0.posZ;
      return getRotationFromPosition(var1, var3, var5);
   }

   public static float[] getRotationFromPosition(double var0, double var2, double var4) {
      double var6 = var0 - Minecraft.getMinecraft().thePlayer.posX;
      double var8 = var4 - Minecraft.getMinecraft().thePlayer.posZ;
      double var10 = var2 - Minecraft.getMinecraft().thePlayer.posY - 1.2D;
      double var12 = (double)MathHelper.sqrt_double(var6 * var6 + var8 * var8);
      float var14 = (float)(Math.atan2(var8, var6) * 180.0D / 3.141592653589793D) - 90.0F;
      float var15 = (float)(-(Math.atan2(var10, var12) * 180.0D / 3.141592653589793D));
      return new float[]{var14, var15};
   }

   public static float[] doScaffoldRotations(Vec3d var0) {
      double var1 = var0.xCoord - mc.thePlayer.posX;
      double var3 = var0.yCoord - mc.thePlayer.boundingBox.minY;
      double var5 = var0.zCoord - mc.thePlayer.posZ;
      double var7 = (double)MathHelper.sqrt_double(var1 * var1 + var5 * var5);
      float var9 = (float)Math.toDegrees(Math.atan2(var5, var1));
      float var10 = (float)(-Math.toDegrees(Math.atan2(var3, var7)));
      return new float[]{mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(var9 - mc.thePlayer.rotationYaw), mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(var10 - mc.thePlayer.rotationPitch)};
   }
}