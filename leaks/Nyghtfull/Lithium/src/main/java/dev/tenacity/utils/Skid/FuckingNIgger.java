package dev.tenacity.utils.Skid;
import net.minecraft.client.Minecraft;
import net.minecraft.util.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import java.util.Comparator;

import java.util.Collections;
import java.util.ArrayList;
import net.minecraft.util.BlockPosition;
import net.minecraft.entity.EntityLivingBase;
import java.util.concurrent.ThreadLocalRandom;

import net.minecraft.entity.Entity;

public class FuckingNIgger {
    public static double ACCURATE_ROTATION_YAW_LEVEL;
    public static double ACCURATE_ROTATION_YAW_VL;
    public static double ACCURATE_ROTATION_PITCH_LEVEL;
    public static double ACCURATE_ROTATION_PITCH_VL;
    public static double ACCURATE_ROTATION_YAW_LEVEL1;
    public static double ACCURATE_ROTATION_YAW_VL1;
    public static double ACCURATE_ROTATION_PITCH_LEVEL1;
    public static double ACCURATE_ROTATION_PITCH_VL1;
    private double lastX;
    private double lastY;
    private double lastZ;
    private Entity lastTarget;
    private double x;
    private double y;
    private double z;
    private double lastAngle;

    private static Minecraft mc = Minecraft.getMinecraft();

    public static Vec3 getBestHitVec(final Entity entity) {
        final Vec3 positionEyes = FuckingNIgger.mc.thePlayer.getPositionEyes(1.0f);
        final float f11 = entity.getCollisionBorderSize();
        final AxisAlignedBB entityBoundingBox = entity.getEntityBoundingBox().expand(f11, f11, f11);
        final double ex = MathHelper.clamp_double(positionEyes.xCoord, entityBoundingBox.minX, entityBoundingBox.maxX);
        final double ey = MathHelper.clamp_double(positionEyes.yCoord, entityBoundingBox.minY, entityBoundingBox.maxY);
        final double ez = MathHelper.clamp_double(positionEyes.zCoord, entityBoundingBox.minZ, entityBoundingBox.maxZ);
        return new Vec3(ex, ey, ez);
    }

    public static Vec3 getBestHitVec(final Entity entity, final float partialTicks) {
        final Vec3 positionEyes = FuckingNIgger.mc.thePlayer.getPositionEyes(partialTicks);
        final float f11 = entity.getCollisionBorderSize();
        final double x = entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks;
        final double y = entity.prevPosY + (entity.posY - entity.prevPosY) * partialTicks;
        final double z = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks;
        final float width = entity.width / 2.0f;
        AxisAlignedBB entityBoundingBox = entityBoundingBox = new AxisAlignedBB(x - width, y, z - width, x + width, y + entity.height, z + width);
        final double ex = MathHelper.clamp_double(positionEyes.xCoord, entityBoundingBox.minX, entityBoundingBox.maxX);
        final double ey = MathHelper.clamp_double(positionEyes.yCoord, entityBoundingBox.minY, entityBoundingBox.maxY);
        final double ez = MathHelper.clamp_double(positionEyes.zCoord, entityBoundingBox.minZ, entityBoundingBox.maxZ);
        return new Vec3(ex, ey, ez);
    }

    public static float rotateStaticYaw(final float currentYaw, final float calcYaw) {
        float yaw = updateRotation(currentYaw, calcYaw, 180.0f);
        if (yaw == currentYaw) {
            return currentYaw;
        }
        if (FuckingNIgger.mc.gameSettings.mouseSensitivity == 0.5) {
            FuckingNIgger.mc.gameSettings.mouseSensitivity = 0.47887325f;
        }
        final float f1 = FuckingNIgger.mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        final float f2 = f1 * f1 * f1 * 8.0f;
        final int deltaX = (int)((6.667 * yaw - 6.6666667 * currentYaw) / f2);
        final float f3 = deltaX * f2;
        yaw = (float)(currentYaw + f3 * 0.15);
        return yaw;
    }

    public static float updateRotation(final float current, final float calc, final float maxDelta) {
        float f = MathHelper.wrapAngleTo180_float(calc - current);
        if (f > maxDelta) {
            f = maxDelta;
        }
        if (f < -maxDelta) {
            f = -maxDelta;
        }
        return current + f;
    }

    public float[] basicRotation(final Entity entity, final float currentYaw, final float currentPitch, final boolean random) {
        final Vec3 ePos = getBestHitVec(entity);
        final double x = ePos.xCoord - FuckingNIgger.mc.thePlayer.posX;
        final double y = ePos.yCoord - (FuckingNIgger.mc.thePlayer.posY + FuckingNIgger.mc.thePlayer.getEyeHeight());
        final double z = ePos.zCoord - FuckingNIgger.mc.thePlayer.posZ;
        final float calcYaw = (float)(MathHelper.func_181159_b(z, x) * 180.0 / 3.141592653589793 - 90.0);
        final float calcPitch = (float)(-(MathHelper.func_181159_b(y, MathHelper.sqrt_double(x * x + z * z)) * 180.0 / 3.141592653589793));
        float yaw = updateRotation(currentYaw, calcYaw, 180.0f);
        float pitch = updateRotation(currentPitch, calcPitch, 180.0f);
        if (random) {
            yaw += (float)ThreadLocalRandom.current().nextGaussian();
            pitch += (float)ThreadLocalRandom.current().nextGaussian();
        }
        return mouseSens(yaw, pitch, currentYaw, currentPitch);
    }

    public float[] middleRotation(final Entity entity, final float currentYaw, final float currentPitch, final boolean random) {
        final double x = entity.posX - FuckingNIgger.mc.thePlayer.posX;
        final double y = entity.posY + entity.getEyeHeight() - (FuckingNIgger.mc.thePlayer.posY + FuckingNIgger.mc.thePlayer.getEyeHeight());
        final double z = entity.posZ - FuckingNIgger.mc.thePlayer.posZ;
        final float calcYaw = (float)(MathHelper.func_181159_b(z, x) * 180.0 / 3.141592653589793 - 90.0);
        final float calcPitch = (float)(-(MathHelper.func_181159_b(y, MathHelper.sqrt_double(x * x + z * z)) * 180.0 / 3.141592653589793));
        float yaw = updateRotation(currentYaw, calcYaw, 180.0f);
        float pitch = updateRotation(currentPitch, calcPitch, 180.0f);
        if (random) {
            yaw += (float)ThreadLocalRandom.current().nextGaussian();
            pitch += (float)ThreadLocalRandom.current().nextGaussian();
        }
        return mouseSens(yaw, pitch, currentYaw, currentPitch);
    }

    public float[] positionRotation(final double posX, final double posY, final double posZ, final float currentYaw, final float currentPitch, final boolean random) {
        final double x = posX - FuckingNIgger.mc.thePlayer.posX;
        final double y = posY + 1.53 - (FuckingNIgger.mc.thePlayer.posY + FuckingNIgger.mc.thePlayer.getEyeHeight());
        final double z = posZ - FuckingNIgger.mc.thePlayer.posZ;
        final float calcYaw = (float)(MathHelper.func_181159_b(z, x) * 180.0 / 3.141592653589793 - 90.0);
        final float calcPitch = (float)(-(MathHelper.func_181159_b(y, MathHelper.sqrt_double(x * x + z * z)) * 180.0 / 3.141592653589793));
        float yaw = updateRotation(currentYaw, calcYaw, 180.0f);
        float pitch = updateRotation(currentPitch, calcPitch, 180.0f);
        if (random) {
            yaw += (float)ThreadLocalRandom.current().nextGaussian();
            pitch += (float)ThreadLocalRandom.current().nextGaussian();
        }
        return mouseSens(yaw, pitch, currentYaw, currentPitch);
    }

    public float[] positionRotation(final double posX, final double posY, final double posZ, final float currentYaw, final float currentPitch, final float yawSpeed, final float pitchSpeed, final boolean random) {
        final double x = posX - FuckingNIgger.mc.thePlayer.posX;
        final double y = posY + 1.53 - (FuckingNIgger.mc.thePlayer.posY + FuckingNIgger.mc.thePlayer.getEyeHeight());
        final double z = posZ - FuckingNIgger.mc.thePlayer.posZ;
        final float calcYaw = (float)(MathHelper.func_181159_b(z, x) * 180.0 / 3.141592653589793 - 90.0);
        final float calcPitch = (float)(-(MathHelper.func_181159_b(y, MathHelper.sqrt_double(x * x + z * z)) * 180.0 / 3.141592653589793));
        float yaw = updateRotation(currentYaw, calcYaw, yawSpeed);
        float pitch = updateRotation(currentPitch, calcPitch, pitchSpeed);
        if (random) {
            yaw += (float)ThreadLocalRandom.current().nextGaussian();
            pitch += (float)ThreadLocalRandom.current().nextGaussian();
        }
        return mouseSens(yaw, pitch, currentYaw, currentPitch);
    }

    public static float[] positionRotation(final double posX, final double posY, final double posZ, final float[] lastRots, final float yawSpeed, final float pitchSpeed, final boolean random) {
        final double x = posX - FuckingNIgger.mc.thePlayer.posX;
        final double y = posY - (FuckingNIgger.mc.thePlayer.posY + FuckingNIgger.mc.thePlayer.getEyeHeight());
        final double z = posZ - FuckingNIgger.mc.thePlayer.posZ;
        final float calcYaw = (float)(MathHelper.func_181159_b(z, x) * 180.0 / 3.141592653589793 - 90.0);
        final float calcPitch = (float)(-(MathHelper.func_181159_b(y, MathHelper.sqrt_double(x * x + z * z)) * 180.0 / 3.141592653589793));
        float yaw = updateRotation(lastRots[0], calcYaw, yawSpeed);
        float pitch = updateRotation(lastRots[1], calcPitch, pitchSpeed);
        if (random) {
            yaw += (float)ThreadLocalRandom.current().nextGaussian();
            pitch += (float)ThreadLocalRandom.current().nextGaussian();
        }
        return new float[] { yaw, pitch };
    }

    public static float[] positionRotation(final double posX, final double posY, final double posZ, final float[] lastRots, final float yawSpeed, final float pitchSpeed, final boolean random, final float partialTicks) {
        final double px = FuckingNIgger.mc.thePlayer.prevPosX + (FuckingNIgger.mc.thePlayer.posX - FuckingNIgger.mc.thePlayer.prevPosX) * partialTicks;
        final double py = FuckingNIgger.mc.thePlayer.prevPosY + (FuckingNIgger.mc.thePlayer.posY - FuckingNIgger.mc.thePlayer.prevPosY) * partialTicks;
        final double pz = FuckingNIgger.mc.thePlayer.prevPosZ + (FuckingNIgger.mc.thePlayer.posZ - FuckingNIgger.mc.thePlayer.prevPosZ) * partialTicks;
        final double x = posX - px;
        final double y = posY - (py + FuckingNIgger.mc.thePlayer.getEyeHeight());
        final double z = posZ - pz;
        final float calcYaw = (float)(MathHelper.func_181159_b(z, x) * 180.0 / 3.141592653589793 - 90.0);
        final float calcPitch = (float)(-(MathHelper.func_181159_b(y, MathHelper.sqrt_double(x * x + z * z)) * 180.0 / 3.141592653589793));
        float yaw = updateRotation(lastRots[0], calcYaw, yawSpeed);
        float pitch = updateRotation(lastRots[1], calcPitch, pitchSpeed);
        if (random) {
            yaw += (float)ThreadLocalRandom.current().nextGaussian();
            pitch += (float)ThreadLocalRandom.current().nextGaussian();
        }
        return new float[] { yaw, pitch };
    }

    public float[] faceEntityCustom(final Entity entity, float yawSpeed, float pitchSpeed, final float currentYaw, final float currentPitch, final String randomMode, final boolean interpolateRotation, final boolean smartAim, final boolean stopOnTarget, final float randomStrength, final Vec3 best, final boolean throughWalls, final boolean advancedRots, final boolean heuristics, final boolean intave, final boolean bestHitVec) {
        if (smartAim && !throughWalls) {
            double ePosX = best.xCoord;
            double ePosY = best.yCoord;
            double ePosZ = best.zCoord;
            if (heuristics) {
                final double[] xyz = this.heuristics(entity, new double[] { ePosX, ePosY, ePosZ });
                ePosX = xyz[0];
                ePosY = xyz[1];
                ePosZ = xyz[2];
            }
            this.x = ePosX - FuckingNIgger.mc.thePlayer.posX;
            this.y = ePosY - (FuckingNIgger.mc.thePlayer.posY + FuckingNIgger.mc.thePlayer.getEyeHeight());
            this.z = ePosZ - FuckingNIgger.mc.thePlayer.posZ;
        }
        else {
            double ex = entity.posX;
            double ey = entity.posY + entity.getEyeHeight();
            double ez = entity.posZ;
            if (bestHitVec) {
                final Vec3 entityVec = getBestHitVec(entity);
                ex = entityVec.xCoord;
                ey = entityVec.yCoord;
                ez = entityVec.zCoord;
            }
            if (heuristics) {
                final double[] xyz = this.heuristics(entity, new double[] { ex, ey, ez });
                ex = xyz[0];
                ey = xyz[1];
                ez = xyz[2];
            }
            this.x = ex - FuckingNIgger.mc.thePlayer.posX;
            this.y = ey - (FuckingNIgger.mc.thePlayer.posY + FuckingNIgger.mc.thePlayer.getEyeHeight());
            this.z = ez - FuckingNIgger.mc.thePlayer.posZ;
        }
        final float calcYaw = (float)(Math.atan2(this.z, this.x) * 180.0 / 3.141592653589793 - 90.0);
        final float calcPitch = (float)(-(MathHelper.func_181159_b(this.y, MathHelper.sqrt_double(this.x * this.x + this.z * this.z)) * 180.0 / 3.141592653589793));
        if (stopOnTarget && FuckingNIgger.mc.objectMouseOver != null && FuckingNIgger.mc.objectMouseOver.entityHit == entity) {
            yawSpeed = 0.0f;
            pitchSpeed = 0.0f;
        }
        final double diffYaw = MathHelper.wrapAngleTo180_float(calcYaw - currentYaw);
        final double diffPitch = MathHelper.wrapAngleTo180_float(calcPitch - currentPitch);
        float yaw;
        float pitch;
        if (interpolateRotation) {
            yaw = this.interpolateRotation(currentYaw, calcYaw, yawSpeed / RandomUtil.nextFloat(170.0f, 180.0f));
            pitch = this.interpolateRotation(currentPitch, calcPitch, pitchSpeed / RandomUtil.nextFloat(170.0f, 180.0f));
        }
        else if (heuristics) {
            final float[] f = this.testRots(currentYaw, currentPitch, calcYaw, calcPitch, entity, yawSpeed, pitchSpeed, best, smartAim, throughWalls);
            yaw = f[0];
            pitch = f[1];
        }
        else {
            yaw = updateRotation(currentYaw, calcYaw, yawSpeed);
            pitch = updateRotation(currentPitch, calcPitch, pitchSpeed);
        }
        switch (randomMode) {
            case "Basic": {
                yaw += (float)(intave ? (RandomUtil.nextSecureFloat(1.0, 2.0) * Math.sin(pitch * 3.141592653589793) * randomStrength) : (ThreadLocalRandom.current().nextGaussian() * randomStrength));
                pitch += (float)(intave ? (RandomUtil.nextSecureFloat(1.0, 2.0) * Math.sin(yaw * 3.141592653589793) * randomStrength) : (ThreadLocalRandom.current().nextGaussian() * randomStrength));
                break;
            }
            case "OnlyRotation": {
                if (-yawSpeed > diffYaw || diffYaw > yawSpeed || -pitchSpeed > diffPitch || diffPitch > pitchSpeed) {
                    yaw += (float)(intave ? (RandomUtil.nextSecureFloat(1.0, 2.0) * Math.sin(pitch * 3.141592653589793) * randomStrength) : (ThreadLocalRandom.current().nextGaussian() * randomStrength));
                    pitch += (float)(intave ? (RandomUtil.nextSecureFloat(1.0, 2.0) * Math.sin(yaw * 3.141592653589793) * randomStrength) : (ThreadLocalRandom.current().nextGaussian() * randomStrength));
                    break;
                }
                break;
            }
            case "Doubled": {
                final float random1 = RandomUtil.nextSecureFloat(-randomStrength, randomStrength);
                final float random2 = RandomUtil.nextSecureFloat(-randomStrength, randomStrength);
                final float random3 = RandomUtil.nextSecureFloat(-randomStrength, randomStrength);
                final float random4 = RandomUtil.nextSecureFloat(-randomStrength, randomStrength);
                yaw += RandomUtil.nextSecureFloat(Math.min(random1, random2), Math.max(random1, random2));
                pitch += RandomUtil.nextSecureFloat(Math.min(random3, random4), Math.max(random3, random4));
                break;
            }
        }
        if (advancedRots) {
            pitch += (float)(Math.sin(0.06981317007977318 * (updateRotation(currentYaw, calcYaw, 180.0f) - yaw)) * 8.0);
        }
        if (FuckingNIgger.mc.gameSettings.mouseSensitivity == 0.5) {
            FuckingNIgger.mc.gameSettings.mouseSensitivity = 0.47887325f;
        }
        final float f2 = FuckingNIgger.mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        final float f3 = f2 * f2 * f2 * 8.0f;
        final int deltaX = (int)((6.667 * yaw - 6.667 * currentYaw) / f3);
        final int deltaY = (int)((6.667 * pitch - 6.667 * currentPitch) / f3) * -1;
        final float f4 = deltaX * f3;
        final float f5 = deltaY * f3;
        yaw = (float)(currentYaw + f4 * 0.15);
        final float f6 = (float)(currentPitch - f5 * 0.15);
        pitch = MathHelper.clamp_float(f6, -90.0f, 90.0f);
        this.lastX = entity.posX;
        this.lastY = entity.posY;
        this.lastZ = entity.posZ;
        if (entity instanceof EntityLivingBase) {
            this.checkRotationAnalysis((EntityLivingBase)entity, yaw, pitch, currentYaw, currentPitch);
        }
        return new float[] { yaw, pitch };
    }

    private double[] heuristics(final Entity entity, final double[] xyz) {
        final double boxSize = 0.2;
        final float f11 = entity.getCollisionBorderSize();
        final double minX = MathHelper.clamp_double(xyz[0] - boxSize, entity.getEntityBoundingBox().minX - f11, entity.getEntityBoundingBox().maxX + f11);
        final double minY = MathHelper.clamp_double(xyz[1] - boxSize, entity.getEntityBoundingBox().minY - f11, entity.getEntityBoundingBox().maxY + f11);
        final double minZ = MathHelper.clamp_double(xyz[2] - boxSize, entity.getEntityBoundingBox().minZ - f11, entity.getEntityBoundingBox().maxZ + f11);
        final double maxX = MathHelper.clamp_double(xyz[0] + boxSize, entity.getEntityBoundingBox().minX - f11, entity.getEntityBoundingBox().maxX + f11);
        final double maxY = MathHelper.clamp_double(xyz[1] + boxSize, entity.getEntityBoundingBox().minY - f11, entity.getEntityBoundingBox().maxY + f11);
        final double maxZ = MathHelper.clamp_double(xyz[2] + boxSize, entity.getEntityBoundingBox().minZ - f11, entity.getEntityBoundingBox().maxZ + f11);
        xyz[0] = MathHelper.clamp_double(xyz[0] + RandomUtil.randomSin(), minX, maxX);
        xyz[1] = MathHelper.clamp_double(xyz[1] + RandomUtil.randomSin(), minY, maxY);
        xyz[2] = MathHelper.clamp_double(xyz[2] + RandomUtil.randomSin(), minZ, maxZ);
        return xyz;
    }

    public static int[] getDxDy(final float yaw, final float pitch, final float lastYaw, final float lastPitch) {
        if (FuckingNIgger.mc.gameSettings.mouseSensitivity == 0.5) {
            FuckingNIgger.mc.gameSettings.mouseSensitivity = 0.47887325f;
        }
        final float f1 = FuckingNIgger.mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        final float f2 = f1 * f1 * f1 * 8.0f;
        final int deltaX = (int)Math.round((6.667 * yaw - 6.667 * lastYaw) / f2);
        final int deltaY = (int)Math.round((6.667 * pitch - 6.667 * lastPitch) / f2) * -1;
        return new int[] { deltaX, deltaY };
    }

    public static float[] dxToRots(final int deltaX, final int deltaY, final float lastYaw, final float lastPitch) {
        if (FuckingNIgger.mc.gameSettings.mouseSensitivity == 0.5) {
            FuckingNIgger.mc.gameSettings.mouseSensitivity = 0.47887325f;
        }
        final float f1 = FuckingNIgger.mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        final float f2 = f1 * f1 * f1 * 8.0f;
        final float f3 = deltaX * f2;
        final float f4 = deltaY * f2;
        final float yaw = (float)(lastYaw + f3 * 0.15);
        final float f5 = (float)(lastPitch - f4 * 0.15);
        final float pitch = MathHelper.clamp_float(f5, -90.0f, 90.0f);
        return new float[] { yaw, pitch };
    }

    public static float[] mouseSens(float yaw, float pitch, final float lastYaw, final float lastPitch) {
        if (FuckingNIgger.mc.gameSettings.mouseSensitivity == 0.5) {
            FuckingNIgger.mc.gameSettings.mouseSensitivity = 0.47887325f;
        }
        if (yaw == lastYaw && pitch == lastPitch) {
            return new float[] { yaw, pitch };
        }
        final float f1 = FuckingNIgger.mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        final float f2 = f1 * f1 * f1 * 8.0f;
        final int deltaX = (int)((6.667 * yaw - 6.667 * lastYaw) / f2);
        final int deltaY = (int)((6.667 * pitch - 6.667 * lastPitch) / f2) * -1;
        final float f3 = deltaX * f2;
        final float f4 = deltaY * f2;
        yaw = (float)(lastYaw + f3 * 0.15);
        final float f5 = (float)(lastPitch - f4 * 0.15);
        pitch = MathHelper.clamp_float(f5, -90.0f, 90.0f);
        return new float[] { yaw, pitch };
    }

    public float rotateToYaw(final float yawSpeed, final float currentYaw, final float calcYaw) {
        float yaw = updateRotation(currentYaw, calcYaw, yawSpeed + RandomUtil.nextFloat(0.0f, 15.0f));
        final double diffYaw = MathHelper.wrapAngleTo180_float(calcYaw - currentYaw);
        if (-yawSpeed > diffYaw || diffYaw > yawSpeed) {
            yaw += (float)(RandomUtil.nextFloat(1.0f, 2.0f) * Math.sin(FuckingNIgger.mc.thePlayer.rotationPitch * 3.141592653589793));
        }
        if (yaw == currentYaw) {
            return currentYaw;
        }
        if (FuckingNIgger.mc.gameSettings.mouseSensitivity == 0.5) {
            FuckingNIgger.mc.gameSettings.mouseSensitivity = 0.47887325f;
        }
        final float f1 = FuckingNIgger.mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        final float f2 = f1 * f1 * f1 * 8.0f;
        final int deltaX = (int)((6.667 * yaw - 6.666666666666667 * currentYaw) / f2);
        final float f3 = deltaX * f2;
        yaw = (float)(currentYaw + f3 * 0.15);
        return yaw;
    }

    public float rotateToYaw(final float yawSpeed, final float[] currentRots, final float calcYaw) {
        float yaw = updateRotation(currentRots[0], calcYaw, yawSpeed + RandomUtil.nextFloat(0.0f, 15.0f));
        if (yaw != calcYaw) {
            yaw += (float)(RandomUtil.nextFloat(1.0f, 2.0f) * Math.sin(currentRots[1] * 3.141592653589793));
        }
        if (yaw == currentRots[0]) {
            return currentRots[0];
        }
        yaw += (float)(ThreadLocalRandom.current().nextGaussian() * 0.2);
        if (FuckingNIgger.mc.gameSettings.mouseSensitivity == 0.5) {
            FuckingNIgger.mc.gameSettings.mouseSensitivity = 0.47887325f;
        }
        final float f1 = FuckingNIgger.mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        final float f2 = f1 * f1 * f1 * 8.0f;
        final int deltaX = (int)((6.667 * yaw - 6.6666667 * currentRots[0]) / f2);
        final float f3 = deltaX * f2;
        yaw = (float)(currentRots[0] + f3 * 0.15);
        return yaw;
    }

    public float rotateToPitch(final float pitchSpeed, final float currentPitch, final float calcPitch) {
        float pitch = updateRotation(currentPitch, calcPitch, pitchSpeed + RandomUtil.nextFloat(0.0f, 15.0f));
        if (pitch != calcPitch) {
            pitch += (float)(RandomUtil.nextFloat(1.0f, 2.0f) * Math.sin(FuckingNIgger.mc.thePlayer.rotationYaw * 3.141592653589793));
        }
        if (FuckingNIgger.mc.gameSettings.mouseSensitivity == 0.5) {
            FuckingNIgger.mc.gameSettings.mouseSensitivity = 0.47887325f;
        }
        final float f1 = FuckingNIgger.mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        final float f2 = f1 * f1 * f1 * 8.0f;
        final int deltaY = (int)((6.667 * pitch - 6.666667 * currentPitch) / f2) * -1;
        final float f3 = deltaY * f2;
        final float f4 = (float)(currentPitch - f3 * 0.15);
        pitch = MathHelper.clamp_float(f4, -90.0f, 90.0f);
        return pitch;
    }

    public float rotateToPitch(final float pitchSpeed, final float[] currentRots, final float calcPitch) {
        float pitch = updateRotation(currentRots[1], calcPitch, pitchSpeed + RandomUtil.nextFloat(0.0f, 15.0f));
        if (pitch != calcPitch) {
            pitch += (float)(RandomUtil.nextFloat(1.0f, 2.0f) * Math.sin(currentRots[0] * 3.141592653589793));
        }
        if (FuckingNIgger.mc.gameSettings.mouseSensitivity == 0.5) {
            FuckingNIgger.mc.gameSettings.mouseSensitivity = 0.47887325f;
        }
        final float f1 = FuckingNIgger.mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        final float f2 = f1 * f1 * f1 * 8.0f;
        final int deltaY = (int)((6.667 * pitch - 6.666667 * currentRots[1]) / f2) * -1;
        final float f3 = deltaY * f2;
        final float f4 = (float)(currentRots[1] - f3 * 0.15);
        pitch = MathHelper.clamp_float(f4, -90.0f, 90.0f);
        return pitch;
    }

    public float[] backRotate(final float yawSpeed, final float pitchSpeed, final float currentYaw, final float currentPitch, final float calcYaw, final float calcPitch) {
        float yaw = updateRotation(currentYaw, calcYaw + RandomUtil.nextFloat(-2.0f, 2.0f), 20.0f + RandomUtil.nextFloat(0.0f, 15.0f));
        float pitch = updateRotation(currentPitch, calcPitch + RandomUtil.nextFloat(-2.0f, 2.0f), 10.0f + RandomUtil.nextFloat(0.0f, 15.0f));
        yaw += (float)(ThreadLocalRandom.current().nextGaussian() * 0.6);
        pitch += (float)(ThreadLocalRandom.current().nextGaussian() * 0.6);
        if (FuckingNIgger.mc.gameSettings.mouseSensitivity == 0.5) {
            FuckingNIgger.mc.gameSettings.mouseSensitivity = 0.47887325f;
        }
        final float f1 = FuckingNIgger.mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        final float f2 = f1 * f1 * f1 * 8.0f;
        final int deltaX = (int)((6.667 * yaw - 6.6666667 * currentYaw) / f2);
        final int deltaY = (int)((6.667 * pitch - 6.666667 * currentPitch) / f2) * -1;
        final float f3 = deltaX * f2;
        final float f4 = deltaY * f2;
        yaw = (float)(currentYaw + f3 * 0.15);
        final float f5 = (float)(currentPitch - f4 * 0.15);
        pitch = MathHelper.clamp_float(f5, -90.0f, 90.0f);
        return new float[] { yaw, pitch };
    }

    public float[] scaffoldRots(final double bx, final double by, final double bz, final float lastYaw, final float lastPitch, final float yawSpeed, final float pitchSpeed, final boolean random) {
        final double x = bx - FuckingNIgger.mc.thePlayer.posX;
        final double y = by - (FuckingNIgger.mc.thePlayer.posY + FuckingNIgger.mc.thePlayer.getEyeHeight());
        final double z = bz - FuckingNIgger.mc.thePlayer.posZ;
        final float calcYaw = (float)(Math.toDegrees(MathHelper.func_181159_b(z, x)) - 90.0);
        final float calcPitch = (float)(-(MathHelper.func_181159_b(y, MathHelper.sqrt_double(x * x + z * z)) * 180.0 / 3.141592653589793));
        float pitch = updateRotation(lastPitch, calcPitch, pitchSpeed + RandomUtil.nextFloat(0.0f, 15.0f));
        float yaw = updateRotation(lastYaw, calcYaw, yawSpeed + RandomUtil.nextFloat(0.0f, 15.0f));
        if (random) {
            yaw += (float)ThreadLocalRandom.current().nextDouble(-2.0, 2.0);
            pitch += (float)ThreadLocalRandom.current().nextDouble(-0.2, 0.2);
        }
        return new float[] { yaw, pitch };
    }

    public float[] advancedScaffoldRots(final BlockPosition blockPosition, final double[] expandXZ, final float yaw, final float[] lastRots, final float yawSpeed, final float pitchSpeed) {
        final ArrayList<Float> yaws = new ArrayList<Float>();
        final ArrayList<Float> pitches = new ArrayList<Float>();
        for (int bx = blockPosition.getX(); bx <= blockPosition.getX() + 1; ++bx) {
            for (int bz = blockPosition.getZ(); bz <= blockPosition.getZ() + 1; ++bz) {
                for (int by = blockPosition.getY(); by <= blockPosition.getY() + 1; ++by) {
                    final double x = bx - FuckingNIgger.mc.thePlayer.posX;
                    final double z = bz - FuckingNIgger.mc.thePlayer.posZ;
                    final double y = by - (FuckingNIgger.mc.thePlayer.posY + FuckingNIgger.mc.thePlayer.getEyeHeight());
                    yaws.add(updateRotation(lastRots[0], (float)(MathHelper.func_181159_b(z, x) * 180.0 / 3.141592653589793 - 90.0), 180.0f));
                    final float calcPitch = (float)(-(MathHelper.func_181159_b(y, MathHelper.sqrt_double(x * x + z * z)) * 180.0 / 3.141592653589793));
                    pitches.add(MathHelper.clamp_float(calcPitch, -90.0f, 90.0f));
                }
            }
        }
        Collections.sort(yaws);
        Collections.sort(pitches);
        final float yaww = MathHelper.wrapAngleTo180_float(yaw) + 180.0f;
        final float maxYaw = MathHelper.wrapAngleTo180_float(yaws.get(yaws.size() - 1)) + 180.0f;
        final float minYaw = MathHelper.wrapAngleTo180_float(yaws.get(0)) + 180.0f;
        if ((yaww <= minYaw || yaww >= maxYaw) && (minYaw <= maxYaw || yaww >= minYaw || yaww <= maxYaw)) {
            double p = 0.85;
            if ((FuckingNIgger.mc.gameSettings.keyBindJump.isKeyDown() || !FuckingNIgger.mc.thePlayer.onGround) && FuckingNIgger.mc.thePlayer.moveForward != 0.0f) {
                p = 0.65;
            }
            return this.scaffoldRots(blockPosition.getX() + expandXZ[0], blockPosition.getY() + p, blockPosition.getZ() + expandXZ[1], lastRots[0], lastRots[1], yawSpeed, pitchSpeed, false);
        }
        ArrayList<MovingObjectPosition> movingObjectPositions = new ArrayList<MovingObjectPosition>();
        ArrayList<MovingObjectPosition> movingObjectPositions2 = new ArrayList<MovingObjectPosition>();
        for (float i = pitches.get(0); i < pitches.get(pitches.size() - 1); i += 0.01f) {
            final MovingObjectPosition j = FuckingNIgger.mc.thePlayer.customRayTrace(4.5, 1.0f, yaw, i);
            if (j.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && this.isOkBlock(j.getBlockPos()) && !movingObjectPositions.contains(j)) {
                if (j.sideHit != EnumFacing.DOWN && j.sideHit != EnumFacing.UP) {
                    movingObjectPositions.add(j);
                }
                movingObjectPositions2.add(j);
            }
        }
        movingObjectPositions.sort(Comparator.comparingDouble(m -> FuckingNIgger.mc.thePlayer.getDistanceSq(m.getBlockPos().add(0.5, 0.5, 0.5))));
        MovingObjectPosition k = null;
        if (movingObjectPositions.size() > 0) {
            k = movingObjectPositions.get(0);
        }
        BlockPosition b = new BlockPosition(FuckingNIgger.mc.thePlayer.posX, FuckingNIgger.mc.thePlayer.posY - 1.0, FuckingNIgger.mc.thePlayer.posZ);
        if (FuckingNIgger.mc.theWorld.getBlockState(b).getBlock().getMaterial() == Material.air && k != null) {
            final float[] f = this.scaffoldRots(k.hitVec.xCoord, k.getBlockPos().getY() + RandomUtil.nextDouble(0.45, 0.55), k.hitVec.zCoord, lastRots[0], lastRots[1], yawSpeed, pitchSpeed, false);
            return new float[] { yaw, f[1] };
        }
        if (movingObjectPositions2.size() != 0) {
            return new float[] { yaw, lastRots[1] };
        }
        movingObjectPositions = new ArrayList<MovingObjectPosition>();
        movingObjectPositions2 = new ArrayList<MovingObjectPosition>();
        for (float i = pitches.get(0); i < pitches.get(pitches.size() - 1); i += 0.01f) {
            final MovingObjectPosition j = FuckingNIgger.mc.thePlayer.customRayTrace(4.5, 1.0f, yaw, i);
            if (j.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && this.isOkBlock(j.getBlockPos()) && !movingObjectPositions.contains(j)) {
                if (j.sideHit != EnumFacing.DOWN && j.sideHit != EnumFacing.UP) {
                    movingObjectPositions.add(j);
                }
                movingObjectPositions2.add(j);
            }
        }
        movingObjectPositions.sort(Comparator.comparingDouble(m -> FuckingNIgger.mc.thePlayer.getDistanceSq(m.getBlockPos().add(0.5, 0.5, 0.5))));
        k = null;
        if (movingObjectPositions.size() > 0) {
            k = movingObjectPositions.get(0);
        }
        b = new BlockPosition(FuckingNIgger.mc.thePlayer.posX, FuckingNIgger.mc.thePlayer.posY - 1.0, FuckingNIgger.mc.thePlayer.posZ);
        if (FuckingNIgger.mc.theWorld.getBlockState(b).getBlock().getMaterial() == Material.air && k != null) {
            final float[] f = this.scaffoldRots(k.hitVec.xCoord, k.getBlockPos().getY() + RandomUtil.nextDouble(0.45, 0.55), k.hitVec.zCoord, lastRots[0], lastRots[1], yawSpeed, pitchSpeed, false);
            return new float[] { yaw, f[1] };
        }
        if (movingObjectPositions2.size() != 0) {
            return new float[] { yaw, lastRots[1] };
        }
        return lastRots;
    }

    private boolean isOkBlock(final BlockPosition blockPosition) {
        final Block block = FuckingNIgger.mc.theWorld.getBlockState(blockPosition).getBlock();
        return !(block instanceof BlockLiquid) && !(block instanceof BlockAir) && !(block instanceof BlockChest) && !(block instanceof BlockFurnace);
    }

    public float interpolateRotation(final float current, final float predicted, float percentage) {
        final float f = MathHelper.wrapAngleTo180_float(predicted - current);
        if (f <= 10.0f && f >= -10.0f) {
            percentage = 1.0f;
        }
        return current + percentage * f;
    }

    public static float[] getFovToTarget(final double posX, final double posY, final double posZ, final float yaw, final float pitch) {
        final float[] f = new float[2];
        final double x = posX - FuckingNIgger.mc.thePlayer.posX;
        final double y = posY - (FuckingNIgger.mc.thePlayer.posY + FuckingNIgger.mc.thePlayer.getEyeHeight());
        final double z = posZ - FuckingNIgger.mc.thePlayer.posZ;
        final float calcYaw = (float)(MathHelper.func_181159_b(z, x) * 180.0 / 3.141592653589793 - 90.0);
        final float calcPitch = (float)(-(MathHelper.func_181159_b(y, MathHelper.sqrt_double(x * x + z * z)) * 180.0 / 3.141592653589793));
        final float diffY = MathHelper.wrapAngleTo180_float(calcYaw - yaw);
        final float diffP = MathHelper.wrapAngleTo180_float(calcPitch - pitch);
        return new float[] { diffY, diffP };
    }

    private float[] testRots(final float currentYaw, final float currentPitch, float calcYaw, float calcPitch, final Entity entity, final float speedYaw, final float speedPitch, final Vec3 best, final boolean smartAim, final boolean throughWalls) {
        final double radius = RandomUtil.nextDouble(0.001, 2.0);
        this.lastAngle = ((this.lastAngle > 360.0) ? 0.0 : (this.lastAngle + RandomUtil.nextDouble(-0.4, 1.2)));
        final double x = Math.sin(this.lastAngle) * radius;
        final double y = Math.cos(this.lastAngle) * radius;
        calcYaw += (float)x;
        calcPitch += (float)y;
        float diffYaw = MathHelper.wrapAngleTo180_float(calcYaw - currentYaw);
        if (Math.abs(diffYaw) > 10.0f) {
            if (diffYaw > speedYaw) {
                diffYaw = speedYaw;
            }
            if (diffYaw < -speedYaw) {
                diffYaw = -speedYaw;
            }
        }
        else {
            diffYaw *= RandomUtil.nextFloat(0.3, 0.7);
        }
        float diffPitch = MathHelper.wrapAngleTo180_float(calcPitch - currentPitch);
        if (Math.abs(diffPitch) > 10.0f) {
            if (diffPitch > speedPitch) {
                diffPitch = speedPitch;
            }
            if (diffPitch < -speedPitch) {
                diffPitch = -speedPitch;
            }
        }
        else {
            diffPitch *= RandomUtil.nextFloat(0.3, 0.7);
        }
        if (entity instanceof EntityLivingBase) {
            final EntityLivingBase entityPlayer = (EntityLivingBase)entity;
            if (entityPlayer.hurtTime < 1) {
                final MovingObjectPosition objectPosition = RayTraceUtil.rayCast(1.0f, new float[] { currentYaw + diffYaw, currentPitch + diffPitch });
                if (smartAim && !throughWalls) {
                    final double ePosX = best.xCoord;
                    final double ePosY = best.yCoord;
                    final double ePosZ = best.zCoord;
                    this.x = ePosX - FuckingNIgger.mc.thePlayer.posX;
                    this.y = ePosY - (FuckingNIgger.mc.thePlayer.posY + FuckingNIgger.mc.thePlayer.getEyeHeight());
                    this.z = ePosZ - FuckingNIgger.mc.thePlayer.posZ;
                }
                else {
                    final Vec3 entityVec = getBestHitVec(entity);
                    this.x = entityVec.xCoord - FuckingNIgger.mc.thePlayer.posX;
                    this.y = entityVec.yCoord - (FuckingNIgger.mc.thePlayer.posY + FuckingNIgger.mc.thePlayer.getEyeHeight());
                    this.z = entityVec.zCoord - FuckingNIgger.mc.thePlayer.posZ;
                }
                final float newCalcYaw = (float)(MathHelper.func_181159_b(this.z, this.x) * 180.0 / 3.141592653589793 - 90.0);
                final float newCalcPitch = (float)(-(MathHelper.func_181159_b(this.y, MathHelper.sqrt_double(this.x * this.x + this.z * this.z)) * 180.0 / 3.141592653589793));
                float diffY = MathHelper.wrapAngleTo180_float(newCalcYaw - currentYaw);
                if (Math.abs(diffY) > -1.0f) {
                    if (diffY > speedYaw) {
                        diffY = speedYaw;
                    }
                    if (diffY < -speedYaw) {
                        diffY = -speedYaw;
                    }
                }
                else {
                    diffY *= RandomUtil.nextFloat(0.3, 0.7);
                }
                float diffP = MathHelper.wrapAngleTo180_float(newCalcPitch - currentPitch);
                if (Math.abs(diffP) > -1.0f) {
                    if (diffP > speedPitch) {
                        diffP = speedPitch;
                    }
                    if (diffP < -speedPitch) {
                        diffP = -speedPitch;
                    }
                }
                else {
                    diffP *= RandomUtil.nextFloat(0.3, 0.7);
                }
                final MovingObjectPosition objectPosition2 = RayTraceUtil.rayCast(1.0f, new float[] { currentYaw + diffY, currentPitch + diffP });
                if (objectPosition != null && objectPosition2 != null && objectPosition2.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && objectPosition.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) {
                    diffYaw = diffY;
                    diffPitch = diffP;
                }
            }
        }
        if (entity == null) {
            FuckingNIgger.ACCURATE_ROTATION_YAW_LEVEL = 0.0;
            FuckingNIgger.ACCURATE_ROTATION_YAW_VL = 0.0;
            FuckingNIgger.ACCURATE_ROTATION_PITCH_LEVEL = 0.0;
            FuckingNIgger.ACCURATE_ROTATION_PITCH_VL = 0.0;
        }
        float bestYaw = currentYaw + diffYaw;
        float bestPitch = currentPitch + diffPitch;
        final MovingObjectPosition objectPosition3 = RayTraceUtil.rayCast(1.0f, new float[] { bestYaw, bestPitch });
        float yawSpeed = Math.abs(bestYaw % 360.0f - currentYaw % 360.0f);
        float perfectYaw = this.basicRotation(entity, bestYaw, bestPitch, false)[0];
        double bestYawRotationDistance = Math.abs(bestYaw - perfectYaw);
        float pitchSpeed = Math.abs(bestPitch % 360.0f - currentPitch % 360.0f);
        float perfectPitch = this.basicRotation(entity, bestYaw, bestPitch, false)[1];
        double bestPitchRotationDistance = Math.abs(bestPitch - perfectPitch);
        final boolean targetIsMoving = Math.abs(entity.posX - entity.lastTickPosX) > 0.01 || Math.abs(entity.posZ - entity.lastTickPosZ) > 0.01;
        if (yawSpeed > 0.5f && targetIsMoving) {
            double correctYaw = FuckingNIgger.ACCURATE_ROTATION_YAW_LEVEL / 0.25 / 0.8;
            if (bestYawRotationDistance / 0.8 < 2.0) {
                correctYaw += 2.0 - bestYawRotationDistance / 0.8;
            }
            bestYaw = (float)((RandomUtil.nextInt(0, 100000) % 2 == 0) ? (bestYaw - correctYaw) : (bestYaw + correctYaw));
        }
        while (bestPitch == perfectPitch) {
            bestPitch += (float)(RandomUtil.nextFloat(-1.0f, 1.0f) / 4.0f + RandomUtil.nextFloat(-1.0f, 1.0f) / 4.0f + MathHelper.clamp_float((float)ThreadLocalRandom.current().nextGaussian(), -1.0f, 1.0f) / 4.0f + RandomUtil.randomSin() / 4.0);
        }
        if (entity instanceof EntityLivingBase) {
            final EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
            if (entityLivingBase.hurtTime <= 4) {
                final MovingObjectPosition objectPosition4 = RayTraceUtil.rayCast(1.0f, new float[] { bestYaw, bestPitch });
                if (objectPosition3 == null || objectPosition4 == null || objectPosition4.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY || objectPosition3.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) {}
            }
        }
        yawSpeed = Math.abs(bestYaw % 360.0f - currentYaw % 360.0f);
        perfectYaw = this.basicRotation(entity, bestYaw, bestPitch, false)[0];
        bestYawRotationDistance = Math.abs(bestYaw - perfectYaw);
        pitchSpeed = Math.abs(bestPitch % 360.0f - currentPitch % 360.0f);
        for (perfectPitch = this.basicRotation(entity, bestYaw, bestPitch, false)[1], bestPitchRotationDistance = Math.abs(bestPitch - perfectPitch); bestPitchRotationDistance == 0.0; bestPitchRotationDistance = Math.abs(bestPitch - perfectPitch)) {
            bestPitch += (float)(RandomUtil.nextFloat(-1.0f, 1.0f) / 4.0f + RandomUtil.nextFloat(-1.0f, 1.0f) / 4.0f + MathHelper.clamp_float((float)ThreadLocalRandom.current().nextGaussian(), -1.0f, 1.0f) / 4.0f + RandomUtil.randomSin() / 4.0);
        }
        if (yawSpeed > 0.5f) {
            if (targetIsMoving) {
                FuckingNIgger.ACCURATE_ROTATION_YAW_LEVEL += 2.0 - bestYawRotationDistance / 0.8;
                FuckingNIgger.ACCURATE_ROTATION_YAW_LEVEL = Math.max(0.0, FuckingNIgger.ACCURATE_ROTATION_YAW_LEVEL);
                final int suspiciousLevel = (int)FuckingNIgger.ACCURATE_ROTATION_YAW_LEVEL;
                if (suspiciousLevel > 12) {
                    ++FuckingNIgger.ACCURATE_ROTATION_YAW_VL;
                    if (FuckingNIgger.ACCURATE_ROTATION_YAW_VL > 3.0) {
                        FuckingNIgger.mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("Too accurate yaw rotation §7(§c" + bestYawRotationDistance + " §7| " + FuckingNIgger.ACCURATE_ROTATION_YAW_LEVEL + " §7| " + yawSpeed + ") "));
                    }
                }
            }
        }
        else if (FuckingNIgger.ACCURATE_ROTATION_YAW_VL > 0.0) {
            FuckingNIgger.ACCURATE_ROTATION_YAW_VL -= 1.0E-5;
        }
        if (pitchSpeed > 0.5f) {
            if (targetIsMoving && bestPitchRotationDistance == 0.0 && currentPitch != bestPitch) {
                FuckingNIgger.ACCURATE_ROTATION_PITCH_LEVEL += 2.0 - bestPitchRotationDistance / 0.8;
                FuckingNIgger.ACCURATE_ROTATION_PITCH_LEVEL = Math.max(0.0, FuckingNIgger.ACCURATE_ROTATION_PITCH_LEVEL);
                final int suspiciousLevel = (int)FuckingNIgger.ACCURATE_ROTATION_PITCH_LEVEL;
                if (suspiciousLevel > 8) {
                    ++FuckingNIgger.ACCURATE_ROTATION_PITCH_VL;
                    if (FuckingNIgger.ACCURATE_ROTATION_PITCH_VL > 3.0) {
                        FuckingNIgger.mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText("Too accurate pitch rotation §7(§c" + bestPitchRotationDistance + " §7| " + FuckingNIgger.ACCURATE_ROTATION_PITCH_LEVEL + " §7| " + yawSpeed + ") "));
                    }
                }
            }
        }
        else if (FuckingNIgger.ACCURATE_ROTATION_PITCH_VL > 0.0) {
            FuckingNIgger.ACCURATE_ROTATION_PITCH_VL -= 1.0E-5;
        }
        this.lastTarget = entity;
        return new float[] { bestYaw, bestPitch };
    }

    public void checkRotationAnalysis(final EntityLivingBase target, final float yaw, final float pitch, final float prevYaw, final float prevPitch) {
        if (target == null) {
            FuckingNIgger.ACCURATE_ROTATION_YAW_LEVEL1 = 0.0;
            FuckingNIgger.ACCURATE_ROTATION_YAW_VL1 = 0.0;
            FuckingNIgger.ACCURATE_ROTATION_PITCH_LEVEL1 = 0.0;
            FuckingNIgger.ACCURATE_ROTATION_PITCH_VL1 = 0.0;
            return;
        }
        final float yawSpeed = Math.abs(yaw % 360.0f - prevYaw % 360.0f);
        final float perfectYaw = this.basicRotation(target, yaw, pitch, false)[0];
        final double bestYawRotationDistance = Math.abs(yaw - perfectYaw);
        final float pitchSpeed = Math.abs(pitch % 360.0f - prevPitch % 360.0f);
        final float perfectPitch = this.basicRotation(target, yaw, pitch, false)[1];
        final double bestPitchRotationDistance = Math.abs(pitch - perfectPitch);
        final boolean targetIsMoving = Math.abs(target.posX - target.lastTickPosX) > 0.01 || Math.abs(target.posZ - target.lastTickPosZ) > 0.01;
        if (yawSpeed > 0.5f) {
            if (targetIsMoving) {
                FuckingNIgger.ACCURATE_ROTATION_YAW_LEVEL1 += 2.0 - bestYawRotationDistance / 0.8;
                FuckingNIgger.ACCURATE_ROTATION_YAW_LEVEL1 = Math.max(0.0, FuckingNIgger.ACCURATE_ROTATION_YAW_LEVEL1);
                final int suspiciousLevel = (int)FuckingNIgger.ACCURATE_ROTATION_YAW_LEVEL1;
                if (suspiciousLevel > 12) {
                    ++FuckingNIgger.ACCURATE_ROTATION_YAW_VL1;
                    if (FuckingNIgger.ACCURATE_ROTATION_YAW_VL1 > 3.0) {}
                }
            }
        }
        else if (FuckingNIgger.ACCURATE_ROTATION_YAW_VL1 > 0.0) {
            FuckingNIgger.ACCURATE_ROTATION_YAW_VL1 -= 1.0E-5;
        }
        if (pitchSpeed > 0.5f) {
            if (targetIsMoving && bestPitchRotationDistance == 0.0 && prevPitch != pitch) {
                FuckingNIgger.ACCURATE_ROTATION_PITCH_LEVEL1 += 2.0 - bestPitchRotationDistance / 0.8;
                FuckingNIgger.ACCURATE_ROTATION_PITCH_LEVEL1 = Math.max(0.0, FuckingNIgger.ACCURATE_ROTATION_PITCH_LEVEL1);
                final int suspiciousLevel = (int)FuckingNIgger.ACCURATE_ROTATION_PITCH_LEVEL1;
                if (suspiciousLevel > 8) {
                    ++FuckingNIgger.ACCURATE_ROTATION_PITCH_VL1;
                    if (FuckingNIgger.ACCURATE_ROTATION_PITCH_VL1 > 3.0) {}
                }
            }
        }
        else if (FuckingNIgger.ACCURATE_ROTATION_PITCH_VL1 > 0.0) {
            FuckingNIgger.ACCURATE_ROTATION_PITCH_VL1 -= 1.0E-5;
        }
    }
}
