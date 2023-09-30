package io.github.raze.utilities.collection.math;

import io.github.raze.utilities.system.BaseUtility;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class RotationUtil implements BaseUtility {

    public static float getAngleDifference(float a, float b) {
        return ((((a - b) % 360F) + 540F) % 360F) - 180F;
    }

    public static float[] getRotationsToBlock(double blockX, double blockY, double blockZ) {

        double x = blockX - (mc.thePlayer.posX);
        double y = blockY - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight() / 2);
        double z = blockZ - (mc.thePlayer.posZ);

        double distance = MathHelper.sqrt_double(x * x + z * z);

        float yaw   = (float) ( (Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F);
        float pitch = (float) (-(Math.atan2(y, distance) * 180.0D / Math.PI));

        return new float[] { yaw, pitch };
    }

    public static float[] getRotationsToEntity(EntityLivingBase entity, double prediction, double random) {
        float[] rotations = getRotationsToBlock(
                entity.posX + (entity.posX - entity.lastTickPosX) * prediction,
                entity.posY + (entity.posY - entity.lastTickPosY) * prediction,
                entity.posZ + (entity.posZ - entity.lastTickPosZ) * prediction
        );

        float yaw   = rotations[0];
        float pitch = rotations[1];

        float randomYaw = 0.0F,
              randomPitch = 0.0F;

        if (random != 0) {
            randomYaw   += ((Math.random() - 0.5) * random) / 2;
            randomYaw   += ((Math.random() - 0.5) * random) / 2;
            randomPitch += ((Math.random() - 0.5) * random) / 2;

            if (mc.thePlayer.ticksExisted % 5 == 0) {
                randomYaw = (float) (((Math.random() - 0.5) * random) / 2);
                randomPitch = (float) (((Math.random() - 0.5) * random) / 2);
            }

            yaw += randomYaw;
            pitch += randomPitch;
        }

        return new float[] { yaw, pitch };
    }

    public static float[] getFixedRotations(float[] rotations, float[] lastRotations) {
        Minecraft mc = Minecraft.getMinecraft();

        float yaw = rotations[0];
        float pitch = rotations[1];

        float lastYaw = lastRotations[0];
        float lastPitch = lastRotations[1];

        float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        float gcd = f * f * f * 1.2F;

        float deltaYaw = yaw - lastYaw;
        float deltaPitch = pitch - lastPitch;

        float fixedDeltaYaw = deltaYaw - (deltaYaw % gcd);
        float fixedDeltaPitch = deltaPitch - (deltaPitch % gcd);

        float fixedYaw = lastYaw + fixedDeltaYaw;
        float fixedPitch = lastPitch + fixedDeltaPitch;

        return new float[] { fixedYaw, fixedPitch };
    }

    public static boolean raytrace(float yaw, float pitch, double reach, EntityLivingBase target) {
        Vec3 a = mc.thePlayer.getPositionEyes(mc.timer.renderPartialTicks);
        Vec3 b = mc.thePlayer.getVectorForRotation(MathUtil.clamp(pitch, -90.F, 90.F), yaw % 360);
        Vec3 c = a.addVector(b.xCoord * reach, b.yCoord * reach, b.zCoord * reach);

        MovingObjectPosition objectPosition = target.getEntityBoundingBox().calculateIntercept(a, c);

        return (objectPosition != null && objectPosition.hitVec != null);
    }

    public static Vec3 getVectorForRotation(float pitch, float yaw) {
        float f = MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);

        return new Vec3(f1 * f2, f3, f * f2);
    }

}
