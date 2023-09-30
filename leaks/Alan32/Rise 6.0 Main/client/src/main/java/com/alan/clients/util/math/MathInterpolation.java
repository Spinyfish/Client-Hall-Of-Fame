package com.alan.clients.util.math;

import com.alan.clients.util.vector.Vector3d;
import net.minecraft.entity.Entity;

/**
 * @author Patrick
 * @since 10/17/2021
 */
public class MathInterpolation {

    /**
     * Interpolates a value relating to a multiplier and its last value
     *
     * @param current    current value
     * @param previous   previous value
     * @param multiplier multiplier used for difference of current and previous value
     * @return interpolated value
     */
    public static double interpolate(final double current, final double previous, final double multiplier) {
        return previous + (current - previous) * multiplier;
    }

    /**
     * Interpolates a value relating to a multiplier and its last value
     *
     * @param current    current value
     * @param previous   previous value
     * @param multiplier multiplier used for difference of current and previous value
     * @return interpolated value
     */
    public static float interpolate(final float current, final float previous, final float multiplier) {
        return previous + (current - previous) * multiplier;
    }

    /**
     * Interpolates a vector relating to a multiplier and its last vector
     *
     * @param currentVector  current value
     * @param previousVector previous value
     * @param multiplier     multiplier used for difference of current and previous value
     * @return interpolated value
     */
    public static Vector3d interpolate(final Vector3d currentVector, final Vector3d previousVector, final double multiplier) {
        return new Vector3d(
                interpolate(currentVector.getX(), previousVector.getX(), multiplier),
                interpolate(currentVector.getY(), previousVector.getY(), multiplier),
                interpolate(currentVector.getZ(), previousVector.getZ(), multiplier)
        );
    }

    /**
     * Interpolates an entity using rendering partial ticks
     *
     * @param entity       entity to interpolate the position of
     * @param partialTicks current render partial ticks
     * @return interpolated position vector
     */
    public static Vector3d interpolate(final Entity entity, final float partialTicks) {
        return new Vector3d(
                interpolate(entity.posX, entity.prevPosX, partialTicks),
                interpolate(entity.posY, entity.prevPosY, partialTicks),
                interpolate(entity.posZ, entity.prevPosZ, partialTicks)
        );
    }
}