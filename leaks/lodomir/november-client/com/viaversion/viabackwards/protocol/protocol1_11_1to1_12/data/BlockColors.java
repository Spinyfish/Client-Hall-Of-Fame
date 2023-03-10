/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.protocol.protocol1_11_1to1_12.data;

public class BlockColors {
    private static final String[] COLORS = new String[16];

    public static String get(int key) {
        return key >= 0 && key < COLORS.length ? COLORS[key] : "Unknown color";
    }

    static {
        BlockColors.COLORS[0] = "White";
        BlockColors.COLORS[1] = "Orange";
        BlockColors.COLORS[2] = "Magenta";
        BlockColors.COLORS[3] = "Light Blue";
        BlockColors.COLORS[4] = "Yellow";
        BlockColors.COLORS[5] = "Lime";
        BlockColors.COLORS[6] = "Pink";
        BlockColors.COLORS[7] = "Gray";
        BlockColors.COLORS[8] = "Light Gray";
        BlockColors.COLORS[9] = "Cyan";
        BlockColors.COLORS[10] = "Purple";
        BlockColors.COLORS[11] = "Blue";
        BlockColors.COLORS[12] = "Brown";
        BlockColors.COLORS[13] = "Green";
        BlockColors.COLORS[14] = "Red";
        BlockColors.COLORS[15] = "Black";
    }
}

