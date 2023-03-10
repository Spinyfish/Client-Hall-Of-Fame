/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.data;

import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2ObjectOpenHashMap;

public class PaintingMapping {
    private static final Int2ObjectMap<String> PAINTINGS = new Int2ObjectOpenHashMap<String>(26, 0.99f);

    public static void init() {
        PaintingMapping.add("Kebab");
        PaintingMapping.add("Aztec");
        PaintingMapping.add("Alban");
        PaintingMapping.add("Aztec2");
        PaintingMapping.add("Bomb");
        PaintingMapping.add("Plant");
        PaintingMapping.add("Wasteland");
        PaintingMapping.add("Pool");
        PaintingMapping.add("Courbet");
        PaintingMapping.add("Sea");
        PaintingMapping.add("Sunset");
        PaintingMapping.add("Creebet");
        PaintingMapping.add("Wanderer");
        PaintingMapping.add("Graham");
        PaintingMapping.add("Match");
        PaintingMapping.add("Bust");
        PaintingMapping.add("Stage");
        PaintingMapping.add("Void");
        PaintingMapping.add("SkullAndRoses");
        PaintingMapping.add("Wither");
        PaintingMapping.add("Fighters");
        PaintingMapping.add("Pointer");
        PaintingMapping.add("Pigscene");
        PaintingMapping.add("BurningSkull");
        PaintingMapping.add("Skeleton");
        PaintingMapping.add("DonkeyKong");
    }

    private static void add(String motive) {
        PAINTINGS.put(PAINTINGS.size(), motive);
    }

    public static String getStringId(int id) {
        return PAINTINGS.getOrDefault(id, "kebab");
    }
}

