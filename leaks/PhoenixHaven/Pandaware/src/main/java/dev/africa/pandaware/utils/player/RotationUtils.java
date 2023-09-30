package dev.africa.pandaware.utils.player;

import dev.africa.pandaware.api.interfaces.MinecraftInstance;
import dev.africa.pandaware.utils.math.vector.Vec2f;
import lombok.AllArgsConstructor;
import lombok.experimental.UtilityClass;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.*;

@UtilityClass
public class RotationUtils implements MinecraftInstance {
    public Vec2f getRotations(EntityLivingBase theEntity, RotationAt rotationAt) {
        if (theEntity == null || rotationAt == null || mc.thePlayer == null) return new Vec2f(0, 0);

        float rotationTypeValue = 3;

        double xDistance;
        double zDistance;
        double yDistance;

        float yaw, pitch;
        if (rotationAt.equals(RotationAt.HEAD))
            rotationTypeValue = 1.314f;
        if (rotationAt.equals(RotationAt.LEGS))
            rotationTypeValue = 9.7236f;
        if (rotationAt.equals(RotationAt.FEET))
            rotationTypeValue = 194.472f;

        xDistance = theEntity.posX - mc.thePlayer.posX;
        zDistance = theEntity.posZ - mc.thePlayer.posZ;
        yDistance = theEntity.posY + (theEntity.getEyeHeight() - 0.1D) / rotationTypeValue - mc.thePlayer.posY - mc.thePlayer.getEyeHeight() / 1.4D;
        double angleHelper = MathHelper.sqrt_double(xDistance * xDistance + zDistance * zDistance);
        yaw = (float) Math.toDegrees(-Math.atan(xDistance / zDistance));
        pitch = (float) -Math.toDegrees(Math.atan(yDistance / angleHelper));
        double v = Math.toDegrees(Math.atan(zDistance / xDistance));
        if ((zDistance < 0.0D) && (xDistance < 0.0D)) {
            yaw = (float) (90.0D + v);
        } else if ((zDistance < 0.0D) && (xDistance > 0.0D)) {
            yaw = (float) (-90.0D + v);
        }

        return new Vec2f(yaw, pitch >= 90 ? 90 : pitch <= -90 ? -90 : pitch);
    }

    public Vec2f getMiddlePointRotations(Entity entity, RotationAt rotationAt) {
        if (entity == null || mc.thePlayer == null) return new Vec2f(0, 0);

        float rotationTypeValue = 0.3f;

        if (rotationAt.equals(RotationAt.HEAD))
            rotationTypeValue = 0;
        if (rotationAt.equals(RotationAt.LEGS))
            rotationTypeValue = 0.6f;
        if (rotationAt.equals(RotationAt.FEET))
            rotationTypeValue = 1f;

        double yDistance = 0;
        double py = (180 / Math.PI);
        Vec3 vector = new Vec3(
                entity.posX - mc.thePlayer.posX,
                (mc.thePlayer.posY + rotationTypeValue + mc.thePlayer.getEyeHeight()),
                entity.posZ - mc.thePlayer.posZ
        );

        AxisAlignedBB axisAlignedBB = entity.getEntityBoundingBox().expand(0.1, 0.1 - rotationTypeValue, 0.1);
        if (vector.yCoord > axisAlignedBB.maxY) {
            yDistance = axisAlignedBB.maxY - vector.yCoord;
        } else if (vector.yCoord < axisAlignedBB.minY) {
            yDistance = axisAlignedBB.minY - vector.yCoord;
        }

        double squareRoot = MathHelper.sqrt_double((vector.getX() * vector.getX()) + (vector.getZ() * vector.getZ()));

        float yaw = (float) (Math.atan2(vector.getZ(), vector.getX()) * py) - 90;
        float pitch = (float) -(Math.atan2(yDistance, squareRoot) * py);
        return new Vec2f(yaw, pitch);
    }

    public float smoothRotations(float last, float current, float speed) {
        float f = MathHelper.wrapAngleTo180_float(current - last);

        if (f > speed) {
            f = speed;
        }

        if (f < -speed) {
            f = -speed;
        }

        return last + f;
    }

    public Vec2f getRotations(EntityLivingBase theEntity) {
        double xDistance = theEntity.posX - mc.thePlayer.posX;
        double zDistance = theEntity.posZ - mc.thePlayer.posZ;
        double yDistance = theEntity.posY + (theEntity.getEyeHeight() - 0.1D) / 3 - mc.thePlayer.posY - mc.thePlayer.getEyeHeight() / 1.4D;
        double angleHelper = MathHelper.sqrt_double(xDistance * xDistance + zDistance * zDistance);
        float yaw = (float) Math.toDegrees(-Math.atan(xDistance / zDistance));
        float pitch = (float) -Math.toDegrees(Math.atan(yDistance / angleHelper));
        double v = Math.toDegrees(Math.atan(zDistance / xDistance));
        if ((zDistance < 0.0D) && (xDistance < 0.0D)) {
            yaw = (float) (90.0D + v);
        } else if ((zDistance < 0.0D) && (xDistance > 0.0D)) {
            yaw = (float) (-90.0D + v);
        }
        return new Vec2f(yaw, pitch >= 90 ? 90 : pitch <= -90 ? -90 : pitch);
    }

    public double getYawRotationDifference(EntityLivingBase target) {
        if (Math.abs(getRotations(target, RotationAt.CHEST).getX() - mc.thePlayer.rotationYaw) % 360.0f > 180.0f) {
            return 360.0f - Math.abs(getRotations(target, RotationAt.CHEST).getX() - mc.thePlayer.rotationYaw) % 360.0f;
        }

        return Math.abs(getRotations(target, RotationAt.CHEST).getX() - mc.thePlayer.rotationYaw) % 360.0f;
    }

    public double getPitchRotationDifference(EntityLivingBase target) {
        if (Math.abs(getRotations(target, RotationAt.CHEST).getY() - mc.thePlayer.rotationPitch) % 90.0f > 45.0f) {
            return 90.0f - Math.abs(getRotations(target, RotationAt.CHEST).getX() - mc.thePlayer.rotationYaw) % 90.0f;
        }

        return Math.abs(getRotations(target, RotationAt.CHEST).getY() - mc.thePlayer.rotationPitch) % 90.0f;
    }

    public double getRotationDifference(EntityLivingBase target) {
        return getYawRotationDifference(target) + getPitchRotationDifference(target);
    }

    public Vec2f getBlockRotations(Vec3 vector) {
        if (vector == null || mc.thePlayer == null) return new Vec2f(0, 0);

        Vec3 eyesPos = new Vec3(
                mc.thePlayer.posX,
                mc.thePlayer.posY + mc.thePlayer.getEyeHeight(),
                mc.thePlayer.posZ
        );

        double diffX = vector.xCoord - eyesPos.xCoord;
        double diffY = vector.yCoord - eyesPos.yCoord;
        double diffZ = vector.zCoord - eyesPos.zCoord;

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        return new Vec2f(
                (float) MathHelper.wrapAngleTo180_double(Math.toDegrees(Math.atan2(diffZ, diffX)) - 90f),
                (float) MathHelper.wrapAngleTo180_double(-Math.toDegrees(Math.atan2(diffY, diffXZ)))
        );
    }

    public static Vec2f getBlockRotations(BlockPos blockPos) {
        if (blockPos == null || mc.thePlayer == null) {
            return new Vec2f();
        }
        double deltaX = (blockPos.getX() + 0.5) - mc.thePlayer.posX;
        double deltaY = (blockPos.getY() + 0.5) - (1.6D + 0.2125) - 0.4D - mc.thePlayer.posY;
        double deltaZ = (blockPos.getZ() + 0.5) - mc.thePlayer.posZ;

        double distanceXZ = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ);
        double pitchToEntity = -Math.toDegrees(Math.atan(deltaY / distanceXZ));

        double yawToEntity;
        double v = Math.toDegrees(Math.atan(deltaZ / deltaX));
        if ((deltaZ < 0.0D) && (deltaX < 0.0D)) {
            yawToEntity = 90.0D + v;
        } else {
            if ((deltaZ < 0.0D) && (deltaX > 0.0D)) {
                yawToEntity = -90.0D + v;
            } else {
                yawToEntity = Math.toDegrees(-Math.atan(deltaX / deltaZ));
            }
        }
        float yawChangeToEntity = MathHelper.wrapAngleTo180_float(-(mc.thePlayer.rotationYaw - (float) yawToEntity));
        float pitchChangeToEntity = -MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationPitch - (float) pitchToEntity);

        return new Vec2f(yawChangeToEntity + mc.thePlayer.rotationYaw, pitchChangeToEntity + mc.thePlayer.rotationPitch);
    }

    public float updateRotation(float playerRotation, float targetRotation, float maxSpeed) {
        float speed = MathHelper.wrapAngleTo180_float(targetRotation - playerRotation);

        if (speed > maxSpeed) {
            speed = maxSpeed;
        }
        if (speed < -maxSpeed) {
            speed = -maxSpeed;
        }

        return playerRotation + speed;
    }

    public Vec3 getVectorForRotation(float yaw, float pitch) {
        float yawCos = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float yawSin = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        float pitchCos = -MathHelper.cos(-pitch * 0.017453292F);
        float pitchSin = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3(yawSin * pitchCos, pitchSin, yawCos * pitchCos);
    }

    public MovingObjectPosition getMovingObjectPositionFromVector(float yaw, float pitch) {
        Vec3 eyesPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
        Vec3 rotationVector = RotationUtils.getVectorForRotation(yaw, pitch);
        Vec3 vector = eyesPos.addVector(rotationVector.xCoord * 4, rotationVector.yCoord * 4, rotationVector.zCoord * 4);

        return mc.theWorld.rayTraceBlocks(eyesPos, vector, false, false, true);
    }

    public float getAngleDifference(final float a, final float b) {
        return ((((a - b) % 360F) + 540F) % 360F) - 180F;
    }

    public static Vec3 getBestVector(Vec3 look, AxisAlignedBB axisAlignedBB) {
        return new Vec3(MathHelper.clamp_double(look.xCoord, axisAlignedBB.minX, axisAlignedBB.maxX), MathHelper.clamp_double(look.yCoord, axisAlignedBB.minY, axisAlignedBB.maxY), MathHelper.clamp_double(look.zCoord, axisAlignedBB.minZ, axisAlignedBB.maxZ));
    }

    @AllArgsConstructor
    public enum RotationAt {
        HEAD("Head"),
        CHEST("Chest"),
        LEGS("Legs"),
        FEET("Feet");

        private String label;

        @Override
        public String toString() {
            return label;
        }
    }
}