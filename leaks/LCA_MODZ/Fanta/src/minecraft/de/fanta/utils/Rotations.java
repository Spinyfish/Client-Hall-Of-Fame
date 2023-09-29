package de.fanta.utils;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import java.util.Random;

import de.fanta.Client;
import de.fanta.module.impl.combat.Killaura;
import de.fanta.module.impl.world.Scaffold;
import de.fanta.module.impl.world.Scaffold.BlockData;
import de.fanta.setting.settings.CheckBox;

public class Rotations {

	public static float yaw;
	public static float pitch;
	public static boolean RotationInUse;
	static Minecraft mc = Minecraft.getMinecraft();
	Random random = new Random();
	float ran = 0;
	static TimeUtil time = new TimeUtil();
	public static float friction = 0;
	public static float strafe = 0;
	public static float forward = 0;
	public static float f1 = 0;
	public static float f2 = 0;

	public static int getRotation(double before, float after) {

		while (before > 360.0D) {
			before -= 360.0D;
		}
		while (before < 0.0D) {
			before += 360.0D;
		}
		while (after > 360.0F) {
			after -= 360.0F;
		}
		while (after < 0.0F) {
			after += 360.0F;
		}
		if (before > after) {
			if (before - after > 180.0D) {
				return 1;
			}
			return -1;
		}
		if (after - before > 180.0D) {
			return -1;
		}
		return 1;
	}

	public static boolean setYaw(float y, float speed) {

		setRotation(yaw, pitch);
		if (speed >= 360.0F) {
			yaw = y;
			return true;
		}

		if ((isInRange(yaw, y, speed)) || (speed >= 360.0F)) {
			yaw = y;
			return true;
		}
		if (getRotation(yaw, y) < 0) {
			yaw = yaw - speed;
		} else {
			yaw = yaw + speed;
		}
		return false;
	}

	public static boolean isInRange(double before, float after, float max) {
		while (before > 360.0D)
			before -= 360.0D;
		while (before < 0.0D)
			before += 360.0D;
		while (after > 360.0F)
			after -= 360.0F;
		while (after < 0.0F)
			after += 360.0F;
		if (before > after) {
			if ((before - after > 180.0D) && (360.0D - before - after <= max))
				return true;
			return before - after <= max;
		} else {
			if ((after - before > 180.0D) && (360.0F - after - before <= max))
				return true;
			return after - before <= max;
		}
	}

	public static boolean setPitch(float p, float speed) {

		if (p > 90.0F)
			p = 90.0F;
		else if (p < -90.0F)
			p = -90.0F;

		if ((Math.abs(pitch - p) <= speed) || (speed >= 360.0F)) {
			pitch = p;
			return false;
		}

		if (p < pitch)
			pitch = pitch - speed;
		else
			pitch = pitch + speed;

		return true;
	}

	public static void setRotation(float y, float p) {
		if (p > 90.0F)
			p = 90.0F;
		else if (p < -90.0F)
			p = -90.0F;

		yaw = y;
		pitch = p;
		RotationInUse = true;
	}

//    public static float[] Intavee(final EntityPlayerSP player, final Entity target) {
//    	if (FriendSystem.isFriendString(Killaura.kTarget.getName()) )
//			return null;
//    	 final float RotationPitch2 = (float) MathHelper.getRandomDoubleInRange(new Random(), 50, 90);
//       
//        final float RotationPitch = (float) MathHelper.getRandomDoubleInRange(new Random(), 70, 90);
//        final float RotationYaw = (float) MathHelper.getRandomDoubleInRange(new Random(), RotationPitch, 90);
//        final double posX = target.posX - player.posX;
//        final float RotationY2 = (float) MathHelper.getRandomDoubleInRange(new Random(), 175, 180);
//        final float RotationY4 = (float) MathHelper.getRandomDoubleInRange(new Random(), -0.2, 0.2);
//        final float RotationY3 = (float) MathHelper.getRandomDoubleInRange(new Random(), -0.2, 0.2);
//        final double posY = target.posY + target.getEyeHeight()
//                - (player.posY + player.getAge() + player.getEyeHeight() + RotationY3);
//        final double posZ = target.posZ - player.posZ;
//        final double var14 = MathHelper.sqrt_double(posX * posX + posZ * posZ);
//        float yaw = (float) (Math.atan2(posZ, posX) * RotationY2 / Math.PI) - RotationYaw;
//        float pitch = (float) -(Math.atan2(posY, var14) * RotationPitch2 / Math.PI);
//        float deltaYaw = yaw - Rotations.yaw;
//        float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
//        float f2 = f * f * f * 1.2F;
//        deltaYaw -= deltaYaw % f2;
//        Rotations.yaw = Rotations.yaw + deltaYaw;
//        float deltaPitch = pitch - Rotations.pitch;
//        deltaPitch -= deltaPitch % f2;
//        Rotations.pitch = pitch + deltaPitch;
//        return new float[]{MathHelper.wrapAngleTo180_float(yaw), pitch};
//    }
//    public static float[] Intavee(final EntityPlayerSP player, final EntityLivingBase target) {
//       // final float RotationPitch = (float) MathHelper.getRandomDoubleInRange(new Random(), 170, 180);
//        final float RotationPitch2 = (float) MathHelper.getRandomDoubleInRange(new Random(), 175, 180);
//        //final float RotationYaw = (float) MathHelper.getRandomDoubleInRange(new Random(), RotationPitch, 94);
//        final float yaw2 = (float) MathHelper.getRandomDoubleInRange(new Random(), 3.13, Math.PI);
//        final double posX = target.posX - player.posX;
//        final float RotationY2 = (float) MathHelper.getRandomDoubleInRange(new Random(), 178, 180);
//        final float RotationY4 = (float) MathHelper.getRandomDoubleInRange(new Random(), 0.2, 0.3);
//        final float RotationY3 = (float) MathHelper.getRandomDoubleInRange(new Random(), RotationY4, 0.1);
//        final double posY = target.posY + target.getEyeHeight() - (player.posY + player.getAge() + player.getEyeHeight());
//        final double posZ = target.posZ - player.posZ;
//        final double var14 = MathHelper.sqrt_double(posX * posX + posZ * posZ);
//        float yaw = (float) (Math.atan2(posZ, posX) * 180 / yaw2) - 	90;
//
//        float pitch = (float) -(Math.atan2(posY, var14) * 180 / Math.PI);
//        final float f2 = Minecraft.getMinecraft().gameSettings.mouseSensitivity * 0.6F + 0.2F;
//        final float f3 = f2 * f2 * f2 * 1.2F;
//        yaw -= yaw % f3;
//        pitch -= pitch % (f3 * f2);
//        return new float[]{yaw, MathHelper.clamp_float(pitch, -90, 90)};
//    }

	public static float[] Intaveee(final EntityPlayerSP player, final EntityLivingBase target) {
		// final float RotationPitch = (float) MathHelper.getRandomDoubleInRange(new
		// Random(), 170, 180);
		final float RotationPitch2 = (float) MathHelper.getRandomDoubleInRange(new Random(), 140, 180);
		// final float RotationYaw = (float) MathHelper.getRandomDoubleInRange(new
		// Random(), RotationPitch, 94);
		final float yaw2 = (float) MathHelper.getRandomDoubleInRange(new Random(), 3.13, Math.PI);
		final double posX = target.posX - player.posX;
		final float RotationY2 = (float) MathHelper.getRandomDoubleInRange(new Random(), 170, 180);

		final float Rotationss = (float) MathHelper.getRandomDoubleInRange(new Random(), 87, 88);
		final float Rotationsss = (float) MathHelper.getRandomDoubleInRange(new Random(), Rotationss, 90);

		final float RotationY4 = (float) MathHelper.getRandomDoubleInRange(new Random(), 0.2, 0.3);
		final float RotationY3 = (float) MathHelper.getRandomDoubleInRange(new Random(), RotationY4, 0.1);
		final double posY = target.posY + target.getEyeHeight()
				- (player.posY + player.getAge() + player.getEyeHeight());
		final double posZ = target.posZ - player.posZ;
		final double var14 = MathHelper.sqrt_double(posX * posX + posZ * posZ);
		float yaw = (float) (Math.atan2(posZ, posX) * 180 / yaw2) - Rotationsss;

		float pitch = (float) -(Math.atan2(posY, var14) * RotationPitch2 / Math.PI);
		final float f2 = Minecraft.getMinecraft().gameSettings.mouseSensitivity * 0.6F + 0.2F;
		final float f3 = f2 * f2 * f2 * 1.2F;
		yaw -= yaw % f3;
		pitch -= pitch % (f3 * f2);
		return new float[] { yaw, MathHelper.clamp_float(pitch, -90, 90) };
	}

	public static float[] Intavee(final EntityPlayerSP player, final EntityLivingBase target) {
		final float RotationPitch = (float) MathHelper.getRandomDoubleInRange(new Random(), 90, 92);
		final float RotationYaw = (float) MathHelper.getRandomDoubleInRange(new Random(), RotationPitch, 94);
		final double posX = target.posX - player.posX;
		final float RotationY2 = (float) MathHelper.getRandomDoubleInRange(new Random(), 175, 180);
		final float RotationY4 = (float) MathHelper.getRandomDoubleInRange(new Random(), 0.2, 0.3);
		final float RotationY3 = (float) MathHelper.getRandomDoubleInRange(new Random(), RotationY4, 0.1);
		final double posY = target.posY + target.getEyeHeight()
				- (player.posY + player.getAge() + player.getEyeHeight() + RotationY3);
		final double posZ = target.posZ - player.posZ;
		final double var14 = MathHelper.sqrt_double(posX * posX + posZ * posZ);
		float yaw = (float) (Math.atan2(posZ, posX) * RotationY2 / Math.PI) - RotationYaw;
		float pitch = (float) -(Math.atan2(posY, var14) * RotationY2 / Math.PI);
		final float f2 = Minecraft.getMinecraft().gameSettings.mouseSensitivity * 0.6F + 0.2F;
		final float f3 = f2 * f2 * f2 * 1.2F;
		yaw -= yaw % f3;
		pitch -= pitch % (f3 * f2);
		// return new float[]{yaw, MathHelper.clamp_float(pitch, -90, 90)};
		return new float[] { yaw, pitch };
	}

	public static float[] getKillAuraRotsTest(final EntityPlayerSP player, final EntityLivingBase target) {
		final float RotationPitch = (float) MathHelper.getRandomDoubleInRange(new Random(), 170, 180);
		final float RotationPitch2 = (float) MathHelper.getRandomDoubleInRange(new Random(), 175, 180);
		final float RotationYaw = (float) MathHelper.getRandomDoubleInRange(new Random(), RotationPitch, 94);
		final float yaw2 = (float) MathHelper.getRandomDoubleInRange(new Random(), 3.13, Math.PI);
		final double posX = target.posX - player.posX;
		final float RotationY2 = (float) MathHelper.getRandomDoubleInRange(new Random(), 178, 180);
		final float RotationY4 = (float) MathHelper.getRandomDoubleInRange(new Random(), 1, 2);
		final float RotationY3 = (float) MathHelper.getRandomDoubleInRange(new Random(), RotationY4, 0.1);
		final double posY = target.posY + target.getEyeHeight()
				- (player.posY + player.getAge() + player.getEyeHeight());
		final double posZ = target.posZ - player.posZ;
		final double var14 = MathHelper.sqrt_double(posX * posX + posZ * posZ);
		float yaw = (float) (Math.atan2(posZ, posX) * 180 / Math.PI) - 90;
		float pitch = (float) -(Math.atan2(posY, var14) * 180 / Math.PI - RotationY3);
		final float f2 = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
		final float f3 = f2 * f2 * f2 * 1.2F;
		yaw -= yaw % f3;
		pitch -= pitch % (f3 * f2);
		return new float[] { yaw, pitch };
	}
	
	
	public static float[] basicRotation(final EntityLivingBase target, float currentYaw, float currentPitch) {
		Vec3 positionEyes = mc.thePlayer.getPositionEyes(1.0F);
        float f11 = target.getCollisionBorderSize();
        double ex = MathHelper.clamp_double(positionEyes.xCoord, target.getEntityBoundingBox().minX - f11, target.getEntityBoundingBox().maxX + f11);
        double ey = MathHelper.clamp_double(positionEyes.yCoord, target.getEntityBoundingBox().minY - f11, target.getEntityBoundingBox().maxY + f11);
        double ez = MathHelper.clamp_double(positionEyes.zCoord, target.getEntityBoundingBox().minZ - f11, target.getEntityBoundingBox().maxZ + f11);
        double x = ex - mc.thePlayer.posX;
        double y = ey - (mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight());
        double z = ez - mc.thePlayer.posZ;
        float calcYaw = (float) ((MathHelper.func_181159_b(z, x) * 180.0D / Math.PI) - 90.0F);
        float calcPitch = (float) (-((MathHelper.func_181159_b(y, MathHelper.sqrt_double(x * x + z * z)) * 180.0D / Math.PI)));


        float yaw = updateRotation(currentYaw, calcYaw, 180);
        float pitch = updateRotation(currentPitch, calcPitch, 180);
        final float f2 = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
		final float f3 = f2 * f2 * f2 * 1.2F;
		yaw -= yaw % f3;
		pitch -= pitch % (f3 * f2);
        return new float[] {yaw, pitch};
	}
	
	
	
	public static float[] getNewKillAuraRots(final EntityPlayerSP player, final EntityLivingBase target, float currentYaw, float currentPitch) {
		Vec3 positionEyes = player.getPositionEyes(1.0F);
        float f11 = target.getCollisionBorderSize();
        double ex = MathHelper.clamp_double(positionEyes.xCoord, target.getEntityBoundingBox().minX - f11, target.getEntityBoundingBox().maxX + f11);
        double ey = MathHelper.clamp_double(positionEyes.yCoord, target.getEntityBoundingBox().minY - f11, target.getEntityBoundingBox().maxY + f11);
        double ez = MathHelper.clamp_double(positionEyes.zCoord, target.getEntityBoundingBox().minZ - f11, target.getEntityBoundingBox().maxZ + f11);
        double x = ex - player.posX;
        double y = ey - (player.posY + (double) player.getEyeHeight());
        double z = ez - player.posZ;
        float calcYaw = (float) ((MathHelper.func_181159_b(z, x) * 180.0D / Math.PI) - 90.0F);
        float calcPitch = (float) (-((MathHelper.func_181159_b(y, MathHelper.sqrt_double(x * x + z * z)) * 180.0D / Math.PI)));
        float yawSpeed =   40;
        float pitchSpeed = 180; 
        float yaw = updateRotation(currentYaw, calcYaw, yawSpeed);  
        float pitch = updateRotation(currentPitch, calcPitch, pitchSpeed);
        double diffYaw = MathHelper.wrapAngleTo180_float(calcYaw - currentYaw);
        double diffPitch = MathHelper.wrapAngleTo180_float(calcPitch - currentPitch);
        if((!(-yawSpeed <= diffYaw) || !(diffYaw <= yawSpeed)) || (!(-pitchSpeed <= diffPitch) || !(diffPitch <= pitchSpeed))) {
        	yaw += RandomUtil.nextFloat(1, 2) * Math.sin(pitch * Math.PI);
            pitch += RandomUtil.nextFloat(1, 2) * Math.sin(yaw * Math.PI);
        }
        final float f2 = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
		final float f3 = f2 * f2 * f2 * 1.2F;
		yaw -= (yaw % f3);
		pitch -=(pitch % (f3 * f2));
		return new float[] {yaw, pitch };
	}
	
	
	
	public static float updateRotation(float p_70663_1_, float p_70663_2_, float p_70663_3_) {
        float f = MathHelper.wrapAngleTo180_float(p_70663_2_ - p_70663_1_);
        if (f > p_70663_3_) {
            f = p_70663_3_;
        }

        if (f < -p_70663_3_) {
            f = -p_70663_3_;
        }

        return p_70663_1_ + f;
    }
	

	/*
	 * public static float[] testScaffoldRots(BlockPos pos) {
	 * 
	 * double x = pos.getX() + 0.2 +
	 * mc.thePlayer.getHorizontalFacing().getOpposite().getFrontOffsetX(); double y
	 * = pos.getY() + 5.35 +
	 * mc.thePlayer.getHorizontalFacing().getOpposite().getFrontOffsetY(); double z
	 * = pos.getZ() + 0.2 +
	 * mc.thePlayer.getHorizontalFacing().getOpposite().getFrontOffsetZ();
	 * 
	 * double diffX = x - mc.thePlayer.posX; double diffY = y - mc.thePlayer.posY +
	 * mc.thePlayer.getEyeHeight(); double diffZ = z - mc.thePlayer.posZ; float yaw
	 * = (float) (Math.toDegrees(Math.atan2(diffZ, diffX)) - 90); float pitch =
	 * (float) Math.toDegrees(Math.atan2(diffY, Math.sqrt(diffX * diffX + diffZ *
	 * diffZ)));
	 * 
	 * return new float[] {MathHelper.wrapAngleTo180_float(yaw),
	 * MathHelper.wrapAngleTo180_float(pitch)}; }
	 * 
	 */

	public static float[] rotationrecode7(BlockData blockData) {
		double x = blockData.getPos().getX() + 0.5D - mc.thePlayer.posX;
		double z = blockData.getPos().getZ() + 0.5D - mc.thePlayer.posZ;
		double y = blockData.getPos().getY() + 0.6D;
		double ymax = mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - y;
		double allmax = MathHelper.sqrt_double(x * x + z * z);
		float yaw = (float) (Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
		float pitch = (float) (Math.atan2(ymax, allmax) * 180.0D / Math.PI);
		if (yaw < 0.0F)
			yaw += 360.0F;
		final float f2 = Minecraft.getMinecraft().gameSettings.mouseSensitivity * 0.6F + 0.2F;
		final float f3 = f2 * f2 * f2 * 1.2F;
		yaw -= yaw % f3;
		pitch -= pitch % (f3 * f2);
		return new float[] { yaw, MathHelper.clamp_float(pitch, 82.5F, 90) };

	}

	public static float[] rotationrecode2(Scaffold.BlockData blockdata) {
		double x = (double) blockdata.getPos().getX() + 0.5D - mc.thePlayer.posX
				+ (double) blockdata.getFacing().getFrontOffsetX() / 2.0D;
		double z = (double) blockdata.getPos().getZ() + 0.5D - mc.thePlayer.posZ
				+ (double) blockdata.getFacing().getFrontOffsetZ() / 2.0D;
		double y = (double) blockdata.getPos().getY();
		double ymax = mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight() - y;
		double angle = (double) MathHelper.sqrt_double(x * x + z * z);
		float yawAngle = (float) ((float) (MathHelper.func_181159_b(z, x) * 180.0D / Math.PI) - 90.0F);
        float pitchAngle = (float) -(MathHelper.func_181159_b(y, angle) * 180.0D / Math.PI);
		
		float yaw = updateRotation(Rotations.yaw, yawAngle, 180F);
		float pitch = updateRotation(Rotations.pitch, pitchAngle, 180F);
		final float f2 = Minecraft.getMinecraft().gameSettings.mouseSensitivity * 0.6F + 0.2F;
		final float f3 = f2 * f2 * f2 * 1.2F;
		yaw -= (yaw % f3);
		pitch -=(pitch % (f3 * f2));
		return new float[] { yaw, MathHelper.clamp_float(pitch, 81, 82) };
	}

	public static float[] testScaffoldRots(BlockPos pos) {

		double x = pos.getX() + 0.5 - mc.thePlayer.posX
				+ mc.thePlayer.getHorizontalFacing().getOpposite().getFrontOffsetX() / 2.0D;
		double y = pos.getY() - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight()) + 0.6;
		double z = pos.getZ() + 0.5 - mc.thePlayer.posZ
				+ mc.thePlayer.getHorizontalFacing().getOpposite().getFrontOffsetZ() / 2.0D;

		double diffX = x - mc.thePlayer.posX;
		double diffY = y - mc.thePlayer.posY + mc.thePlayer.getEyeHeight();
		double diffZ = z - mc.thePlayer.posZ;

		double allmax = MathHelper.sqrt_double(x * x + z * z);
		float yaw = (float) (Math.atan2(z, x) * 180.0D / Math.PI) - 85.0F;
		float pitch = (float) (-Math.atan2(y, allmax) * 180.0D / Math.PI);
		if (yaw < 0.0F)
			yaw += 360.0F;
		return new float[] { yaw, pitch };
	}

	public static float getPitch() {
		if (RotationInUse)
			return pitch;
		return Minecraft.getMinecraft().thePlayer.rotationPitch;
	}

	public static float getYaw() {
		if (RotationInUse)
			return yaw;
		return Minecraft.getMinecraft().thePlayer.rotationYaw;
	}

	public static float[] getRotationsForPoint(Vec3 point) {
		if (point == null)
			return null;
		double diffX = point.xCoord - Minecraft.getMinecraft().thePlayer.posX;
		double diffY = point.yCoord
				- (Minecraft.getMinecraft().thePlayer.posY + Minecraft.getMinecraft().thePlayer.getEyeHeight() - 0.6);
		double diffZ = point.zCoord - Minecraft.getMinecraft().thePlayer.posZ;
		double dist = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
		float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F;
		float pitch = (float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI);
		final float f2 = Minecraft.getMinecraft().gameSettings.mouseSensitivity * 0.6F + 0.2F;
		final float f3 = f2 * f2 * f2 * 1.2F;
		yaw -= yaw % f3;
		pitch -= pitch % (f3 * f2);
		return new float[] { MathHelper.wrapAngleTo180_float(yaw), MathHelper.wrapAngleTo180_float(pitch) };
	}

	public static Vec3 getRandomCenter(AxisAlignedBB bb) {
		return new Vec3(bb.minX + (bb.maxX - bb.minX) * 0.5 * Math.random(),
				bb.minY + (bb.maxY - bb.minY) * 1 * Math.random(), bb.minZ + (bb.maxZ - bb.minZ) * 0.5 * Math.random());
	}

	public static Vec3 getCenter(AxisAlignedBB bb) {
		return new Vec3(bb.minX + (bb.maxX - bb.minX) * 0.5, bb.minY + (bb.maxY - bb.minY) * 0.5,
				bb.minZ + (bb.maxZ - bb.minZ) * 0.5);
	}

	public static Vec3 getCenter2(AxisAlignedBB bb) {
		return new Vec3(bb.minX + (bb.maxX - bb.minX) * 0.5 - 5, bb.minY + (bb.maxY - bb.minY) * 0.1,
				bb.minZ + (bb.maxZ - bb.minZ) * 0.5);
	}

	public static float getYawToPoint(Vec3 p) {
		float[] rotations = getRotationsForPoint(p);

		return rotations[0];
	}



	public static float getPitchToPoint(Vec3 p) {
		float[] rotations = getRotationsForPoint(p);
		return rotations[1];
	}

	public void onTick() {
		if (!RotationInUse) {
			yaw = mc.thePlayer.rotationYaw;
			pitch = mc.thePlayer.rotationPitch;
		}
	}

}
