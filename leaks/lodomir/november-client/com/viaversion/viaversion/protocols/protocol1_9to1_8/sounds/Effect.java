/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_9to1_8.sounds;

import com.viaversion.viaversion.libs.fastutil.ints.Int2IntMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2IntOpenHashMap;

public class Effect {
    private static final Int2IntMap EFFECTS = new Int2IntOpenHashMap(19, 0.99f);

    public static int getNewId(int id) {
        return EFFECTS.getOrDefault(id, id);
    }

    public static boolean contains(int oldId) {
        return EFFECTS.containsKey(oldId);
    }

    private static void addRewrite(int oldId, int newId) {
        EFFECTS.put(oldId, newId);
    }

    static {
        Effect.addRewrite(1005, 1010);
        Effect.addRewrite(1003, 1005);
        Effect.addRewrite(1006, 1011);
        Effect.addRewrite(1004, 1009);
        Effect.addRewrite(1007, 1015);
        Effect.addRewrite(1008, 1016);
        Effect.addRewrite(1009, 1016);
        Effect.addRewrite(1010, 1019);
        Effect.addRewrite(1011, 1020);
        Effect.addRewrite(1012, 1021);
        Effect.addRewrite(1014, 1024);
        Effect.addRewrite(1015, 1025);
        Effect.addRewrite(1016, 1026);
        Effect.addRewrite(1017, 1027);
        Effect.addRewrite(1020, 1029);
        Effect.addRewrite(1021, 1030);
        Effect.addRewrite(1022, 1031);
        Effect.addRewrite(1013, 1023);
        Effect.addRewrite(1018, 1028);
    }
}

