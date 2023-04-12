package xyz.unnamed.anticheat.utilities.math;

import lombok.experimental.UtilityClass;

/**
 * Copyright (c) 2022 - Tranquil, LLC.
 *
 * @author incognito@tranquil.cc
 * @date 4/8/2023
 */

@UtilityClass
public class MathUtil {

    public static double hypot(final double x, final double z) {
        return Math.sqrt(x * x + z * z);
    }
}
