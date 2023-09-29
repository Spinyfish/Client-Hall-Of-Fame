package com.alan.clients.util.math;

import net.minecraft.util.MathHelper;

/**
 * @author Patrick
 * @since 10/17/2021
 */
public class MathConst {

    public static final float PI = (float) Math.PI;
    public static final float TO_RADIANS = PI / 180.0F;
    public static final float TO_DEGREES = 180.0F / PI;

    // stores sin/cos values from 0-360°
    public static final float[] COSINE = new float[361];
    public static final float[] SINE = new float[361];

    /**
     * Called once at the start of the client to calculate sin and cosine values from 0-360°
     */
    public static void calculate() {
        for (int i = 0; i <= 360; ++i) {
            COSINE[i] = MathHelper.cos(i * TO_RADIANS);
            SINE[i] = MathHelper.sin(i * TO_RADIANS);
        }
    }

    /**
     * Converts a floating point angle to an integer angle
     *
     * @param angle floating point angle
     * @return integer angle
     */
    public static int toIntDegree(final float angle) {
        return (int) (angle % 360 + 360) % 360;
    }
}