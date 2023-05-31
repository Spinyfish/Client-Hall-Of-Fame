/*
 * Copyright Felix Hans from ClockMath coded for haze.yt / Lirium . - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited
 */

package me.felix.util.math;

public class ClockMath {

    public static double convertPercentToRadians(double percent) {
        return percent / 100.0 * Math.PI * 2 + Math.PI;
    }

}
