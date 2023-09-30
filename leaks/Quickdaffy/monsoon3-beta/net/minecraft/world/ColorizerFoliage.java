/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world;

public class ColorizerFoliage {
    private static int[] foliageBuffer = new int[65536];

    public static void setFoliageBiomeColorizer(int[] p_77467_0_) {
        foliageBuffer = p_77467_0_;
    }

    public static int getFoliageColor(double p_77470_0_, double p_77470_2_) {
        int i = (int)((1.0 - p_77470_0_) * 255.0);
        int j = (int)((1.0 - (p_77470_2_ *= p_77470_0_)) * 255.0);
        return foliageBuffer[j << 8 | i];
    }

    public static int getFoliageColorPine() {
        return 0x619961;
    }

    public static int getFoliageColorBirch() {
        return 8431445;
    }

    public static int getFoliageColorBasic() {
        return 4764952;
    }
}

