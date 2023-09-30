package de.lirium.util.rotation;

import de.lirium.util.interfaces.IMinecraft;
import god.buddy.aot.BCompiler;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class RotationUtil implements IMinecraft {

    public static float prevYaw, prevPitch, yaw, pitch;

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    public static Vec3d getBestVector(Vec3d look, AxisAlignedBB axisAlignedBB) {
        return new Vec3d(MathHelper.clamp(look.xCoord, axisAlignedBB.minX, axisAlignedBB.maxX), MathHelper.clamp(look.yCoord, axisAlignedBB.minY, axisAlignedBB.maxY), MathHelper.clamp(look.zCoord, axisAlignedBB.minZ, axisAlignedBB.maxZ));
    }

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    public static float[] getRotation(Entity entity, boolean mouseFix, boolean heuristics, boolean prediction) {
        final Vec3d bestVector = getBestVector(mc.player.getPositionEyes(1F), entity.getEntityBoundingBox());
        double x = bestVector.xCoord - mc.player.posX;
        double y = bestVector.yCoord - (mc.player.posY + (double) mc.player.getEyeHeight());
        double z = bestVector.zCoord - mc.player.posZ;

        if (prediction) {
            final boolean sprinting = entity.isSprinting();
            final boolean sprintingPlayer = mc.player.isSprinting();

            final float walkingSpeed = 0.10000000149011612f; //https://minecraft.fandom.com/wiki/Sprinting

            final float sprint = sprinting ? 1.25f : walkingSpeed;
            final float playerSprint = sprintingPlayer ? 1.25f : walkingSpeed;

            final float predictX = (float) ((entity.posX - entity.prevPosX) * sprint);
            final float predictZ = (float) ((entity.posZ - entity.prevPosZ) * sprint);

            final float playerPredictX = (float) ((mc.player.posX - mc.player.prevPosX) * playerSprint);
            final float playerPredictZ = (float) ((mc.player.posZ - mc.player.prevPosZ) * playerSprint);


            if (predictX != 0.0f && predictZ != 0.0f || playerPredictX != 0.0f && playerPredictZ != 0.0f) {
                x += predictX + playerPredictX;
                z += predictZ + playerPredictZ;
            }
        }

        if (heuristics) {
            try {
                x += SecureRandom.getInstanceStrong().nextDouble() * 0.1;
                y += SecureRandom.getInstanceStrong().nextDouble() * 0.1;
                z += SecureRandom.getInstanceStrong().nextDouble() * 0.1;
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        double d3 = MathHelper.sqrt(x * x + z * z);
        float f = (float) (MathHelper.atan2(z, x) * (180D / Math.PI)) - 90.0F;
        float f1 = (float) (-(MathHelper.atan2(y, d3) * (180D / Math.PI)));
        float calcPitch = updateRotation(pitch, f1);
        float calcYaw = updateRotation(yaw, f);
        calcPitch = MathHelper.clamp(calcPitch, -90, 90);
        if (!mouseFix)
            return new float[]{calcYaw, calcPitch};
        return applyMouseFix(calcYaw, calcPitch);
    }

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    public static float[] getRotation(BlockPos pos, AxisAlignedBB block, boolean mouseFix) {
        double x = pos.getX() - mc.player.posX + mc.player.motionX;
        double y = (pos.getY() + (block.maxY - block.minY)) - (mc.player.posY + (double) mc.player.getEyeHeight());
        double z = pos.getZ() - mc.player.posZ + mc.player.motionZ;

        double d3 = MathHelper.sqrt(x * x + z * z);
        float f = (float) (MathHelper.atan2(z, x) * (180D / Math.PI)) - 90.0F;
        float f1 = (float) (-(MathHelper.atan2(y, d3) * (180D / Math.PI)));
        float calcPitch = updateRotation(pitch, f1);
        float calcYaw = updateRotation(yaw, f);
        calcPitch = MathHelper.clamp(calcPitch, -90, 90);
        if (!mouseFix)
            return new float[]{calcYaw, calcPitch};
        return applyMouseFix(calcYaw, calcPitch);
    }

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    public static float[] applyMouseFix(float newYaw, float newPitch) {
        final float sensitivity = Math.max(0.001F, mc.gameSettings.mouseSensitivity);
        final int deltaYaw = (int) ((newYaw - yaw) / ((sensitivity * (sensitivity >= 0.5 ? sensitivity : 1) / 2)));
        final int deltaPitch = (int) ((newPitch - pitch) / ((sensitivity * (sensitivity >= 0.5 ? sensitivity : 1) / 2))) * -1;
        final float f = sensitivity * 0.6F + 0.2F;
        final float f1 = f * f * f * 8.0F;
        final float f2 = (float) deltaYaw * f1;
        final float f3 = (float) deltaPitch * f1;

        final float endYaw = (float) ((double) yaw + (double) f2 * 0.15);
        float endPitch = (float) ((double) pitch - (double) f3 * 0.15);
        endPitch = MathHelper.clamp(endPitch, -90, 90);
        return new float[]{endYaw, endPitch};
    }

    public static float getYaw(final Entity en, final Entity en2) {
        final Vec3d vec = new Vec3d(en2.posX, en2.posY, en2.posZ);
        final double xD = (en.posX) - (vec.xCoord + (en2.posX - en2.lastTickPosX));
        final double zD = (en.posZ) - (vec.zCoord + (en2.posZ - en2.lastTickPosZ));
        return (float) ((float) Math.atan2(zD, xD) / Math.PI * 180) - 90;
    }

    public static float[] calculateRotationDiff(final float yaw, final float yaw1) {
        float y = Math.abs(yaw - yaw1);
        if (y < 0) y += 360;
        if (y >= 360) y -= 360;
        final float y1 = 360 - y;
        float oneoranother = 0;
        if (y > y1) oneoranother++;
        if (y > y1) y = y1;
        return new float[]{y, oneoranother};
    }

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    public static EnumFacing getFacing(BlockPos from, BlockPos where) {
        final int deltaX = where.getX() - from.getX();
        final int deltaY = where.getY() - from.getY();
        final int deltaZ = where.getZ() - from.getZ();
        if (deltaX >= 1)
            return EnumFacing.EAST;
        else if (deltaX <= -1) {
            return EnumFacing.WEST;
        }

        if (deltaY >= 1) {
            return EnumFacing.UP;
        } else if (deltaY <= -1) {
            return EnumFacing.DOWN;
        }

        if (deltaZ >= 1) {
            return EnumFacing.SOUTH;
        } else if (deltaZ <= -1) {
            return EnumFacing.NORTH;
        }
        return null;
    }

    private static float updateRotation(float p_75652_1_, float p_75652_2_) {
        float f = MathHelper.wrapDegrees(p_75652_2_ - p_75652_1_);

        if (f > (float) 180) {
            f = (float) 180;
        }

        if (f < -(float) 180) {
            f = -(float) 180;
        }

        return p_75652_1_ + f;
    }
}
