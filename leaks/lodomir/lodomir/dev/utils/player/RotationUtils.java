/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.utils.player;

import java.util.ArrayList;
import java.util.HashSet;
import lodomir.dev.utils.player.MovementUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class RotationUtils {
    public static float[] fixRotations(float[] rotations, float[] lastRotations) {
        Minecraft mc = Minecraft.getMinecraft();
        float yaw = rotations[0];
        float pitch = rotations[1];
        float lastYaw = lastRotations[0];
        float lastPitch = lastRotations[1];
        float f = mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
        float gcd = f * f * f * 1.2f;
        float deltaYaw = yaw - lastYaw;
        float deltaPitch = pitch - lastPitch;
        float fixedDeltaYaw = deltaYaw - deltaYaw % gcd;
        float fixedDeltaPitch = deltaPitch - deltaPitch % gcd;
        float fixedYaw = lastYaw + fixedDeltaYaw;
        float fixedPitch = lastPitch + fixedDeltaPitch;
        return new float[]{fixedYaw, fixedPitch};
    }

    public static MovingObjectPosition rayCast(EntityPlayerSP player, double x, double y, double z) {
        HashSet<Entity> excluded = new HashSet<Entity>();
        excluded.add(player);
        return RotationUtils.tracePathD(player.worldObj, player.posX, player.posY + (double)player.getEyeHeight(), player.posZ, x, y, z, 1.0f, excluded);
    }

    private static MovingObjectPosition tracePathD(World w, double posX, double posY, double posZ, double v, double v1, double v2, float borderSize, HashSet<Entity> exclude) {
        return RotationUtils.tracePath(w, (float)posX, (float)posY, (float)posZ, (float)v, (float)v1, (float)v2, borderSize, exclude);
    }

    private static MovingObjectPosition tracePath(World world, float x, float y, float z, float tx, float ty, float tz, float borderSize, HashSet<Entity> excluded) {
        Vec3 startVec = new Vec3(x, y, z);
        Vec3 endVec = new Vec3(tx, ty, tz);
        float minX = x < tx ? x : tx;
        float minY = y < ty ? y : ty;
        float minZ = z < tz ? z : tz;
        float maxX = x > tx ? x : tx;
        float maxY = y > ty ? y : ty;
        float maxZ = z > tz ? z : tz;
        AxisAlignedBB bb = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ).expand(borderSize, borderSize, borderSize);
        ArrayList allEntities = (ArrayList)world.getEntitiesWithinAABBExcludingEntity(null, bb);
        MovingObjectPosition blockHit = world.rayTraceBlocks(startVec, endVec);
        startVec = new Vec3(x, y, z);
        endVec = new Vec3(tx, ty, tz);
        Entity closestHitEntity = null;
        float closestHit = Float.POSITIVE_INFINITY;
        for (Entity ent : allEntities) {
            float currentHit;
            MovingObjectPosition intercept;
            if (!ent.canBeCollidedWith() || excluded.contains(ent)) continue;
            float entBorder = ent.getCollisionBorderSize();
            AxisAlignedBB entityBb = ent.getEntityBoundingBox();
            if (entityBb == null || (intercept = (entityBb = entityBb.expand(entBorder, entBorder, entBorder)).calculateIntercept(startVec, endVec)) == null || (currentHit = (float)intercept.hitVec.distanceTo(startVec)) >= closestHit && currentHit != 0.0f) continue;
            closestHit = currentHit;
            closestHitEntity = ent;
        }
        if (closestHitEntity != null) {
            blockHit = new MovingObjectPosition(closestHitEntity);
        }
        return blockHit;
    }

    public static float updateRotation(float current, float intended, float speed) {
        float f = MathHelper.wrapAngleTo180_float(intended - current);
        if (f > speed) {
            f = speed;
        }
        if (f < -speed) {
            f = -speed;
        }
        return current + f;
    }

    public static float[] getRotations(EntityLivingBase entity) {
        double deltaX = entity.posX + (entity.posX - entity.lastTickPosX) - MovementUtils.mc.thePlayer.posX;
        double deltaY = entity.posY - 3.5 + (double)entity.getEyeHeight() - MovementUtils.mc.thePlayer.posY + (double)MovementUtils.mc.thePlayer.getEyeHeight();
        double deltaZ = entity.posZ + (entity.posZ - entity.lastTickPosZ) - MovementUtils.mc.thePlayer.posZ;
        double distance = Math.sqrt(Math.pow(deltaX, 2.0) + Math.pow(deltaZ, 2.0));
        float yaw = (float)Math.toDegrees(-Math.atan(deltaX / deltaZ));
        float pitch = (float)(-Math.toDegrees(Math.atan(deltaY / distance)));
        if (deltaX < 0.0 && deltaZ < 0.0) {
            yaw = (float)(90.0 + Math.toDegrees(Math.atan(deltaZ / deltaX)));
        } else if (deltaX > 0.0 && deltaZ < 0.0) {
            yaw = (float)(-90.0 + Math.toDegrees(Math.atan(deltaZ / deltaX)));
        }
        return new float[]{yaw, pitch};
    }

    public static float getDistanceToEntity(EntityLivingBase entityLivingBase) {
        return Minecraft.getMinecraft().thePlayer.getDistanceToEntity(entityLivingBase);
    }

    public static float getAngleChange(EntityLivingBase entityIn) {
        float yaw = RotationUtils.getNeededRotations(entityIn)[0];
        float pitch = RotationUtils.getNeededRotations(entityIn)[1];
        float playerYaw = Minecraft.getMinecraft().thePlayer.rotationYaw;
        float playerPitch = Minecraft.getMinecraft().thePlayer.rotationPitch;
        if (playerYaw < 0.0f) {
            playerYaw += 360.0f;
        }
        if (playerPitch < 0.0f) {
            playerPitch += 360.0f;
        }
        if (yaw < 0.0f) {
            yaw += 360.0f;
        }
        if (pitch < 0.0f) {
            pitch += 360.0f;
        }
        float yawChange = Math.max(playerYaw, yaw) - Math.min(playerYaw, yaw);
        float pitchChange = Math.max(playerPitch, pitch) - Math.min(playerPitch, pitch);
        return yawChange + pitchChange;
    }

    public static float[] getNeededRotations(EntityLivingBase entityIn) {
        double d0 = entityIn.posX - Minecraft.getMinecraft().thePlayer.posX;
        double d2 = entityIn.posZ - Minecraft.getMinecraft().thePlayer.posZ;
        double d3 = entityIn.posY + (double)entityIn.getEyeHeight() - (Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().minY + (double)Minecraft.getMinecraft().thePlayer.getEyeHeight());
        double d4 = MathHelper.sqrt_double(d0 * d0 + d2 * d2);
        float f = (float)(MathHelper.func_181159_b(d2, d0) * 180.0 / Math.PI) - 90.0f;
        float f2 = (float)(-(MathHelper.func_181159_b(d3, d4) * 180.0 / Math.PI));
        return new float[]{f, f2};
    }
}

