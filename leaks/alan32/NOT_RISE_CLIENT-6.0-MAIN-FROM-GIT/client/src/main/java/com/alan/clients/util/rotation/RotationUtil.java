package com.alan.clients.util.rotation;

import com.alan.clients.util.RayCastUtil;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.math.MathConst;
import com.alan.clients.util.vector.Vector2f;
import com.alan.clients.util.vector.Vector3d;
import lombok.experimental.UtilityClass;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

/**
 * @author Patrick
 * @since 11/17/2021
 */

@UtilityClass
public class RotationUtil implements InstanceAccess {

    public Vector2f calculate(final Vector3d from, final Vector3d to) {
        final Vector3d diff = to.subtract(from);
        final double distance = Math.hypot(diff.getX(), diff.getZ());
        final float yaw = (float) (MathHelper.atan2(diff.getZ(), diff.getX()) * MathConst.TO_DEGREES) - 90.0F;
        final float pitch = (float) (-(MathHelper.atan2(diff.getY(), distance) * MathConst.TO_DEGREES));
        return new Vector2f(yaw, pitch);
    }

    public Vector2f calculate(final Entity entity) {
        return calculate(entity.getCustomPositionVector().add(0, Math.max(0, Math.min(mc.thePlayer.posY - entity.posY +
                mc.thePlayer.getEyeHeight(), (entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY) * 0.9)), 0));
    }

    public Vector2f calculate(final Entity entity, final boolean adaptive, final double range) {
        Vector2f normalRotations = calculate(entity);
        if (!adaptive || RayCastUtil.rayCast(normalRotations, range).typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
            return normalRotations;
        }

        for (double yPercent = 1; yPercent >= 0; yPercent -= 0.25) {
            for (double xPercent = 1; xPercent >= -0.5; xPercent -= 0.5) {
                for (double zPercent = 1; zPercent >= -0.5; zPercent -= 0.5) {
                    Vector2f adaptiveRotations = calculate(entity.getCustomPositionVector().add(
                            (entity.getEntityBoundingBox().maxX - entity.getEntityBoundingBox().minX) * xPercent,
                            (entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY) * yPercent,
                            (entity.getEntityBoundingBox().maxZ - entity.getEntityBoundingBox().minZ) * zPercent));

                    if (RayCastUtil.rayCast(adaptiveRotations, range).typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                        return adaptiveRotations;
                    }
                }
            }
        }

        return normalRotations;
    }

    public Vector2f calculate(final Vec3 to, final EnumFacing enumFacing) {
        return calculate(new Vector3d(to.xCoord, to.yCoord, to.zCoord), enumFacing);
    }

    public Vector2f calculate(final Vec3 to) {
        return calculate(mc.thePlayer.getCustomPositionVector().add(0, mc.thePlayer.getEyeHeight(), 0), new Vector3d(to.xCoord, to.yCoord, to.zCoord));
    }

    public Vector2f calculate(final Vector3d to) {
        return calculate(mc.thePlayer.getCustomPositionVector().add(0, mc.thePlayer.getEyeHeight(), 0), to);
    }

    public Vector2f calculate(final Vector3d position, final EnumFacing enumFacing) {
        double x = position.getX() + 0.5D;
        double y = position.getY() + 0.5D;
        double z = position.getZ() + 0.5D;

        x += (double) enumFacing.getDirectionVec().getX() * 0.5D;
        y += (double) enumFacing.getDirectionVec().getY() * 0.5D;
        z += (double) enumFacing.getDirectionVec().getZ() * 0.5D;
        return calculate(new Vector3d(x, y, z));
    }

    public Vector2f applySensitivityPatch(final Vector2f rotation) {
        final Vector2f previousRotation = mc.thePlayer.getPreviousRotation();
        final float mouseSensitivity = (float) (mc.gameSettings.mouseSensitivity * (1 + Math.random() / 10000000) * 0.6F + 0.2F);
        final double multiplier = mouseSensitivity * mouseSensitivity * mouseSensitivity * 8.0F * 0.15D;
        final float yaw = previousRotation.x + (float) (Math.round((rotation.x - previousRotation.x) / multiplier) * multiplier);
        final float pitch = previousRotation.y + (float) (Math.round((rotation.y - previousRotation.y) / multiplier) * multiplier);
        return new Vector2f(yaw, MathHelper.clamp_float(pitch, -90, 90));
    }

    public Vector2f applySensitivityPatch(final Vector2f rotation, final Vector2f previousRotation) {
        final float mouseSensitivity = (float) (mc.gameSettings.mouseSensitivity * (1 + Math.random() / 10000000) * 0.6F + 0.2F);
        final double multiplier = mouseSensitivity * mouseSensitivity * mouseSensitivity * 8.0F * 0.15D;
        final float yaw = previousRotation.x + (float) (Math.round((rotation.x - previousRotation.x) / multiplier) * multiplier);
        final float pitch = previousRotation.y + (float) (Math.round((rotation.y - previousRotation.y) / multiplier) * multiplier);
        return new Vector2f(yaw, MathHelper.clamp_float(pitch, -90, 90));
    }

    public Vector2f relateToPlayerRotation(final Vector2f rotation) {
        final Vector2f previousRotation = mc.thePlayer.getPreviousRotation();
        final float yaw = previousRotation.x + MathHelper.wrapAngleTo180_float(rotation.x - previousRotation.x);
        final float pitch = MathHelper.clamp_float(rotation.y, -90, 90);
        return new Vector2f(yaw, pitch);
    }

    public Vector2f resetRotation(final Vector2f rotation) {
        if (rotation == null) {
            return null;
        }

        final float yaw = rotation.x + MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw - rotation.x);
        final float pitch = mc.thePlayer.rotationPitch;
        return new Vector2f(yaw, pitch);
    }

    public Vector2f smooth(final Vector2f lastRotation, final Vector2f targetRotation, final double speed) {
        float yaw = targetRotation.x;
        float pitch = targetRotation.y;
        final float lastYaw = lastRotation.x;
        final float lastPitch = lastRotation.y;

        if (speed != 0) {
            final float rotationSpeed = (float) speed;

            final double deltaYaw = MathHelper.wrapAngleTo180_float(targetRotation.x - lastRotation.x);
            final double deltaPitch = pitch - lastPitch;

            final double distance = Math.sqrt(deltaYaw * deltaYaw + deltaPitch * deltaPitch);
            final double distributionYaw = Math.abs(deltaYaw / distance);
            final double distributionPitch = Math.abs(deltaPitch / distance);

            final double maxYaw = rotationSpeed * distributionYaw;
            final double maxPitch = rotationSpeed * distributionPitch;

            final float moveYaw = (float) Math.max(Math.min(deltaYaw, maxYaw), -maxYaw);
            final float movePitch = (float) Math.max(Math.min(deltaPitch, maxPitch), -maxPitch);

            yaw = lastYaw + moveYaw;
            pitch = lastPitch + movePitch;

            for (int i = 1; i <= (int) (Minecraft.getDebugFPS() / 20f + Math.random() * 10); ++i) {

                if (Math.abs(moveYaw) + Math.abs(movePitch) > 1) {
                    yaw += (Math.random() - 0.5) / 1000;
                    pitch -= Math.random() / 200;
                }

                /*
                 * Fixing GCD
                 */
                final Vector2f rotations = new Vector2f(yaw, pitch);
                final Vector2f fixedRotations = RotationUtil.applySensitivityPatch(rotations);

                /*
                 * Setting rotations
                 */
                yaw = fixedRotations.x;
                pitch = Math.max(-90, Math.min(90, fixedRotations.y));
            }
        }

        return new Vector2f(yaw, pitch);
    }
}