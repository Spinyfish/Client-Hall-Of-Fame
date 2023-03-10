/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.protocol.protocol1_10to1_11;

import com.viaversion.viaversion.libs.fastutil.ints.Int2IntMap;
import com.viaversion.viaversion.libs.fastutil.ints.Int2IntOpenHashMap;

public class PotionSplashHandler {
    private static final Int2IntMap DATA = new Int2IntOpenHashMap(14, 0.99f);

    public static int getOldData(int data) {
        return DATA.get(data);
    }

    static {
        DATA.defaultReturnValue(-1);
        DATA.put(0x1F1FA1, 5);
        DATA.put(8356754, 7);
        DATA.put(2293580, 9);
        DATA.put(14981690, 12);
        DATA.put(8171462, 14);
        DATA.put(5926017, 17);
        DATA.put(3035801, 19);
        DATA.put(16262179, 21);
        DATA.put(4393481, 23);
        DATA.put(5149489, 25);
        DATA.put(13458603, 28);
        DATA.put(9643043, 31);
        DATA.put(0x484D48, 34);
        DATA.put(0x339900, 36);
    }
}

