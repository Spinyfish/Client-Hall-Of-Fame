/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_11to1_10.data;

import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;
import com.viaversion.viaversion.util.Pair;

public class PotionColorMapping {
    private static final Int2ObjectMap<Pair<Integer, Boolean>> POTIONS = new Int2ObjectOpenHashMap<Pair<Integer, Boolean>>(37, 0.99f);

    public static Pair<Integer, Boolean> getNewData(int oldData) {
        return (Pair)POTIONS.get(oldData);
    }

    private static void addRewrite(int oldData, int newData, boolean isInstant) {
        POTIONS.put(oldData, new Pair<Integer, Boolean>(newData, isInstant));
    }

    static {
        PotionColorMapping.addRewrite(0, 3694022, false);
        PotionColorMapping.addRewrite(1, 3694022, false);
        PotionColorMapping.addRewrite(2, 3694022, false);
        PotionColorMapping.addRewrite(3, 3694022, false);
        PotionColorMapping.addRewrite(4, 3694022, false);
        PotionColorMapping.addRewrite(5, 0x1F1FA1, false);
        PotionColorMapping.addRewrite(6, 0x1F1FA1, false);
        PotionColorMapping.addRewrite(7, 8356754, false);
        PotionColorMapping.addRewrite(8, 8356754, false);
        PotionColorMapping.addRewrite(9, 2293580, false);
        PotionColorMapping.addRewrite(10, 2293580, false);
        PotionColorMapping.addRewrite(11, 2293580, false);
        PotionColorMapping.addRewrite(12, 14981690, false);
        PotionColorMapping.addRewrite(13, 14981690, false);
        PotionColorMapping.addRewrite(14, 8171462, false);
        PotionColorMapping.addRewrite(15, 8171462, false);
        PotionColorMapping.addRewrite(16, 8171462, false);
        PotionColorMapping.addRewrite(17, 5926017, false);
        PotionColorMapping.addRewrite(18, 5926017, false);
        PotionColorMapping.addRewrite(19, 3035801, false);
        PotionColorMapping.addRewrite(20, 3035801, false);
        PotionColorMapping.addRewrite(21, 16262179, true);
        PotionColorMapping.addRewrite(22, 16262179, true);
        PotionColorMapping.addRewrite(23, 4393481, true);
        PotionColorMapping.addRewrite(24, 4393481, true);
        PotionColorMapping.addRewrite(25, 5149489, false);
        PotionColorMapping.addRewrite(26, 5149489, false);
        PotionColorMapping.addRewrite(27, 5149489, false);
        PotionColorMapping.addRewrite(28, 13458603, false);
        PotionColorMapping.addRewrite(29, 13458603, false);
        PotionColorMapping.addRewrite(30, 13458603, false);
        PotionColorMapping.addRewrite(31, 9643043, false);
        PotionColorMapping.addRewrite(32, 9643043, false);
        PotionColorMapping.addRewrite(33, 9643043, false);
        PotionColorMapping.addRewrite(34, 0x484D48, false);
        PotionColorMapping.addRewrite(35, 0x484D48, false);
        PotionColorMapping.addRewrite(36, 0x339900, false);
    }
}

