package store.femboy.spring.impl.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.*;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public final class RotationUtil {
    static Minecraft mc = Minecraft.getMinecraft();
    private static final Random random = new Random();

    private double lastXOffset;
    private double lastYOffset;
    private double lastZOffset;

    private int streak;
    private int streakAttempt;
    private boolean decreasing;

    /**
     * Attempts to look at an {@code EntityLivingBase}'s head
     *
     * @param destinationRotations The rotations to use.
     */
    public void turnToEntityClient(Rotation destinationRotations) {
        mc.thePlayer.rotationYaw = destinationRotations.getRotationYaw();
        mc.thePlayer.rotationPitch = destinationRotations.getRotationPitch();
    }

    /**
     * Tries to get a {@code Rotation} for an {@code EntityLivingBase}
     *
     * @param entity The entity to {@code EntityLivingBase} at.
     * @return The {@code Rotation} for this {@code Entity}
     */
    public Rotation getRotationsRandom(EntityLivingBase entity, boolean randomizedAim) {
        ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();

        double randomXZ = randomizedAim ? threadLocalRandom.nextDouble(-0.05, 0.1) : 0;
        double randomY = randomizedAim ? threadLocalRandom.nextDouble(-0.05, 0.1) : 0;

        double x = entity.posX + randomXZ;
        double y = entity.posY + (entity.getEyeHeight() / 2.05) + randomY;
        double z = entity.posZ + randomXZ;

        return attemptFacePosition(x, y, z);
    }

    public Rotation getAdvancedRotations(EntityLivingBase entity) {
        AxisAlignedBB bb = entity.getEntityBoundingBox();

        double posX = bb.maxX - ((bb.maxX - bb.minX) / 2);
        double posY = bb.maxY - ((bb.maxY - bb.minY) / 2);
        double posZ = bb.maxZ - ((bb.maxZ - bb.minZ) / 2);

        double randomizedOffsetX = MathUtil.getRandInt((int) -0.05, (int) 0.05);
        double randomizedOffsetY = MathUtil.getRandInt((int) -0.08, (int) 0.08);
        double randomizedOffsetZ = MathUtil.getRandInt((int) -0.05, (int) 0.05);

        return attemptFacePosition(posX + randomizedOffsetX, posY + randomizedOffsetY, posZ + randomizedOffsetZ);
    }

    public static Rotation getSmartRotations(Entity entity) {
        double diffX = entity.posX - mc.thePlayer.posX;
        double diffZ = entity.posZ - mc.thePlayer.posZ;
        double dist = Math.hypot(diffX, diffZ);

        double diffY;

        if (entity instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
            diffY = entityLivingBase.posY + entityLivingBase.getEyeHeight() * 0.9 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        } else {
            diffY = (entity.getEntityBoundingBox().minY + entity.getEntityBoundingBox().maxY) / 2.0D
                    - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        }

        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F,
                pitch = MathHelper.clamp_float((float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI), -90.0F, 90.0F);

        return new Rotation(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ,
                mc.thePlayer.rotationYaw + wrapAngle(yaw - mc.thePlayer.rotationYaw),
                mc.thePlayer.rotationPitch + wrapAngle(pitch - mc.thePlayer.rotationPitch));
    }

    /**
     * Wraps the specified angle between -180 and 180
     *
     * @param angle - Input angle
     * @return The wrapped angle
     * @author Mojang
     */
    public static float wrapAngle(float angle) {

        angle %= 360.0F;

        if (angle >= 180.0F) {
            angle -= 360.0F;
        }

        if (angle < -180.0F) {
            angle += 360.0F;
        }

        return angle;
    }


    /**
     * Attempts to get rotations to aim a perfect bow shot for this {@code Entity}
     *
     * @param entity The {@code Entity} to get rotations
     * @return The predicted rotations for this entity
     */
    public Rotation getBowAngles(Entity entity) {
        double xDelta = entity.posX - entity.lastTickPosX;
        double zDelta = entity.posZ - entity.lastTickPosZ;

        double distance = mc.thePlayer.getDistanceToEntity(entity) % .8;
        boolean sprint = entity.isSprinting();

        double xMulti = distance / .8 * xDelta * (sprint ? 1.45 : 1.3);
        double zMulti = distance / .8 * zDelta * (sprint ? 1.45 : 1.3);

        double x = entity.posX + xMulti - mc.thePlayer.posX;
        double y = mc.thePlayer.posY + mc.thePlayer.getEyeHeight()
                - (entity.posY + entity.getEyeHeight());
        double z = entity.posZ + zMulti - mc.thePlayer.posZ;

        double distanceToEntity = mc.thePlayer.getDistanceToEntity(entity);

        float yaw = (float) Math.toDegrees(Math.atan2(z, x)) - 90;
        float pitch = (float) Math.toDegrees(Math.atan2(y, distanceToEntity));

        return new Rotation(x, y, z, yaw, pitch);
    }

    /**
     * Tries to get a {@code Rotation} for the specified coordinates
     *
     * @param x The X coordinate
     * @param y The Y coordinate
     * @param z The Z coordinate
     * @return The rotations for the specified coordinates
     */
    public Rotation attemptFacePosition(double x, double y, double z) {
        double xDiff = x - mc.thePlayer.posX;
        double yDiff = y - mc.thePlayer.posY - mc.thePlayer.getEyeHeight();
        double zDiff = z - mc.thePlayer.posZ;

        double dist = Math.hypot(xDiff, zDiff);

        float yaw = (float) (Math.atan2(zDiff, xDiff) * 180 / Math.PI) - 90;
        float pitch = (float) -(Math.atan2(yDiff, dist) * 180 / Math.PI);

        return new Rotation(x, y, z, yaw, pitch);
    }

    public static Rotation getScaffoldRotations(final BlockPos position) {
        double direction = direction();
        double posX = -Math.sin(direction) * 0.5F;
        double posZ = Math.cos(direction) * 0.5F;

        double x = position.getX() - mc.thePlayer.posX - posX;
        double y = position.getY() - mc.thePlayer.prevPosY - mc.thePlayer.getEyeHeight();
        double z = position.getZ() - mc.thePlayer.posZ - posZ;

        double distance = Math.hypot(x, z);

        float yaw = (float) (Math.atan2(z, x) * 180.0D / Math.PI - 90.0F);
        float pitch = (float) -(Math.atan2(y, distance) * 180.0D / Math.PI);

        return new Rotation(x, y, z, mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw), mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch));
    }

    private static double direction() { // Credits: Hideri
        float rotationYaw = mc.thePlayer.rotationYaw;
        if (mc.thePlayer.movementInput.moveForward < 0.0F)
            rotationYaw += 180.0F;
        float forward = 1.0F;
        if (mc.thePlayer.movementInput.moveForward < 0.0F)
            forward = -0.5F;
        else if (mc.thePlayer.movementInput.moveForward > 0.0F)
            forward = 0.5F;
        if (mc.thePlayer.movementInput.moveStrafe > 0.0F)
            rotationYaw -= 90.0F * forward;
        if (mc.thePlayer.movementInput.moveStrafe < 0.0F)
            rotationYaw += 90.0F * forward;
        return Math.toRadians(rotationYaw);
    }

    public static float[] doDortRotations(Entity entity) {
        double diffX = entity.posX - mc.thePlayer.posX, diffY;
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
            diffY = entityLivingBase.posY + entityLivingBase.getEyeHeight() * 0.9
                    - (mc.thePlayer.posY
                    + mc.thePlayer.getEyeHeight());
        } else {
            diffY = (entity.getEntityBoundingBox().minY + entity.getEntityBoundingBox().maxY) / 2.0D
                    - (mc.thePlayer.posY
                    + mc.thePlayer.getEyeHeight());
        }

        double diffZ = entity.posZ - mc.thePlayer.posZ, dist = Math.hypot(diffX, diffZ);

        float sensitivity = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        float gcd = sensitivity * sensitivity * sensitivity * 1.1F;

        float yawRand = random.nextBoolean() ? -RandomUtils.nextFloat(0.0F, 3.0F) : RandomUtils.nextFloat(0.0F, 3.0F);
        float pitchRand = random.nextBoolean() ? -RandomUtils.nextFloat(0.0F, 3.0F) : RandomUtils.nextFloat(0.0F, 3.0F);

        float yaw = ((float) (Math.atan2(diffZ, diffX) * 180.0D / Math.PI) - 90.0F) + yawRand;
        float pitch = MathHelper.clamp_float(((float) -(Math.atan2(diffY, dist) * 180.0D / Math.PI)) + pitchRand + mc.thePlayer.getDistanceToEntity(entity) * 1.25F, -90.0F, 90.0F);

        if (mc.thePlayer.ticksExisted % 2 == 0) {
            pitch = MathHelper.clamp_float(pitch + (random.nextBoolean() ? RandomUtils.nextFloat(2.0F, 8.0F) : -RandomUtils.nextFloat(2.0F, 8.0F)), -90.0F, 90.0F);
        }

        pitch -= pitch % gcd;
        yaw -= yaw % gcd;

        return new float[]{
                mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw),
                mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch)
        };
    }

    public static EntityLivingBase rayCast(float yaw, float pitch, double distance) {
        if (mc.theWorld == null || mc.thePlayer == null) {
            return null;
        }

        Vec3 position = mc.thePlayer.getPositionEyes(mc.timer.renderPartialTicks);
        Vec3 lookVector = mc.thePlayer.getVectorForRotation(pitch, yaw);

        double reachDistance = distance;
        Entity pointedEntity = null;

        List<Entity> var5 = mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().addCoord(lookVector.xCoord * mc.playerController.getBlockReachDistance(), lookVector.yCoord * mc.playerController.getBlockReachDistance(), lookVector.zCoord * mc.playerController.getBlockReachDistance()).expand(reachDistance, reachDistance, reachDistance));
        for (Entity currentEntity : var5) {
            if (!currentEntity.canBeCollidedWith()) {
                continue;
            }

            float borderSize = currentEntity.getCollisionBorderSize();
            AxisAlignedBB bb = currentEntity.getEntityBoundingBox();
            MovingObjectPosition objPosition = bb.expand(borderSize, borderSize, borderSize)
                    .contract(0.1, 0.1, 0.1).calculateIntercept(position,
                            position.addVector(lookVector.xCoord * reachDistance, lookVector.yCoord * reachDistance
                                    , lookVector.zCoord * reachDistance)
                    );

            if (objPosition != null) {
                double range = position.distanceTo(objPosition.hitVec);
                if (range < reachDistance) {
                    if (currentEntity == mc.thePlayer.ridingEntity && reachDistance == 0.0D) {
                        pointedEntity = currentEntity;
                    } else {
                        pointedEntity = currentEntity;
                        reachDistance = range;
                    }
                }
            }
        }

        if ((pointedEntity instanceof EntityLivingBase)) {
            return (EntityLivingBase) pointedEntity;
        }

        return null;
    }

    public static float clampAngle(float angle) {
        angle = angle % 360F;

        while (angle <= -180) {
            angle = angle + 180;
        }

        while (angle > 180) {
            angle = angle - 180;
        }
        return angle;
    }

    public static double preciseRound(double value, double precision) {
        double scale = Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

    public float getYaw(Vec3 to) {
        float x = (float) (to.xCoord - mc.thePlayer.posX);
        float z = (float) (to.zCoord - mc.thePlayer.posZ);
        float var1 = (float) (StrictMath.atan2(z, x) * 180.0D / StrictMath.PI) - 90.0F;
        float rotationYaw = mc.thePlayer.rotationYaw;
        return rotationYaw + MathHelper.wrapAngleTo180_float(var1 - rotationYaw);
    }
}
