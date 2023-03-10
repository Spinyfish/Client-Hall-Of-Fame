/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_18to1_17_1;

import com.viaversion.viaversion.api.Via;
import java.util.Arrays;

public final class BlockEntityIds {
    private static final int[] IDS = new int[14];

    public static int newId(int id) {
        int newId;
        if (id < 0 || id > IDS.length || (newId = IDS[id]) == -1) {
            Via.getPlatform().getLogger().warning("Received out of bounds block entity id: " + id);
            return -1;
        }
        return newId;
    }

    public static int[] getIds() {
        return IDS;
    }

    static {
        Arrays.fill(IDS, -1);
        BlockEntityIds.IDS[1] = 8;
        BlockEntityIds.IDS[2] = 21;
        BlockEntityIds.IDS[3] = 13;
        BlockEntityIds.IDS[4] = 14;
        BlockEntityIds.IDS[5] = 24;
        BlockEntityIds.IDS[6] = 18;
        BlockEntityIds.IDS[7] = 19;
        BlockEntityIds.IDS[8] = 20;
        BlockEntityIds.IDS[9] = 7;
        BlockEntityIds.IDS[10] = 22;
        BlockEntityIds.IDS[11] = 23;
        BlockEntityIds.IDS[12] = 30;
        BlockEntityIds.IDS[13] = 31;
    }
}

