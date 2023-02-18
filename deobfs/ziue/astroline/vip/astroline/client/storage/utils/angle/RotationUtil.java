/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.passive.EntityPig
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.projectile.EntitySnowball
 *  net.minecraft.util.BlockPos
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.MathHelper
 *  net.minecraft.world.World
 */
package vip.astroline.client.storage.utils.angle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class RotationUtil {
    public static Minecraft mc = Minecraft.getMinecraft();

    public static float[] getRotation(Entity a1) {
        double v1 = a1.posX - RotationUtil.mc.thePlayer.posX;
        double v3 = a1.posY + (double)a1.getEyeHeight() * 0.9 - (RotationUtil.mc.thePlayer.posY + (double)RotationUtil.mc.thePlayer.getEyeHeight());
        double v5 = a1.posZ - RotationUtil.mc.thePlayer.posZ;
        double v7 = MathHelper.ceiling_float_int((float)((float)(v1 * v1 + v5 * v5)));
        float v9 = (float)(Math.atan2(v5, v1) * 180.0 / Math.PI) - 90.0f;
        float v10 = (float)(-(Math.atan2(v3, v7) * 180.0 / Math.PI));
        return new float[]{RotationUtil.mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float((float)(v9 - RotationUtil.mc.thePlayer.rotationYaw)), RotationUtil.mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float((float)(v10 - RotationUtil.mc.thePlayer.rotationPitch))};
    }

    public static float[] getAngles(EntityLivingBase entity) {
        if (entity == null) {
            return null;
        }
        EntityPlayerSP player = RotationUtil.mc.thePlayer;
        double diffX = entity.posX - player.posX;
        double diffY = entity.posY + (double)entity.getEyeHeight() * 0.9 - (player.posY + (double)player.getEyeHeight());
        double diffZ = entity.posZ - player.posZ;
        double dist = MathHelper.sqrt_double((double)(diffX * diffX + diffZ * diffZ));
        float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)(-(Math.atan2(diffY, dist) * 180.0 / Math.PI));
        return new float[]{player.rotationYaw + MathHelper.wrapAngleTo180_float((float)(yaw - player.rotationYaw)), player.rotationPitch + MathHelper.wrapAngleTo180_float((float)(pitch - player.rotationPitch))};
    }

    public static float[] getFacingRotations2(int paramInt1, double d, int paramInt3) {
        EntitySnowball localEntityPig = new EntitySnowball((World)Minecraft.getMinecraft().theWorld);
        localEntityPig.posX = (double)paramInt1 + 0.5;
        localEntityPig.posY = d + 0.5;
        localEntityPig.posZ = (double)paramInt3 + 0.5;
        return RotationUtil.getRotationsNeeded((Entity)localEntityPig);
    }

    public static float[] getRotationsNeeded(Entity entity) {
        if (entity == null) {
            return null;
        }
        Minecraft mc = Minecraft.getMinecraft();
        double xSize = entity.posX - mc.thePlayer.posX;
        double ySize = entity.posY + (double)(entity.getEyeHeight() / 2.0f) - (mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight());
        double zSize = entity.posZ - mc.thePlayer.posZ;
        double theta = MathHelper.sqrt_double((double)(xSize * xSize + zSize * zSize));
        float yaw = (float)(Math.atan2(zSize, xSize) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)(-(Math.atan2(ySize, theta) * 180.0 / Math.PI));
        return new float[]{(mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float((float)(yaw - mc.thePlayer.rotationYaw))) % 360.0f, (mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float((float)(pitch - mc.thePlayer.rotationPitch))) % 360.0f};
    }

    public static float[] getRotations(BlockPos pos, EnumFacing facing) {
        return RotationUtil.getRotations(pos.getX(), pos.getY(), pos.getZ(), facing);
    }

    public static float[] getRotations(double x, double y, double z, EnumFacing facing) {
        EntityPig temp = new EntityPig((World)RotationUtil.mc.theWorld);
        temp.posX = x + 0.5;
        temp.posY = y + 0.5;
        temp.posZ = z + 0.5;
        temp.posX += (double)facing.getDirectionVec().getX() * 0.5;
        temp.posY += (double)facing.getDirectionVec().getY() * 0.5;
        temp.posZ += (double)facing.getDirectionVec().getZ() * 0.5;
        return RotationUtil.getRotations((Entity)temp);
    }

    public static float[] getRotationsRadar(Entity target) {
        double var7;
        double var4 = target.posX - RotationUtil.mc.thePlayer.posX;
        double var5 = target.posZ - RotationUtil.mc.thePlayer.posZ;
        if (target instanceof EntityLivingBase) {
            EntityLivingBase var6 = (EntityLivingBase)target;
            var7 = var6.posY + (double)var6.getEyeHeight() - RotationUtil.mc.thePlayer.posY + (double)RotationUtil.mc.thePlayer.getEyeHeight();
        } else {
            var7 = (target.getEntityBoundingBox().minY + target.getEntityBoundingBox().maxY) / 2.0 - RotationUtil.mc.thePlayer.posY + (double)RotationUtil.mc.thePlayer.getEyeHeight();
        }
        double var8 = MathHelper.sqrt_double((double)(var4 * var4 + var5 * var5));
        float var9 = (float)(Math.atan2(var5, var4) * 180.0 / Math.PI) - 90.0f;
        float var10 = (float)(-(Math.atan2(var7 - (target instanceof EntityPlayer ? 0.25 : 0.0), var8) * 180.0 / Math.PI));
        float pitch = RotationUtil.changeRotation(RotationUtil.mc.thePlayer.rotationPitch, var10);
        float yaw = RotationUtil.changeRotation(RotationUtil.mc.thePlayer.rotationYaw, var9);
        return new float[]{yaw, pitch};
    }

    public static float[] getRotations(Entity entity) {
        double diffY;
        if (entity == null) {
            return null;
        }
        double diffX = entity.posX - RotationUtil.mc.thePlayer.posX;
        double diffZ = entity.posZ - RotationUtil.mc.thePlayer.posZ;
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase elb = (EntityLivingBase)entity;
            diffY = elb.posY + (double)elb.getEyeHeight() - (RotationUtil.mc.thePlayer.posY + (double)RotationUtil.mc.thePlayer.getEyeHeight());
        } else {
            diffY = (entity.getEntityBoundingBox().minY + entity.getEntityBoundingBox().maxY) / 2.0 - (RotationUtil.mc.thePlayer.posY + (double)RotationUtil.mc.thePlayer.getEyeHeight());
        }
        double dist = MathHelper.sqrt_double((double)(diffX * diffX + diffZ * diffZ));
        float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)(-(Math.atan2(diffY, dist) * 180.0 / Math.PI));
        return new float[]{yaw, pitch};
    }

    public static boolean isVisibleFOV(Entity e, float fov) {
        return (Math.abs(RotationUtil.getRotations(e)[0] - RotationUtil.mc.thePlayer.rotationYaw) % 360.0f > 180.0f ? 360.0f - Math.abs(RotationUtil.getRotations(e)[0] - RotationUtil.mc.thePlayer.rotationYaw) % 360.0f : Math.abs(RotationUtil.getRotations(e)[0] - RotationUtil.mc.thePlayer.rotationYaw) % 360.0f) <= fov;
    }

    public static float changeRotation(float p_706631, float p_706632) {
        float var4 = MathHelper.wrapAngleTo180_float((float)(p_706632 - p_706631));
        if (var4 > 1000.0f) {
            var4 = 1000.0f;
        }
        if (!(var4 < -1000.0f)) return p_706631 + var4;
        var4 = -1000.0f;
        return p_706631 + var4;
    }
}
