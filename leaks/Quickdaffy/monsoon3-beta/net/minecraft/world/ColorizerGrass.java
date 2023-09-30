/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world;

public class ColorizerGrass {
    private static int[] grassBuffer = new int[65536];

    public static void setGrassBiomeColorizer(int[] p_77479_0_) {
        grassBuffer = p_77479_0_;
    }

    public static int getGrassColor(double p_77480_0_, double p_77480_2_) {
        int j = (int)((1.0 - (p_77480_2_ *= p_77480_0_)) * 255.0);
        int i = (int)((1.0 - p_77480_0_) * 255.0);
        int k = j << 8 | i;
        return k > grassBuffer.length ? -65281 : grassBuffer[k];
    }
}

