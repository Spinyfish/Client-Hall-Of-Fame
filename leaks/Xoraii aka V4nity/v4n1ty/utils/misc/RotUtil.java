package v4n1ty.utils.misc;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.RandomUtils;

public class RotUtil {
    private static final Minecraft MC = Minecraft.getMinecraft();
    private static final Random RANDOM = new Random();

    public static float[] doBasicRotations(Entity entity) {
        if (entity != null) {
            double diffX = entity.posX - MC.thePlayer.posX;
            double diffY;
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
                diffY = entityLivingBase.posY + (double)entityLivingBase.getEyeHeight() * 0.9 - (MC.thePlayer.posY + (double)MC.thePlayer.getEyeHeight());
            } else {
                diffY = (entity.getEntityBoundingBox().minY + entity.getEntityBoundingBox().maxY) / 2.0
                        - (MC.thePlayer.posY + (double)MC.thePlayer.getEyeHeight());
            }

            double diffZ = entity.posZ - MC.thePlayer.posZ;
            double dist = Math.hypot(diffX, diffZ);
            float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90.0F;
            float pitch = MathHelper.clamp_float((float)(-(Math.atan2(diffY, dist) * 180.0 / Math.PI)), -90.0F, 90.0F);
            return new float[]{
                    MC.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - MC.thePlayer.rotationYaw),
                    MC.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - MC.thePlayer.rotationPitch)
            };
        } else {
            return null;
        }
    }

    public static float[] doBypassIRotations(Entity entity) {
        if (entity != null) {
            double diffX = entity.posX - MC.thePlayer.posX;
            double diffY;
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
                diffY = entityLivingBase.posY + (double)entityLivingBase.getEyeHeight() * 0.9 - (MC.thePlayer.posY + (double)MC.thePlayer.getEyeHeight());
            } else {
                diffY = (entity.getEntityBoundingBox().minY + entity.getEntityBoundingBox().maxY) / 2.0
                        - (MC.thePlayer.posY + (double)MC.thePlayer.getEyeHeight());
            }

            double diffZ = entity.posZ - MC.thePlayer.posZ;
            double dist = Math.hypot(diffX, diffZ);
            float sensitivity = MC.gameSettings.mouseSensitivity * 0.6F + 0.2F;
            float gcd = sensitivity * sensitivity * sensitivity * 1.2F;
            float yawRand = RANDOM.nextBoolean() ? -RandomUtils.nextFloat(0.0F, 3.0F) : RandomUtils.nextFloat(0.0F, 3.0F);
            float pitchRand = RANDOM.nextBoolean() ? -RandomUtils.nextFloat(0.0F, 3.0F) : RandomUtils.nextFloat(0.0F, 3.0F);
            float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90.0F + yawRand;
            float pitch = MathHelper.clamp_float(
                    (float)(-(Math.atan2(diffY, dist) * 180.0 / Math.PI))
                            + pitchRand
                            + MathHelper.clamp_float(MC.thePlayer.getDistanceToEntity(entity) * 1.25F, 0.0F, 6.0F),
                    -90.0F,
                    90.0F
            );
            if (MC.thePlayer.ticksExisted % 2 == 0) {
                pitch = MathHelper.clamp_float(
                        pitch + (RANDOM.nextBoolean() ? RandomUtils.nextFloat(2.0F, 8.0F) : -RandomUtils.nextFloat(2.0F, 8.0F)), -90.0F, 90.0F
                );
            }

            pitch -= pitch % gcd;
            yaw -= yaw % gcd;
            return new float[]{
                    MC.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - MC.thePlayer.rotationYaw),
                    MC.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - MC.thePlayer.rotationPitch)
            };
        } else {
            return null;
        }
    }

    public static float[] doBypassIIRotations(Entity entity) {
        if (entity != null) {
            double diffX = entity.posX - MC.thePlayer.posX;
            double diffZ = entity.posZ - MC.thePlayer.posZ;
            double dist = Math.hypot(diffX, diffZ);
            double diffY;
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
                diffY = entityLivingBase.posY + (double)entityLivingBase.getEyeHeight() * 0.9 - (MC.thePlayer.posY + (double)MC.thePlayer.getEyeHeight());
            } else {
                diffY = (entity.getEntityBoundingBox().minY + entity.getEntityBoundingBox().maxY) / 2.0
                        - (MC.thePlayer.posY + (double)MC.thePlayer.getEyeHeight());
            }

            float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0 / Math.PI) - 90.0F;
            float pitch = MathHelper.clamp_float((float)(-(Math.atan2(diffY, dist) * 180.0 / Math.PI)), -90.0F, 90.0F);
            return new float[]{
                    MC.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - MC.thePlayer.rotationYaw),
                    MC.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - MC.thePlayer.rotationPitch)
            };
        } else {
            return null;
        }
    }
}