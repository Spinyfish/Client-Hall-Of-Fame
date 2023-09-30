package dev.eternal.client.util.player;

import dev.eternal.client.util.math.MathUtil;
import dev.eternal.client.util.math.RayCastUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.*;

import java.util.ArrayList;
import java.util.List;

public final class RotationUtil {

  private static final float PHI = 1.61803398875f;
  private static final float TAU = 6.28318530718f;
  public static final List<float[]> LAZY_AIM_OFFSETS = new ArrayList<>();

  static {
    for (float iter = 0, dst = 0; dst <= 70; iter++, dst = MathHelper.sqrt_float(iter)) {
      final float angle = PHI * TAU * iter;

      final float deltaX = MathHelper.sin(angle) * dst;
      final float deltaY = MathHelper.cos(angle) * dst;

      LAZY_AIM_OFFSETS.add(new float[]{deltaX, deltaY});
    }
  }

  /**
   * Calculates the needed rotations {yaw, pitch} to look at given vector
   *
   * @param from the initial eye vector
   * @param to   the vector to be looked at
   * @return the rotations
   * @see Entity
   */
  public static float[] getRotations(Vec3 from, Vec3 to) {
    final float diffX = (float) (from.xCoord - to.xCoord);
    final float diffY = (float) (from.yCoord - to.yCoord);
    final float diffZ = (float) (from.zCoord - to.zCoord);
    var diff = Math.hypot(diffX, diffZ);
    var rotationYaw = MathHelper.atan2(diffZ, diffX) * MathUtil.RAD_TO_DEG + 90.0F;
    var rotationPitch = MathHelper.atan2(diffY, diff) * MathUtil.RAD_TO_DEG;
    return new float[]{(float) rotationYaw, (float) rotationPitch};
  }

  /**
   * Calculates the closest position to the given vector in given BoundingBox
   *
   * @param from        the vector, from which the closest position should be calculated from
   * @param boundingBox the bounding box, in which the position can be found
   * @return the closest position
   */
  public static Vec3 getClosestPosition(Vec3 from, AxisAlignedBB boundingBox) {
    return new Vec3(
        MathHelper.clamp_double(from.xCoord, boundingBox.minX, boundingBox.maxX),
        MathHelper.clamp_double(from.yCoord, boundingBox.minY, boundingBox.maxY),
        MathHelper.clamp_double(from.zCoord, boundingBox.minZ, boundingBox.maxZ));
  }

  /**
   * Applies the standard mc mouse smoothing calculations
   *
   * @param angle the that the calculations should be applied to
   * @return the calculated smoothed angle
   */
  public static float performGCDFix(float angle) {
    float f = Minecraft.getMinecraft().gameSettings.mouseSensitivity * 0.6F + 0.2F;
    float sensMultiplier = f * f * f * 8.0F;

    return (float)
        ((float) (double) Math.round(angle / sensMultiplier * 0.15D)
            * sensMultiplier
            * 6.666666666666667);
  }

  /**
   * Predicts the position for given entity in n ticks
   *
   * @param entity the entity to predict the position for
   * @param ticks  the amount of ticks that should be predicted
   * @return the absolute predicted position
   */
  public static Vec3 predictEntityPosition(EntityLivingBase entity, float ticks) {
    double posX = entity.posX;
    double posY = entity.posY;
    double posZ = entity.posZ;

    double velX = entity.posX - entity.lastTickPosX;
    double velY = entity.posY - entity.lastTickPosY;
    double velZ = entity.posZ - entity.lastTickPosZ;

    for (int i = 0; i < MathHelper.floor_float(ticks); i++) {
      velY = (velY - 0.08D) * 0.9800000190734863D;

      final double newPosX = posX + velX;
      final double newPosY = posY + velY;
      final double newPosZ = posZ + velZ;

      final MovingObjectPosition mop =
          RayCastUtil.rayTraceBlocks(
              new Vec3(posX, posY + 0.1F, posZ), new Vec3(newPosX, newPosY, newPosZ));
      if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
        posY = (float) mop.hitVec.yCoord;
        velY = 0.0F;
      }

      posX = newPosX;
      posY += velY;
      posZ = newPosZ;
    }

    double t = ticks - Math.floor(ticks);
    posX += velX * t;
    posZ += velZ * t;

    return new Vec3(posX, posY, posZ);
  }

  /**
   * returns the closest possible rotations to the block given
   *
   * @param facing       the EnumFacing you want to place to
   * @param yaw          the initial yaw
   * @param pitch        the inital pitch
   * @param reach        the maximum distance the block can be away (redundant?)
   * @param targetBlock  the block you want to get the rotations for
   * @param partialTicks (for bypassing purposes)
   * @return the rotations in a float array [yaw, pitch]
   */
  public static float[] lazyAim(
      EnumFacing facing,
      float yaw,
      float pitch,
      float reach,
      BlockPos targetBlock,
      float partialTicks) {
    for (float[] offsets : LAZY_AIM_OFFSETS) {
      float offsetYaw = MathHelper.wrapAngleTo180_float(yaw + offsets[0]);
      float offsetPitch = MathHelper.clamp_float(pitch + offsets[1], -90, 90);
      MovingObjectPosition mop = RayCastUtil.rayCast(offsetYaw, offsetPitch, reach, partialTicks);

      if (mop != null
          && mop.sideHit != null
          && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
          && mop.getBlockPos() != null
          && mop.sideHit == facing) {

        return new float[]{offsetYaw, offsetPitch};
      }
    }
    return null;
  }

  private static final Minecraft MC = Minecraft.getMinecraft();

  public static float[] lazyAimIfBlockEqual(
      EnumFacing facing,
      float yaw,
      float pitch,
      float reach,
      BlockPos targetBlock,
      float partialTicks) {
    final int sampleCount = LAZY_AIM_OFFSETS.size();
    final float spacing = 1.0F;

    for (final float[] fibonacciOffset : LAZY_AIM_OFFSETS) {
      final float yawOffs = fibonacciOffset[0] * spacing;
      final float pitchOffs = fibonacciOffset[1] * spacing;

      yaw += yawOffs;
      pitch += pitchOffs;

      if (pitch > 90.0F || pitch < -90.0F) continue;

      final MovingObjectPosition mop =
          RayCastUtil.rayCast(
              yaw, pitch, MC.playerController.getBlockReachDistance(), partialTicks);
      if (mop != null
          && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
          && mop.sideHit == facing) {

        return new float[]{yaw, pitch};
      }
    }
    return null;
  }

  /**
   * returns the best rotations for an entity (aiming through small openings to get the best hit
   * results)
   *
   * @param entity
   * @param yaw    the initial yaw
   * @param pitch  the initial pitch
   * @param reach  the maximum distance the entity can be hit from
   * @return the rotations in a float array [yaw, pitch]
   */
  public static float[] lazyAim(
      Entity entity, float yaw, float pitch, float reach, float partialTicks) {
    for (float[] offsets : LAZY_AIM_OFFSETS) {
      final float offsetYaw = MathHelper.wrapAngleTo180_float(yaw + offsets[0]);
      final float offsetPitch = MathHelper.clamp_float(pitch + offsets[1], -90, 90);
      final MovingObjectPosition mop =
          RayCastUtil.rayCast(offsetYaw, offsetPitch, reach, partialTicks);
      if (mop != null
          && mop.hitVec != null
          && mop.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY
          && mop.entityHit != null
          && mop.entityHit == entity) {
        return new float[]{offsetYaw, offsetPitch};
      }
    }
    return new float[]{yaw, pitch};
  }
}
