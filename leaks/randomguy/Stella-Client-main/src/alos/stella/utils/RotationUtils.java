package alos.stella.utils;

import alos.stella.module.modules.combat.KillAura;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public final class RotationUtils {

    private static float[] serverAngles = new float[2];
    private static Minecraft mc = Minecraft.getMinecraft();

    private RotationUtils() {
        throw new java.lang.UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static Vec3 getVectorForRotation(float pitch, float yaw) {
        float f = MathHelper.cos((float) (Math.toRadians(-yaw) - (float) Math.PI));
        float f1 = MathHelper.sin((float) (Math.toRadians(-yaw) - (float) Math.PI));
        float f2 = -MathHelper.cos((float) Math.toRadians(-pitch));
        float f3 = MathHelper.sin((float) Math.toRadians(-pitch));
        return new Vec3(f1 * f2, f3, f * f2);
    }

    public static Vec3 getPositionEyes(float partialTicks) {
        return new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
    }


    	public static float[] getAngles(EntityLivingBase entity) {
    		if (entity == null)
    			return null;
    		final EntityPlayerSP thePlayer = mc.thePlayer;

    		final double diffX = entity.posX - thePlayer.posX,
    				diffY = entity.posY + entity.getEyeHeight() * 0.9 - (thePlayer.posY + thePlayer.getEyeHeight()),
    				diffZ = entity.posZ - thePlayer.posZ, dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ); // @on

    		final float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F,
    				pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI);

    		return new float[] { thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - thePlayer.rotationYaw),
    				thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - thePlayer.rotationPitch) };
    	}
    	public static float[] getCustomAngles(EntityLivingBase entity) {
    		if (entity == null)
    			return null;
    		final EntityPlayerSP thePlayer = mc.thePlayer;

    		final double diffX = entity.posX - thePlayer.posX,
    				diffY = entity.posY + entity.getEyeHeight() * 0.9 - (thePlayer.posY + thePlayer.getEyeHeight()),
    				diffZ = entity.posZ - thePlayer.posZ, dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ); // @on

    		final float yaw = (float) (Math.atan2(diffZ, diffX) * KillAura.rotyaw.get() / Math.PI) - 90.0F,
    				pitch = (float) -(Math.atan2(diffY, dist) * KillAura.rotpitch.get() / Math.PI);

    		return new float[] { thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - thePlayer.rotationYaw),
    				thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - thePlayer.rotationPitch) };
    	}
    private double getYawDifference(Entity e) {
        final float[] rotationPosition = RotationUtils.getAngles((EntityLivingBase) e);
        final float yaw = (int) rotationPosition[0], differenceYaw;
        differenceYaw = mc.thePlayer.rotationYaw > yaw ? mc.thePlayer.rotationYaw - yaw : yaw - mc.thePlayer.rotationYaw;
        return differenceYaw;
    }
}