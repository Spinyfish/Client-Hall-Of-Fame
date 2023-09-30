package de.fanta.utils;

import de.fanta.module.impl.world.Scaffold;
import de.fanta.module.impl.world.Scaffold.BlockData;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import optifine.MathUtils;

public class RotationUtil {
	public static Minecraft mc = Minecraft.getMinecraft();
	public static Rotation rotation;

	public static float[] getRotations(Entity entity) {
		double diffY;
		if (mc.isGamePaused())
			return new float[] { 0.0F, -90.0F };
		if (entity == null) {
			return null;
		}

		double diffX = entity.posX - mc.thePlayer.posX;
		double diffZ = entity.posZ - mc.thePlayer.posZ;

		if (entity instanceof EntityLivingBase) {

			EntityLivingBase elb = (EntityLivingBase) entity;

			diffY = elb.posY + elb.getEyeHeight() - 0.4D - mc.thePlayer.posY + mc.thePlayer.getEyeHeight();
		} else {
			diffY = (entity.boundingBox.minY + entity.boundingBox.maxY) / 2.0D - mc.thePlayer.posY
					+ mc.thePlayer.getEyeHeight();
		}

		double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
		float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
		float pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI);
		return new float[] { yaw, pitch };
	}

	

	public static Rotation mouseFix(float yaw, float pitch) {
		final float curYaw = rotation == null ? mc.thePlayer.rotationYaw : rotation.getYaw();
		final float curPitch = rotation == null ? mc.thePlayer.rotationPitch : rotation.getPitch();
		final float f = (float) (mc.gameSettings.mouseSensitivity * 0.6F + 0.2F);
		final float f2 = f * f * f * 1.2F;
		float nYaw = yaw - curYaw;
		nYaw -= nYaw % f2;
		yaw = curYaw + nYaw;

		float nPitch = pitch - curPitch;
		nPitch -= nPitch % f2;
		pitch = curPitch + nPitch;

		return new Rotation(yaw, MathHelper.clamp_int((int) pitch, -90, 90));
	}
	
	public static float getAngle(Entity entity) {
		double x = RenderUtil.interpolate(entity.posX, entity.lastTickPosX, 1.0) -  RenderUtil.interpolate(mc.thePlayer.posX, mc.thePlayer.lastTickPosX, 1.0);
		double z = RenderUtil.interpolate(entity.posZ, entity.lastTickPosZ, 1.0) -  RenderUtil.interpolate(mc.thePlayer.posZ, mc.thePlayer.lastTickPosZ, 1.0);
		float yaw = (float) (-(Math.toDegrees(Math.atan2(x, z))));
		return (float) (yaw - RenderUtil.interpolate((double) mc.thePlayer.rotationYaw, (double) mc.thePlayer.prevRotationYaw, 1.0));
	}
}
