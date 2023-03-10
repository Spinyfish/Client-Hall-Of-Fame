/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.protocol.protocol1_16_4to1_17.data;

import com.viaversion.viaversion.libs.fastutil.ints.Int2IntMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2IntOpenHashMap;

public final class MapColorRewrites {
    private static final Int2IntMap MAPPINGS = new Int2IntOpenHashMap();

    public static int getMappedColor(int color) {
        return MAPPINGS.getOrDefault(color, -1);
    }

    static {
        MAPPINGS.put(236, 85);
        MAPPINGS.put(237, 27);
        MAPPINGS.put(238, 45);
        MAPPINGS.put(239, 84);
        MAPPINGS.put(240, 144);
        MAPPINGS.put(241, 145);
        MAPPINGS.put(242, 146);
        MAPPINGS.put(243, 147);
        MAPPINGS.put(244, 127);
        MAPPINGS.put(245, 226);
        MAPPINGS.put(246, 124);
        MAPPINGS.put(247, 227);
    }
}

